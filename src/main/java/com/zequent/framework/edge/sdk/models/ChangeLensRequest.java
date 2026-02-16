package com.zequent.framework.edge.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLensRequest {
	private String sn;
	private String lens;
	private String videoId;
}
