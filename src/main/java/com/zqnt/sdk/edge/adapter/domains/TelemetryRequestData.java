package com.zqnt.sdk.edge.adapter.domains;

import com.zequent.framework.common.proto.LiveDataType;
import com.zequent.framework.utils.edge.sdk.dto.AssetTelemetryData;
import com.zequent.framework.utils.edge.sdk.dto.SubAssetTelemetryData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryRequestData {

	private String tid;
	private String sn;
	private String assetId;
	private LocalDateTime timestamp;
	private LiveDataType type;
	private AssetTelemetryData assetTelemetry;
	private SubAssetTelemetryData subAssetTelemetry;

}
