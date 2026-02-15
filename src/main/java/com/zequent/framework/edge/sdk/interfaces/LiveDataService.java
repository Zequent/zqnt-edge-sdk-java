package com.zequent.framework.edge.sdk.interfaces;

import com.zequent.framework.services.livedata.proto.ProduceTelemetryRequest;
import com.zequent.framework.edge.sdk.models.TelemetryRequestData;
import io.smallrye.mutiny.Uni;

/**
 * Controller interface for managing live telemetry data streams.
 * Implementations handle the gRPC communication and stream management.
 * Provides both POJO-based (recommended) and Proto-based (advanced) APIs.
 */
public interface LiveDataService {



	/**
	 * Produces telemetry data using complete request wrapper.
	 * Allows full control over request metadata.
	 *
	 * @param requestData The complete telemetry request data (POJO)
	 * @return Uni<Void> that completes when data is queued for sending
	 */
	Uni<Void> produceTelemetryData(TelemetryRequestData requestData);

	// ========== Proto-based API (Advanced Users) ==========

	/**
	 * Produces telemetry data using Proto message directly.
	 * For advanced users who need direct Proto control.
	 * Uses a persistent stream per device to minimize overhead.
	 *
	 * @param deviceSn The serial number of the device
	 * @param telemetryRequest The telemetry data (Proto)
	 * @return Uni<Void> that completes when data is queued for sending
	 * */

	Uni<Void> produceTelemetry(String deviceSn, ProduceTelemetryRequest telemetryRequest);

	// ========== Stream Management ==========

	/**
	 * Closes the telemetry stream for a specific device.
	 *
	 * @param deviceSn The serial number of the device
	 * @return Uni<Void> that completes when stream is closed
	 */
	Uni<Void> closeStream(String deviceSn);

	/**
	 * Closes all active telemetry streams.
	 * Should be called during application shutdown.
	 *
	 * @return Uni<Void> that completes when all streams are closed
	 */
	Uni<Void> closeAllStreams();
}
