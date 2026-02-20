package com.zequent.framework.edge.sdk.interfaces.impl;

import com.zequent.framework.edge.sdk.config.EdgeClientConfig;
import com.zequent.framework.edge.sdk.interfaces.EdgeAdapterService;
import com.zequent.framework.edge.sdk.models.CommandResult;
import com.zequent.framework.edge.sdk.models.CurrentCapabilities;

import java.util.concurrent.CompletableFuture;

public class EdgeAdapterServiceImpl implements EdgeAdapterService {

	private final EdgeClientConfig config;

	public EdgeAdapterServiceImpl(EdgeClientConfig config) {
		this.config = config;
	}

	/**
	 * Get configured serial number
	 */
	protected String getDefaultSn() {
		return config.getSn();
	}

	// ==================== CONVENIENCE METHODS (no SN parameter needed) ====================

	/**
	 * Open cover using configured serial number
	 */
	public CompletableFuture<CommandResult> openCover() {
		return openCover(getDefaultSn());
	}

	/**
	 * Close cover using configured serial number
	 */
	public CompletableFuture<CommandResult> closeCover() {
		return closeCover(getDefaultSn(), null);
	}

	/**
	 * Start charging using configured serial number
	 */
	public CompletableFuture<CommandResult> startCharging() {
		return startCharging(getDefaultSn());
	}

	/**
	 * Stop charging using configured serial number
	 */
	public CompletableFuture<CommandResult> stopCharging() {
		return stopCharging(getDefaultSn());
	}

	/**
	 * Reboot asset using configured serial number
	 */
	public CompletableFuture<CommandResult> rebootAsset() {
		return rebootAsset(getDefaultSn());
	}

	/**
	 * Boot up sub-asset using configured serial number
	 */
	public CompletableFuture<CommandResult> bootUpSubAsset() {
		return bootUpSubAsset(getDefaultSn());
	}

	/**
	 * Boot down sub-asset using configured serial number
	 */
	public CompletableFuture<CommandResult> bootDownSubAsset() {
		return bootDownSubAsset(getDefaultSn());
	}

	/**
	 * Enter manual control using configured serial number
	 */
	public CompletableFuture<CommandResult> enterManualControl() {
		return enterManualControl(getDefaultSn());
	}

	/**
	 * Exit manual control using configured serial number
	 */
	public CompletableFuture<CommandResult> exitManualControl() {
		return exitManualControl(getDefaultSn());
	}

	/**
	 * Look at using configured serial number
	 */

	/**
	 * Get capabilities using configured serial number
	 */
	public CompletableFuture<CurrentCapabilities> getCapabilities() {
		return getCapabilities(getDefaultSn());
	}

	/**
	 * Enable gimbal tracking using configured serial number
	 */
	public CompletableFuture<CommandResult> enableGimbalTracking(boolean enabled) {
		return enableGimbalTracking(getDefaultSn(), enabled);
	}

	/**
	 * Change AC mode using configured serial number
	 */
	public CompletableFuture<CommandResult> changeAcMode(String mode) {
		return changeAcMode(getDefaultSn(), mode);
	}
}
