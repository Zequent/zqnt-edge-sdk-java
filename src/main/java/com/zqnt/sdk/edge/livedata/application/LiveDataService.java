package com.zqnt.sdk.edge.livedata.application;

import com.zequent.framework.services.livedata.proto.ProduceTelemetryRequest;
import com.zqnt.sdk.edge.adapter.domains.TelemetryRequestData;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing live telemetry data streams.
 * Implementations handle the gRPC communication and stream management.
 * Provides both POJO-based (recommended) and Proto-based (advanced) APIs.
 */
public interface LiveDataService {

	/**
	 * Produces telemetry data using complete request wrapper.
	 * Allows full control over request metadata.
	 *
	 * @param requestData The complete telemetry request data (POJO)
	 * @return CompletableFuture that completes when data is queued for sending
	 */
	CompletableFuture<Void> produceTelemetryData(TelemetryRequestData requestData);

	/**
	 * Produces telemetry data using Proto message directly.
	 * For advanced users who need direct Proto control.
	 * Uses a persistent stream per device to minimize overhead.
	 *
	 * @param deviceSn The serial number of the device
	 * @param telemetryRequest The telemetry data (Proto)
	 * @return CompletableFuture that completes when data is queued for sending
	 */
	CompletableFuture<Void> produceTelemetry(String deviceSn, ProduceTelemetryRequest telemetryRequest);

	/**
	 * Closes the telemetry stream for a specific device.
	 *
	 * @param deviceSn The serial number of the device
	 * @return CompletableFuture that completes when stream is closed
	 */
	CompletableFuture<Void> closeStream(String deviceSn);

	/**
	 * Closes all active telemetry streams.
	 * Should be called during application shutdown.
	 *
	 * @return CompletableFuture that completes when all streams are closed
	 */
	CompletableFuture<Void> closeAllStreams();
}
