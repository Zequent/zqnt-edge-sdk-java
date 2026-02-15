package com.zequent.framework.edge.sdk.models;

import lombok.Data;

@Data
public class LookAtRequest {

    private String sn;

    private Double latitude;

    private Double longitude;

    private Float altitude;

    private Boolean locked;

    private String payloadIndex;
}
