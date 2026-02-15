package com.zequent.framework.edge.sdk.models;

import lombok.*;

@Getter
@Setter
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
