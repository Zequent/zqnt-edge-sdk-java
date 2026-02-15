package com.zequent.framework.edge.sdk.interfaces.impl;

import com.zequent.framework.edge.sdk.interfaces.LiveDataService;
import com.zequent.framework.edge.sdk.mapper.TelemetryMapper;
import com.zequent.framework.services.livedata.proto.MutinyLiveDataServiceGrpc;
import com.zequent.framework.services.livedata.proto.ProduceTelemetryRequest;
import com.zequent.framework.edge.sdk.models.TelemetryRequestData;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation of LiveDataController that manages persistent gRPC streams
 * per device using BroadcastProcessor for high-performance telemetry streaming.
 * Supports both POJO-based and Proto-based APIs for flexibility.
 * Note: This class is not a CDI bean itself. It should be created via a @Produces method
 * in a configuration class that properly injects the required dependencies.
 */
@Slf4j
public class LiveDataServiceImpl implements LiveDataService {

	private final TelemetryMapper telemetryMapper;
	private final Map<String, BroadcastProcessor<ProduceTelemetryRequest>> processors = new ConcurrentHashMap<>();
	private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

	private final MutinyLiveDataServiceGrpc.MutinyLiveDataServiceStub liveDataServiceStub;

	public LiveDataServiceImpl(TelemetryMapper telemetryMapper,
							   MutinyLiveDataServiceGrpc.MutinyLiveDataServiceStub liveDataServiceStub) {
		this.telemetryMapper = telemetryMapper;
		this.liveDataServiceStub = liveDataServiceStub;
	}

	@PreDestroy
	public void onDestroy() {
		log.info("LiveDataController shutdown detected, cleaning up gRPC streams");
		shuttingDown.set(true);
		closeAllStreams().await().atMost(Duration.ofSeconds(10));
	}


	@Override
	public Uni<Void> produceTelemetryData(TelemetryRequestData requestData) {
		if (requestData == null) {
			return Uni.createFrom().voidItem();
		}

		var request = telemetryMapper.map(requestData);
		return produceTelemetry(requestData.getSn(), request);
	}

	// ========== Proto-based API Implementation ==========

	@Override
	public Uni<Void> produceTelemetry(String deviceSn, ProduceTelemetryRequest telemetryRequest) {
		if (shuttingDown.get()) {
			log.warn("Cannot produce telemetry for device {} - controller is shutting down", deviceSn);
			return Uni.createFrom().voidItem();
		}

		return Uni.createFrom().item(() -> {
			var processor = getOrCreateProcessor(deviceSn);
			if (processor != null) {
				processor.onNext(telemetryRequest);
			}
			return null;
		});
	}

	@Override
	public Uni<Void> closeStream(String deviceSn) {
		return Uni.createFrom().item(() -> {
			var processor = processors.remove(deviceSn);
			if (processor != null) {
				try {
					processor.onComplete();
					log.info("Closed telemetry stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error closing stream for device {}: {}", deviceSn, e.getMessage());
				}
			}
			return null;
		});
	}

	@Override
	public Uni<Void> closeAllStreams() {
		return Uni.createFrom().item(() -> {
			log.info("Closing all telemetry streams ({} active)", processors.size());
			processors.forEach((deviceSn, processor) -> {
				try {
					processor.onComplete();
					log.debug("Completed stream for device: {}", deviceSn);
				} catch (Exception e) {
					log.warn("Error completing stream for device {}: {}", deviceSn, e.getMessage());
				}
			});
			processors.clear();
			log.info("All telemetry streams closed");
			return null;
		});
	}

	private BroadcastProcessor<ProduceTelemetryRequest> getOrCreateProcessor(String deviceSn) {
		if (shuttingDown.get()) {
			log.warn("Cannot create processor for device {} - controller is shutting down", deviceSn);
			return null;
		}

		return processors.computeIfAbsent(deviceSn, sn -> {
			var processor = BroadcastProcessor.<ProduceTelemetryRequest>create();
			subscribeProcessorToGrpc(sn, processor);
			return processor;
		});
	}

	private void subscribeProcessorToGrpc(String deviceSn, BroadcastProcessor<ProduceTelemetryRequest> processor) {
		liveDataServiceStub.produceTelemetry(processor)
				.onFailure(this::isNotShutdownError)
					.invoke(e -> log.error("gRPC stream failed for device {}: {}", deviceSn, e.getMessage()))
				.onFailure(this::isNotShutdownError)
					.retry()
					.withBackOff(Duration.ofSeconds(1), Duration.ofSeconds(30))
					.withJitter(0.2)
					.atMost(10)
				.subscribe().with(
						response -> log.debug("Telemetry response received for device {} :  {}", deviceSn, response),
						failure -> {
							if (!shuttingDown.get()) {
								log.error("Telemetry stream terminated for device {} after retries: {}",
										deviceSn, failure.getMessage());
								processors.remove(deviceSn);
								scheduleReconnect(deviceSn, processor);
							}
						}
				);
		log.info("Started gRPC telemetry stream for device {}", deviceSn);
	}

	private boolean isNotShutdownError(Throwable t) {
		if (shuttingDown.get()) {
			return false;
		}
		String msg = t.getMessage();
		return msg == null ||
				!(msg.contains("shutdown") ||
						msg.contains("UNAVAILABLE") && msg.contains("Channel"));
	}

	private void scheduleReconnect(String deviceSn, BroadcastProcessor<ProduceTelemetryRequest> processor) {
		if (shuttingDown.get()) {
			return;
		}

		Uni.createFrom().item(() -> processor)
				.onItem().delayIt().by(Duration.ofSeconds(30))
				.subscribe().with(
						p -> {
							if (!shuttingDown.get()) {
								log.info("Attempting to reconnect gRPC stream for device {}", deviceSn);
								processors.put(deviceSn, p);
								subscribeProcessorToGrpc(deviceSn, p);
							}
						}
				);
	}
}
