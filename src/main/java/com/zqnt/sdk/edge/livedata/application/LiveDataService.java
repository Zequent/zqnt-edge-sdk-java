package com.zqnt.sdk.edge.livedata.application;

import com.zqnt.sdk.edge.adapter.domains.DetectionRequestData;
import com.zqnt.sdk.edge.adapter.domains.NotificationRequestData;
import com.zqnt.sdk.edge.adapter.domains.TelemetryRequestData;
import com.zqnt.utils.common.proto.DetectionBatch;
import com.zqnt.utils.events.proto.ProduceNotificationRequest;
import com.zqnt.utils.livedata.proto.ProduceTelemetryRequest;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing live data streams (telemetry, detections, notifications).
 * Implementations handle the gRPC communication and stream management.
 * Provides both POJO-based (recommended) and Proto-based (advanced) APIs.
 */
public interface LiveDataService {

	// -------------------------------------------------------------------------
	// Telemetry
	// -------------------------------------------------------------------------

	/**
	 * Produces telemetry data using complete request wrapper.
	 *
	 * @param requestData The complete telemetry request data (POJO)
	 * @return CompletableFuture that completes when data is queued for sending
	 */
	CompletableFuture<Void> produceTelemetryData(TelemetryRequestData requestData);

	/**
	 * Produces telemetry data using Proto message directly.
	 * For advanced users who need direct Proto control.
	 *
	 * @param deviceSn         The serial number of the device
	 * @param telemetryRequest The telemetry data (Proto)
	 * @return CompletableFuture that completes when data is queued for sending
	 */
	CompletableFuture<Void> produceTelemetry(String deviceSn, ProduceTelemetryRequest telemetryRequest);

	/**
	 * Closes the telemetry stream for a specific device.
	 */
	CompletableFuture<Void> closeStream(String deviceSn);

	/**
	 * Closes all active telemetry streams.
	 * Should be called during application shutdown.
	 */
	CompletableFuture<Void> closeAllStreams();

	// -------------------------------------------------------------------------
	// Detections
	// -------------------------------------------------------------------------

	/**
	 * Produces detection data using complete request wrapper.
	 *
	 * @param requestData The detection batch data (POJO)
	 * @return CompletableFuture that completes when data is queued for sending
	 */
	CompletableFuture<Void> produceDetectionData(DetectionRequestData requestData);

	/**
	 * Produces detection data using Proto message directly.
	 * For advanced users who need direct Proto control.
	 *
	 * @param deviceSn       The serial number of the device
	 * @param detectionBatch The detection batch (Proto)
	 * @return CompletableFuture that completes when data is queued for sending
	 */
	CompletableFuture<Void> produceDetection(String deviceSn, DetectionBatch detectionBatch);

	/**
	 * Closes the detection stream for a specific device.
	 */
	CompletableFuture<Void> closeDetectionStream(String deviceSn);

	// -------------------------------------------------------------------------
	// Notifications
	// -------------------------------------------------------------------------

	/**
	 * Produces notification data using complete request wrapper.
	 *
	 * @param requestData The notification request data (POJO)
	 * @return CompletableFuture that completes when data is queued for sending
	 */
	CompletableFuture<Void> produceNotificationData(NotificationRequestData requestData);

	/**
	 * Produces notification data using Proto message directly.
	 * For advanced users who need direct Proto control.
	 *
	 * @param deviceSn            The serial number of the device
	 * @param notificationRequest The notification request (Proto)
	 * @return CompletableFuture that completes when data is queued for sending
	 */
	CompletableFuture<Void> produceNotification(String deviceSn, ProduceNotificationRequest notificationRequest);

	/**
	 * Closes the notification stream for a specific device.
	 */
	CompletableFuture<Void> closeNotificationStream(String deviceSn);
}
