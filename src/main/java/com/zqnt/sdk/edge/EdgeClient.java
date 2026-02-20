package com.zqnt.sdk.edge;

import com.zqnt.sdk.edge.adapter.application.EdgeAdapterService;
import com.zqnt.sdk.edge.config.EdgeClientConfig;

import lombok.Getter;

public class EdgeClient {

	@Getter
	private final EdgeClientConfig config;
	private final EdgeAdapterService edgeAdapterService;

	public EdgeClient(EdgeClientConfig config, EdgeAdapterService edgeAdapterService) {
		this.config = config;
		this.edgeAdapterService = edgeAdapterService;
	}

	public EdgeAdapterService edgeAdapter() {
		return edgeAdapterService;
	}

	/**
	 * Get the configured serial number
	 */
	public String getSn() {
		return config.getSn();
	}
}
