package com.zqnt.sdk.edge.adapter.domains;


import com.zqnt.utils.devicecontrol.proto.CapabilityTargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Capability {

	private String command;
	private String description;
	private Boolean available;
	private String unavailableReason;
	private Map<String, String> metadata = new HashMap<>();
	private Map<String, Object> constraints = new HashMap<>();
	private Map<String, Object> inputSchema = new HashMap<>();
	private Map<String, Object> outputSchema = new HashMap<>();
	private CapabilityTargetType targetType = CapabilityTargetType.CAPABILITY_TARGET_TYPE_ASSET;
	private String targetRef;
	private String schemaVersion;

	public Capability(String command, String description, Boolean available, String unavailableReason,
			Map<String, String> metadata) {
		this.command = command;
		this.description = description;
		this.available = available;
		this.unavailableReason = unavailableReason;
		this.metadata = metadata == null ? new HashMap<>() : metadata;
	}
}
