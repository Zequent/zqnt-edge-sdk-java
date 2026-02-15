package com.zequent.framework.edge.sdk.models;


import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Capability {

	private String command;
	private String description;
	private Boolean available;
	private String unavailableReason;
	private Map<String, String> metadata = new HashMap<>();
}
