package com.zequent.framework.edge.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeZoomRequest {
	private String sn;
	private String lens;
	private String payloadIndex;
	private Float zoom;
}
