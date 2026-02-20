package com.zqnt.sdk.edge.adapter.domains;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LiveStreamStartRequest {

	private String sn;
	private String tid;
	private String videoId;
	private String streamServer;
	private String videoType;
}
