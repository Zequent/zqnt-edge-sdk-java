package com.zequent.framework.edge.sdk.interfaces.impl;

import com.zequent.framework.edge.sdk.interfaces.LiveDataService;
import com.zequent.framework.edge.sdk.mapper.TelemetryMapper;
import com.zequent.framework.services.livedata.proto.LiveDataResponse;
import com.zequent.framework.services.livedata.proto.LiveDataServiceGrpc;
import com.zequent.framework.services.livedata.proto.ProduceTelemetryRequest;
import com.zequent.framework.edge.sdk.models.TelemetryRequestData;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Standard gRPC implementation of LiveDataService that manages persistent gRPC streams
 * per device for high-performance telemetry streaming.
 */
@Slf4j
public class LiveDataServiceImpl implements LiveDataService {

	private final TelemetryMapper telemetryMapper;
	private final LiveDataServiceGrpc.LiveDataServiceStub liveDataServiceStub;
	private final Map<String, StreamObserver<ProduceTelemetryRequest>> activeStreams = new ConcurrentHashMap<>();
	private final Map<String, CompletableFuture<Void>> streamFutures = new ConcurrentHashMap<>();
	private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
	private final ScheduledExecutorService reconnectScheduler = Executors.newScheduledThreadPool(1);

	public LiveDataServiceImpl(TelemetryMapper telemetryMapper,
							   LiveDataServiceGrpc.LiveDataServiceStub liveDataServiceStub) {
		this.telemetryMapper = telemetryMapper;
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
			log.info("Closing all telemetry streams ({} active)", activeStreams.size());

			activeStreams.forEach((deviceSn, stream) -> {
				try {
					stream.onCompleted();
					log.debug("Completed stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error completing stream for device {}: {}", deviceSn, e.getMessage());
				}
			});

			activeStreams.clear();
			streamFutures.values().forEach(f -> f.complete(null));
			streamFutures.clear();

			log.info("All telemetry streams closed");
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
				if (!shuttingDown.get() && !isShutdownError(t)) {
					log.error("gRPC stream error for device {}: {}", deviceSn, t.getMessage());
					activeStreams.remove(deviceSn);
					streamFutures.remove(deviceSn);

					// Schedule reconnection
					scheduleReconnect(deviceSn, 5);
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

	private boolean isShutdownError(Throwable t) {
		if (shuttingDown.get()) {
			return true;
		}
		String msg = t.getMessage();
		return msg != null && (
			msg.contains("shutdown") ||
			msg.contains("UNAVAILABLE") && msg.contains("Channel")
		);
	}

	private void scheduleReconnect(String deviceSn, int delaySeconds) {
		if (shuttingDown.get()) {
			return;
		}

		log.info("Scheduling reconnection for device {} in {} seconds", deviceSn, delaySeconds);

		reconnectScheduler.schedule(() -> {
			if (!shuttingDown.get() && !activeStreams.containsKey(deviceSn)) {
				log.info("Attempting to reconnect gRPC stream for device {}", deviceSn);
				try {
					CompletableFuture<Void> newFuture = new CompletableFuture<>();
					StreamObserver<ProduceTelemetryRequest> newStream = createGrpcStream(deviceSn, newFuture);
					activeStreams.put(deviceSn, newStream);
					streamFutures.put(deviceSn, newFuture);
				} catch (Exception e) {
					log.error("Failed to reconnect stream for device {}: {}", deviceSn, e.getMessage());
					// Retry with exponential backoff
					int nextDelay = Math.min(delaySeconds * 2, 60);
					scheduleReconnect(deviceSn, nextDelay);
				}
			}
		}, delaySeconds, TimeUnit.SECONDS);
	}
}
