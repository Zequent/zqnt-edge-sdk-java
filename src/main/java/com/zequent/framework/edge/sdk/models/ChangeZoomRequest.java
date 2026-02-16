package com.zequent.framework.edge.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeZoomRequest {
	private String sn;
	private String lens;
	private String payloadIndex;
	private Float zoom;
}
