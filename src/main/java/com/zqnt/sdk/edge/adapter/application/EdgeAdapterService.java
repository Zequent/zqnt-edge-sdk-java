package com.zqnt.sdk.edge.adapter.application;

import com.zqnt.sdk.edge.adapter.domains.ChangeLensRequest;
import com.zqnt.sdk.edge.adapter.domains.ChangeZoomRequest;
import com.zqnt.sdk.edge.adapter.domains.CommandResult;
import com.zqnt.sdk.edge.adapter.domains.CurrentCapabilities;
import com.zqnt.sdk.edge.adapter.domains.GoToRequest;
import com.zqnt.sdk.edge.adapter.domains.LiveStreamStartRequest;
import com.zqnt.sdk.edge.adapter.domains.LiveStreamStopRequest;
import com.zqnt.sdk.edge.adapter.domains.LookAtRequest;
import com.zqnt.sdk.edge.adapter.domains.ManualControlInput;
import com.zqnt.sdk.edge.adapter.domains.ReturnToHomeRequest;
import com.zqnt.sdk.edge.adapter.domains.TakeOffRequest;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


public interface EdgeAdapterService {

	/**
	 * Execute takeoff command
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> takeOff(TakeOffRequest request) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("takeOff is not implemented for this asset", request.getSn())
		);
	}

	/**
	 * Return to home position
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> returnToHome(ReturnToHomeRequest request) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("returnToHome is not implemented for this asset", request.getSn())
		);
	}

	/**
	 * Navigate to coordinates
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> goTo(GoToRequest request) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("goTo is not implemented for this asset", request.getSn())
		);
	}

	/**
	 * Enter manual control mode
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> enterManualControl(String sn) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("enterManualControl is not implemented for this asset", sn)
		);
	}

	/**
	 * Exit manual control mode
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> exitManualControl(String sn) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("exitManualControl is not implemented for this asset", sn)
		);
	}

	/**
	 * Send manual control input stream
	 * This allows clients to stream manual control inputs (joystick commands) to the asset
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> manualControlInput(Stream<ManualControlInput> inputStream) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("manualControlInput is not implemented for this asset", "unknown")
		);
	}

	/**
	 * Open dock cover
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> openCover(String sn) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("openCover is not implemented for this asset", sn)
		);
	}

	/**
	 * Close dock cover
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> closeCover(String sn, Boolean force) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("closeCover is not implemented for this asset", sn)
		);
	}

	/**
	 * Start charging the drone
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> startCharging(String sn) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("startCharging is not implemented for this asset", sn)
		);
	}

	/**
	 * Stop charging the drone
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> stopCharging(String sn) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("stopCharging is not implemented for this asset", sn)
		);
	}

	/**
	 * Reboot the asset
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> rebootAsset(String sn) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("rebootAsset is not implemented for this asset", sn)
		);
	}

	/**
	 * Boot up the sub-asset (drone)
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> bootUpSubAsset(String sn) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("bootUpSubAsset is not implemented for this asset", sn)
		);
	}

	/**
	 * Boot down the sub-asset (drone)
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> bootDownSubAsset(String sn) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("bootDownSubAsset is not implemented for this asset", sn)
		);
	}

	/**
	 * Point camera at coordinates
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> lookAt(LookAtRequest lookAtRequest) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("lookAt is not implemented for this asset", lookAtRequest.getSn())
		);
	}

	/**
	 * Change camera lens
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> changeLens(ChangeLensRequest request) {
		return CompletableFuture.completedFuture(
			CommandResult.notImplemented("changeLens is not implemented for this asset", request.getSn())
		);
	}

	/**
	 * Change camera zoom
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> changeZoom(ChangeZoomRequest request) {
		return CompletableFuture.completedFuture(
			CommandResult.notImplemented("changeZoom is not implemented for this asset", request.getSn())
		);
	}

	/**
	 * Start live stream
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> startLiveStream(LiveStreamStartRequest request) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("startLiveStream is not implemented for this asset", request.getSn())
		);
	}

	/**
	 * Stop live stream
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> stopLiveStream(LiveStreamStopRequest request) {
		return CompletableFuture.completedFuture(
				CommandResult.notImplemented("stopLiveStream is not implemented for this asset", request.getSn())
		);
	}


	/**
	 * Enter remote debug mode
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> enterRemoteDebugMode(String sn) {
		return CompletableFuture.completedFuture(
			CommandResult.notImplemented("enterRemoteDebugMode is not implemented for this asset", sn)
		);
	}

	/**
	 * Close/Exit remote debug mode
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> closeRemoteDebugMode(String sn) {
		return CompletableFuture.completedFuture(
			CommandResult.notImplemented("closeRemoteDebugMode is not implemented for this asset", sn)
		);
	}

	/**
	 * Change air conditioner mode
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> changeAcMode(String sn, String mode) {
		return CompletableFuture.completedFuture(
			CommandResult.notImplemented("changeAcMode is not implemented for this asset", sn)
		);
	}

	/**
	 * Enable gimbal tracking
	 * Default: Returns NOT_IMPLEMENTED error
	 */
	default CompletableFuture<CommandResult> enableGimbalTracking(String sn, boolean enabled) {
		return CompletableFuture.completedFuture(
			CommandResult.notImplemented("enableGimbalTracking is not implemented for this asset", sn)
		);
	}

	/**
	 * Get current capabilities
	 * Default: Returns empty capabilities
	 */
	default CompletableFuture<CurrentCapabilities> getCapabilities(String sn) {
		return CompletableFuture.completedFuture(
			CurrentCapabilities.empty(sn)
		);
	}

	/**
	 * Start a task
	 * @param taskId
	 * @return {@link CommandResult}
	 */
	default CompletableFuture<CommandResult> startTask(String taskId, String tid) {
		return CompletableFuture.completedFuture(CommandResult.notImplemented("startTask is not implemented for this task",taskId));
	}

	/**
	 * Stop a task
	 * @param taskId
	 * @return {@link CommandResult}
	 */
	default CompletableFuture<CommandResult> stopTask(String taskId) {
		return CompletableFuture.completedFuture(CommandResult.notImplemented("stopTask is not implemented for this asset taskId", taskId));
	}


	default CompletableFuture<CommandResult> prepareTask(String taskId, String tid) {
		return CompletableFuture.completedFuture(CommandResult.notImplemented("prepareTask is not implemented for this asset taskId", taskId));
	}
}
