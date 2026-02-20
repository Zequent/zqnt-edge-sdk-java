package com.zequent.framework.edge.sdk.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LiveStreamStopRequest {

	private String sn;
	private String tid;
	private String videoId;
}
