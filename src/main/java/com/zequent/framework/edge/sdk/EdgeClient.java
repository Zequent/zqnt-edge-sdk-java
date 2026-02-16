package com.zequent.framework.edge.sdk;

import com.zequent.framework.edge.sdk.config.EdgeClientConfig;
import com.zequent.framework.edge.sdk.interfaces.EdgeAdapterService;
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
