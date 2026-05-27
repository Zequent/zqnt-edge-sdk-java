package com.zqnt.sdk.edge.livedata.application.impl;

import com.zqnt.sdk.edge.adapter.domains.DetectionRequestData;
import com.zqnt.sdk.edge.adapter.domains.NotificationRequestData;
import com.zqnt.sdk.edge.adapter.domains.TelemetryRequestData;
import com.zqnt.sdk.edge.livedata.application.DetectionMapper;
import com.zqnt.sdk.edge.livedata.application.LiveDataService;
import com.zqnt.sdk.edge.livedata.application.NotificationMapper;
import com.zqnt.sdk.edge.livedata.application.TelemetryMapper;

import com.zqnt.utils.common.proto.DetectionBatch;
import com.zqnt.utils.livedata.proto.LiveDataResponse;
import com.zqnt.utils.livedata.proto.LiveDataServiceGrpc;
import com.zqnt.utils.livedata.proto.ProduceNotificationRequest;
import com.zqnt.utils.livedata.proto.ProduceTelemetryRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Standard gRPC implementation of LiveDataService that manages persistent gRPC streams
 * per device for high-performance telemetry streaming.
 */
@Slf4j
public class LiveDataServiceImpl implements LiveDataService {

	private final TelemetryMapper telemetryMapper;
	private final DetectionMapper detectionMapper;
	private final NotificationMapper notificationMapper;
	private final LiveDataServiceGrpc.LiveDataServiceStub liveDataServiceStub;
	private static final int INITIAL_RECONNECT_DELAY_SECONDS = 2;
	private static final int MAX_RECONNECT_DELAY_SECONDS = 60;
	private static final int MAX_RECONNECT_ATTEMPTS = 100;

	// Telemetry streams
	private final Map<String, StreamObserver<ProduceTelemetryRequest>> activeStreams = new ConcurrentHashMap<>();
	private final Map<String, CompletableFuture<Void>> streamFutures = new ConcurrentHashMap<>();
	private final Map<String, AtomicInteger> reconnectAttempts = new ConcurrentHashMap<>();

	// Detection streams
	private final Map<String, StreamObserver<DetectionBatch>> activeDetectionStreams = new ConcurrentHashMap<>();
	private final Map<String, CompletableFuture<Void>> detectionStreamFutures = new ConcurrentHashMap<>();
	private final Map<String, AtomicInteger> detectionReconnectAttempts = new ConcurrentHashMap<>();

	// Notification streams
	private final Map<String, StreamObserver<ProduceNotificationRequest>> activeNotificationStreams = new ConcurrentHashMap<>();
	private final Map<String, CompletableFuture<Void>> notificationStreamFutures = new ConcurrentHashMap<>();
	private final Map<String, AtomicInteger> notificationReconnectAttempts = new ConcurrentHashMap<>();

	private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
	private final ScheduledExecutorService reconnectScheduler = Executors.newScheduledThreadPool(1);

	public LiveDataServiceImpl(TelemetryMapper telemetryMapper,
							   DetectionMapper detectionMapper,
							   NotificationMapper notificationMapper,
							   LiveDataServiceGrpc.LiveDataServiceStub liveDataServiceStub) {
		this.telemetryMapper = telemetryMapper;
		this.detectionMapper = detectionMapper;
		this.notificationMapper = notificationMapper;
		this.liveDataServiceStub = liveDataServiceStub;
	}

