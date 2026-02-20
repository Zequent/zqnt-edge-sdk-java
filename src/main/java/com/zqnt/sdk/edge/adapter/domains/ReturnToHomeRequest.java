package com.zqnt.sdk.edge.adapter.domains;

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
