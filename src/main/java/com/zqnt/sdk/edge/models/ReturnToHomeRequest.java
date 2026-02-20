package com.zequent.framework.edge.sdk.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReturnToHomeRequest {
	private String tid;
	private String sn;
	private Float altitude;
}
