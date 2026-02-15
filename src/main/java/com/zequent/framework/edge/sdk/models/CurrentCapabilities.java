package com.zequent.framework.edge.sdk.models;

import com.zequent.framework.common.proto.AssetTypeEnum;
import lombok.*;

import java.util.Collections;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CurrentCapabilities {
	private String sn;
	private AssetTypeEnum assetType;
	private Set<Capability> capabilities;
	private long timestamp;

	/**
	 * Create an empty capabilities response
	 * Used when getCapabilities is not implemented
	 */
	public static CurrentCapabilities empty(String sn) {
		return new CurrentCapabilities(
			sn,
			AssetTypeEnum.ASSET_TYPE_UNKNOWN,
			Collections.emptySet(),
			System.currentTimeMillis()
		);
	}

	/**
	 * Create a capabilities response with specific asset type
	 */
	public static CurrentCapabilities of(String sn, AssetTypeEnum assetType, Set<Capability> capabilities) {
		return new CurrentCapabilities(
			sn,
			assetType,
			capabilities,
			System.currentTimeMillis()
		);
	}

}
