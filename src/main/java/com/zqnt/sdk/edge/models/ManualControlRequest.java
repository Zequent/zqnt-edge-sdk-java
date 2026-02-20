package com.zequent.framework.edge.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualControlRequest {
	private String sn;
	private boolean enable;  // true = enter, false = exit
}