	/**
	 * Cleanup method - call this when shutting down
	 */
	public void shutdown() {
		log.info("LiveDataService shutdown initiated, cleaning up gRPC streams");
		shuttingDown.set(true);
		closeAllStreams();
		reconnectScheduler.shutdown();
		try {
			if (!reconnectScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				reconnectScheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			reconnectScheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	// -------------------------------------------------------------------------
	// Detection
	// -------------------------------------------------------------------------

	@Override
	public CompletableFuture<Void> produceDetectionData(DetectionRequestData requestData) {
		if (requestData == null) {
			return CompletableFuture.completedFuture(null);
		}
		var request = detectionMapper.map(requestData);
		return produceDetection(requestData.getSn(), request);
	}

	@Override
	public CompletableFuture<Void> produceDetection(String deviceSn, DetectionBatch detectionBatch) {
		if (shuttingDown.get()) {
			log.warn("Cannot produce detection for device {} - service is shutting down", deviceSn);
			return CompletableFuture.completedFuture(null);
		}

		StreamObserver<DetectionBatch> stream = getOrCreateDetectionStream(deviceSn);
		if (stream == null) {
			return CompletableFuture.completedFuture(null);
		}

		try {
			stream.onNext(detectionBatch);
			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			log.error("Error sending detection for device {}: {}", deviceSn, e.getMessage());
			activeDetectionStreams.remove(deviceSn);
			return CompletableFuture.failedFuture(e);
		}
	}

	@Override
	public CompletableFuture<Void> closeDetectionStream(String deviceSn) {
		return CompletableFuture.runAsync(() -> {
			StreamObserver<DetectionBatch> stream = activeDetectionStreams.remove(deviceSn);
			CompletableFuture<Void> future = detectionStreamFutures.remove(deviceSn);

			if (stream != null) {
				try {
					stream.onCompleted();
					log.info("Closed detection stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error closing detection stream for device {}: {}", deviceSn, e.getMessage());
				}
			}

			if (future != null) {
				future.complete(null);
			}
		});
	}

	private StreamObserver<DetectionBatch> getOrCreateDetectionStream(String deviceSn) {
		if (shuttingDown.get()) {
			log.warn("Cannot create detection stream for device {} - service is shutting down", deviceSn);
			return null;
		}

		return activeDetectionStreams.computeIfAbsent(deviceSn, sn -> {
			CompletableFuture<Void> streamFuture = new CompletableFuture<>();
			detectionStreamFutures.put(sn, streamFuture);
			return createDetectionGrpcStream(sn, streamFuture);
		});
	}

	private StreamObserver<DetectionBatch> createDetectionGrpcStream(
			String deviceSn,
			CompletableFuture<Void> streamFuture) {

		log.info("Creating gRPC detection stream for device {}", deviceSn);

		StreamObserver<LiveDataResponse> responseObserver = new StreamObserver<>() {
			@Override
			public void onNext(LiveDataResponse response) {
				log.debug("Detection response received for device {}", deviceSn);
			}

			@Override
			public void onError(Throwable t) {
				log.error("gRPC detection stream error for device {}: {}", deviceSn, t.getMessage(), t);
				activeDetectionStreams.remove(deviceSn);
				detectionStreamFutures.remove(deviceSn);
				detectionReconnectAttempts.computeIfAbsent(deviceSn, key -> new AtomicInteger(0));

				if (!shuttingDown.get() && shouldReconnect(t)) {
					int attempts = detectionReconnectAttempts.get(deviceSn).incrementAndGet();
					if (attempts <= MAX_RECONNECT_ATTEMPTS) {
						scheduleDetectionReconnect(deviceSn, computeNextDelay(attempts));
					} else {
						log.warn("Max detection reconnect attempts reached for device {}. Will not retry until manual recovery.", deviceSn);
					}
				}
				streamFuture.completeExceptionally(t);
			}

			@Override
			public void onCompleted() {
				log.info("gRPC detection stream completed for device {}", deviceSn);
				activeDetectionStreams.remove(deviceSn);
				detectionStreamFutures.remove(deviceSn);
				streamFuture.complete(null);
			}
		};

		return liveDataServiceStub.produceDetection(responseObserver);
	}

	private void scheduleDetectionReconnect(String deviceSn, int delaySeconds) {
		if (shuttingDown.get()) return;

		log.info("Scheduling detection reconnection for device {} in {} seconds (attempt {})",
				deviceSn, delaySeconds, detectionReconnectAttempts.getOrDefault(deviceSn, new AtomicInteger(0)).get());

		reconnectScheduler.schedule(() -> {
			if (shuttingDown.get() || activeDetectionStreams.containsKey(deviceSn)) return;

			log.info("Attempting to reconnect gRPC detection stream for device {}", deviceSn);
			try {
				CompletableFuture<Void> newFuture = new CompletableFuture<>();
				StreamObserver<DetectionBatch> newStream = createDetectionGrpcStream(deviceSn, newFuture);
				activeDetectionStreams.put(deviceSn, newStream);
				detectionStreamFutures.put(deviceSn, newFuture);
				detectionReconnectAttempts.remove(deviceSn);
			} catch (Exception e) {
				int attempts = detectionReconnectAttempts.getOrDefault(deviceSn, new AtomicInteger(1)).get();
				if (attempts < MAX_RECONNECT_ATTEMPTS) {
					scheduleDetectionReconnect(deviceSn, computeNextDelay(attempts + 1));
				} else {
					log.error("Exhausted detection reconnect attempts for device {} after {} tries", deviceSn, attempts, e);
				}
			}
		}, delaySeconds, TimeUnit.SECONDS);
	}

	// -------------------------------------------------------------------------
	// Notification
	// -------------------------------------------------------------------------

	@Override
	public CompletableFuture<Void> produceNotificationData(NotificationRequestData requestData) {
		if (requestData == null) {
			return CompletableFuture.completedFuture(null);
		}
		var request = notificationMapper.map(requestData);
		return produceNotification(requestData.getSn(), request);
	}

	@Override
	public CompletableFuture<Void> produceNotification(String deviceSn, ProduceNotificationRequest notificationRequest) {
		if (shuttingDown.get()) {
			log.warn("Cannot produce notification for device {} - service is shutting down", deviceSn);
			return CompletableFuture.completedFuture(null);
		}

		StreamObserver<ProduceNotificationRequest> stream = getOrCreateNotificationStream(deviceSn);
		if (stream == null) {
			return CompletableFuture.completedFuture(null);
		}

		try {
			stream.onNext(notificationRequest);
			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			log.error("Error sending notification for device {}: {}", deviceSn, e.getMessage());
			activeNotificationStreams.remove(deviceSn);
			return CompletableFuture.failedFuture(e);
		}
	}

	@Override
	public CompletableFuture<Void> closeNotificationStream(String deviceSn) {
		return CompletableFuture.runAsync(() -> {
			StreamObserver<ProduceNotificationRequest> stream = activeNotificationStreams.remove(deviceSn);
			CompletableFuture<Void> future = notificationStreamFutures.remove(deviceSn);

			if (stream != null) {
				try {
					stream.onCompleted();
					log.info("Closed notification stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error closing notification stream for device {}: {}", deviceSn, e.getMessage());
				}
			}

			if (future != null) {
				future.complete(null);
			}
		});
	}

	private StreamObserver<ProduceNotificationRequest> getOrCreateNotificationStream(String deviceSn) {
		if (shuttingDown.get()) {
			log.warn("Cannot create notification stream for device {} - service is shutting down", deviceSn);
			return null;
		}

		return activeNotificationStreams.computeIfAbsent(deviceSn, sn -> {
			CompletableFuture<Void> streamFuture = new CompletableFuture<>();
			notificationStreamFutures.put(sn, streamFuture);
			return createNotificationGrpcStream(sn, streamFuture);
		});
	}

	private StreamObserver<ProduceNotificationRequest> createNotificationGrpcStream(
			String deviceSn,
			CompletableFuture<Void> streamFuture) {

		log.info("Creating gRPC notification stream for device {}", deviceSn);

		StreamObserver<LiveDataResponse> responseObserver = new StreamObserver<>() {
			@Override
			public void onNext(LiveDataResponse response) {
				log.debug("Notification response received for device {}", deviceSn);
			}

			@Override
			public void onError(Throwable t) {
				log.error("gRPC notification stream error for device {}: {}", deviceSn, t.getMessage(), t);
				activeNotificationStreams.remove(deviceSn);
				notificationStreamFutures.remove(deviceSn);
				notificationReconnectAttempts.computeIfAbsent(deviceSn, key -> new AtomicInteger(0));

				if (!shuttingDown.get() && shouldReconnect(t)) {
					int attempts = notificationReconnectAttempts.get(deviceSn).incrementAndGet();
					if (attempts <= MAX_RECONNECT_ATTEMPTS) {
						scheduleNotificationReconnect(deviceSn, computeNextDelay(attempts));
					} else {
						log.warn("Max notification reconnect attempts reached for device {}. Will not retry until manual recovery.", deviceSn);
					}
				}
				streamFuture.completeExceptionally(t);
			}

			@Override
			public void onCompleted() {
				log.info("gRPC notification stream completed for device {}", deviceSn);
				activeNotificationStreams.remove(deviceSn);
				notificationStreamFutures.remove(deviceSn);
				streamFuture.complete(null);
			}
		};

		return liveDataServiceStub.produceNotification(responseObserver);
	}

	private void scheduleNotificationReconnect(String deviceSn, int delaySeconds) {
		if (shuttingDown.get()) return;

		log.info("Scheduling notification reconnection for device {} in {} seconds (attempt {})",
				deviceSn, delaySeconds, notificationReconnectAttempts.getOrDefault(deviceSn, new AtomicInteger(0)).get());

		reconnectScheduler.schedule(() -> {
			if (shuttingDown.get() || activeNotificationStreams.containsKey(deviceSn)) return;

			log.info("Attempting to reconnect gRPC notification stream for device {}", deviceSn);
			try {
				CompletableFuture<Void> newFuture = new CompletableFuture<>();
				StreamObserver<ProduceNotificationRequest> newStream = createNotificationGrpcStream(deviceSn, newFuture);
				activeNotificationStreams.put(deviceSn, newStream);
				notificationStreamFutures.put(deviceSn, newFuture);
				notificationReconnectAttempts.remove(deviceSn);
			} catch (Exception e) {
				int attempts = notificationReconnectAttempts.getOrDefault(deviceSn, new AtomicInteger(1)).get();
				if (attempts < MAX_RECONNECT_ATTEMPTS) {
					scheduleNotificationReconnect(deviceSn, computeNextDelay(attempts + 1));
				} else {
					log.error("Exhausted notification reconnect attempts for device {} after {} tries", deviceSn, attempts, e);
				}
			}
		}, delaySeconds, TimeUnit.SECONDS);
	}

	// -------------------------------------------------------------------------
	// Telemetry
	// -------------------------------------------------------------------------

	@Override
	public CompletableFuture<Void> produceTelemetryData(TelemetryRequestData requestData) {
		if (requestData == null) {
			return CompletableFuture.completedFuture(null);
		}

		// Perform mapping in the current (CDI) context before going async
		var request = telemetryMapper.map(requestData);
		return produceTelemetry(requestData.getSn(), request);
	}

	@Override
	public CompletableFuture<Void> produceTelemetry(String deviceSn, ProduceTelemetryRequest telemetryRequest) {
		if (shuttingDown.get()) {
			log.warn("Cannot produce telemetry for device {} - service is shutting down", deviceSn);
			return CompletableFuture.completedFuture(null);
		}

		// Get or create stream in current thread context
		StreamObserver<ProduceTelemetryRequest> stream = getOrCreateStream(deviceSn);
		if (stream == null) {
			return CompletableFuture.completedFuture(null);
		}

		// Send telemetry - this is lightweight and doesn't need to be async
		try {
			stream.onNext(telemetryRequest);
			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			log.error("Error sending telemetry for device {}: {}", deviceSn, e.getMessage());
			// Stream might be broken, remove it
			activeStreams.remove(deviceSn);
			return CompletableFuture.failedFuture(e);
		}
	}

	@Override
	public CompletableFuture<Void> closeStream(String deviceSn) {
		return CompletableFuture.runAsync(() -> {
			StreamObserver<ProduceTelemetryRequest> stream = activeStreams.remove(deviceSn);
			CompletableFuture<Void> future = streamFutures.remove(deviceSn);

			if (stream != null) {
				try {
					stream.onCompleted();
					log.info("Closed telemetry stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error closing stream for device {}: {}", deviceSn, e.getMessage());
				}
			}

			if (future != null) {
				future.complete(null);
			}
		});
	}

	@Override
	public CompletableFuture<Void> closeAllStreams() {
		return CompletableFuture.runAsync(() -> {
			log.info("Closing all streams (telemetry: {}, detection: {}, notification: {})",
					activeStreams.size(), activeDetectionStreams.size(), activeNotificationStreams.size());

			activeStreams.forEach((deviceSn, stream) -> {
				try {
					stream.onCompleted();
					log.debug("Completed telemetry stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error completing telemetry stream for device {}: {}", deviceSn, e.getMessage());
				}
			});
			activeStreams.clear();
			streamFutures.values().forEach(f -> f.complete(null));
			streamFutures.clear();

			activeDetectionStreams.forEach((deviceSn, stream) -> {
				try {
					stream.onCompleted();
					log.debug("Completed detection stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error completing detection stream for device {}: {}", deviceSn, e.getMessage());
				}
			});
			activeDetectionStreams.clear();
			detectionStreamFutures.values().forEach(f -> f.complete(null));
			detectionStreamFutures.clear();

			activeNotificationStreams.forEach((deviceSn, stream) -> {
				try {
					stream.onCompleted();
					log.debug("Completed notification stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error completing notification stream for device {}: {}", deviceSn, e.getMessage());
				}
			});
			activeNotificationStreams.clear();
			notificationStreamFutures.values().forEach(f -> f.complete(null));
			notificationStreamFutures.clear();

			log.info("All streams closed");
		});
	}

	private StreamObserver<ProduceTelemetryRequest> getOrCreateStream(String deviceSn) {
		if (shuttingDown.get()) {
			log.warn("Cannot create stream for device {} - service is shutting down", deviceSn);
			return null;
		}

		return activeStreams.computeIfAbsent(deviceSn, sn -> {
			CompletableFuture<Void> streamFuture = new CompletableFuture<>();
			streamFutures.put(sn, streamFuture);
			return createGrpcStream(sn, streamFuture);
		});
	}

	private StreamObserver<ProduceTelemetryRequest> createGrpcStream(
			String deviceSn,
			CompletableFuture<Void> streamFuture) {

		log.info("Creating gRPC telemetry stream for device {}", deviceSn);

		StreamObserver<LiveDataResponse> responseObserver = new StreamObserver<LiveDataResponse>() {
			@Override
			public void onNext(LiveDataResponse response) {
				log.debug("Telemetry response received for device {}", deviceSn);
			}

			@Override
			public void onError(Throwable t) {
				log.error("gRPC stream error for device {}: {}", deviceSn, t.getMessage(), t);
				activeStreams.remove(deviceSn);
				streamFutures.remove(deviceSn);
				reconnectAttempts.computeIfAbsent(deviceSn, key -> new AtomicInteger(0));

				if (!shuttingDown.get() && shouldReconnect(t)) {
					int attempts = reconnectAttempts.get(deviceSn).incrementAndGet();
					if (attempts <= MAX_RECONNECT_ATTEMPTS) {
						int delay = computeNextDelay(attempts);
						scheduleReconnect(deviceSn, delay);
					} else {
						log.warn("Max reconnect attempts reached for device {} ({}). Will not retry until manual recovery.", deviceSn, attempts);
					}
				}
				streamFuture.completeExceptionally(t);
			}

			@Override
			public void onCompleted() {
				log.info("gRPC stream completed for device {}", deviceSn);
				activeStreams.remove(deviceSn);
				streamFutures.remove(deviceSn);
				streamFuture.complete(null);
			}
		};

		return liveDataServiceStub.produceTelemetry(responseObserver);
	}

	private boolean shouldReconnect(Throwable t) {
		Status status = Status.fromThrowable(t);
		if (status == null) {
			return true;
		}
		return switch (status.getCode()) {
			case UNAVAILABLE,
				DEADLINE_EXCEEDED,
				RESOURCE_EXHAUSTED,
				INTERNAL,
				UNKNOWN -> true;
			case UNAUTHENTICATED,
				PERMISSION_DENIED,
				FAILED_PRECONDITION,
				UNIMPLEMENTED,
				DATA_LOSS -> false;
			default -> true;
		};
	}

	private int computeNextDelay(int attempts) {
		int next = INITIAL_RECONNECT_DELAY_SECONDS * (1 << Math.min(attempts - 1, 6)); // max 128x base
		next = Math.min(next, MAX_RECONNECT_DELAY_SECONDS);
		int jitter = ThreadLocalRandom.current().nextInt(0, Math.max(1, next / 4));
		return next + jitter;
	}

	private void scheduleReconnect(String deviceSn, int delaySeconds) {
		if (shuttingDown.get()) {
			return;
		}

		log.info("Scheduling reconnection for device {} in {} seconds (attempt {})", deviceSn, delaySeconds,
									reconnectAttempts.getOrDefault(deviceSn, new AtomicInteger(0)).get());

		reconnectScheduler.schedule(() -> {
			if (shuttingDown.get() || activeStreams.containsKey(deviceSn)) {
				return;
			}

			log.info("Attempting to reconnect gRPC stream for device {}", deviceSn);
			try {
				CompletableFuture<Void> newFuture = new CompletableFuture<>();
				StreamObserver<ProduceTelemetryRequest> newStream = createGrpcStream(deviceSn, newFuture);
				activeStreams.put(deviceSn, newStream);
				streamFutures.put(deviceSn, newFuture);
				reconnectAttempts.remove(deviceSn);
			} catch (Exception e) {
				int attempts = reconnectAttempts.getOrDefault(deviceSn, new AtomicInteger(1)).get();
				if (attempts < MAX_RECONNECT_ATTEMPTS) {
					int nextDelay = computeNextDelay(attempts + 1);
					log.warn("Failed to reconnect stream for device {}: {}. retry in {}s", deviceSn, e.getMessage(), nextDelay, e);
					scheduleReconnect(deviceSn, nextDelay);
				} else {
					log.error("Exhausted reconnect attempts for device {} after {} tries", deviceSn, attempts, e);
				}
			}
		}, delaySeconds, TimeUnit.SECONDS);
	}
}
