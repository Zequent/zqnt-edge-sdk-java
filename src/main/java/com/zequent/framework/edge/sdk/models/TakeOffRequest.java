package com.zequent.framework.edge.sdk.models;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TakeOffRequest {
	private String sn;
	private String tid;
	private Coordinates coordinates;
}
