package com.zqnt.sdk.edge.config;

import com.zequent.framework.common.proto.AssetTypeEnum;
import com.zequent.framework.common.proto.AssetVendor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class EdgeClientConfig {

	private String endpoint;
	private String sn;

	@Builder.Default
	private Duration timeout = Duration.ofSeconds(30);

	@Builder.Default
	private int maxRetries = 3;

	private AssetTypeEnum assetType;
	private AssetVendor assetVendor;
	private String assetId;
}
