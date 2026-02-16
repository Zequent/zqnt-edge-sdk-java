package com.zequent.framework.edge.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LookAtRequest {

    private String sn;

    private Double latitude;

    private Double longitude;

    private Float altitude;

    private Boolean locked;

    private String payloadIndex;
}
