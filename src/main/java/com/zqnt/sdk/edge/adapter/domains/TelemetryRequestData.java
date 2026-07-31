package com.zqnt.sdk.edge.adapter.domains;

import com.zqnt.utils.edge.sdk.domains.TelemetryData;
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
	private TelemetryData telemetry;

}
