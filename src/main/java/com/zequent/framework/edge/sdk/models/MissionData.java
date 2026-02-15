package com.zequent.framework.edge.sdk.models;


import io.quarkus.arc.All;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
public class MissionData {

	private UUID id;
	private String name;
}
