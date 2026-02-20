package com.zequent.framework.edge.sdk.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoToRequest {
	private String sn;
	private String tid;
	private Coordinates coordinates;
}
