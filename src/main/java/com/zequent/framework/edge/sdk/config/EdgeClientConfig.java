package com.zequent.framework.edge.sdk.config;

import com.zequent.framework.common.proto.AssetTypeEnum;
import com.zequent.framework.common.proto.AssetVendor;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.time.Duration;
import java.util.Optional;

@ConfigMapping(prefix = "zequent.edge")
public interface EdgeClientConfig {


	@WithName("endpoint")
	String endpoint();

	@WithName("sn")
	String sn();

	@WithDefault("30s")
	Duration timeout();

	@WithDefault("3")
	int maxRetries();


	@WithName("asset-type")
	AssetTypeEnum assetType();

	@WithName("asset-vendor")
	AssetVendor assetVendor();

	Optional<String> assetId();
}
