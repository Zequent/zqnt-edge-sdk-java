package com.zqnt.sdk.edge.application;

import com.zqnt.utils.asset.domains.AssetPayloadDTO;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProtoJsonMapperPayloadTest {

    private final ProtoJsonMapper mapper = new ProtoJsonMapper();

    @Test
    void preservesPayloadInventoryAndState() {
        AssetPayloadDTO input = AssetPayloadDTO.builder()
                .externalId("parachute-1")
                .kind("PARACHUTE")
                .state(Map.of("widgetValueCount", 2))
                .active(true)
                .build();

        AssetPayloadDTO result = mapper.map(mapper.map(input));

        assertEquals("parachute-1", result.getExternalId());
        assertEquals("PARACHUTE", result.getKind());
        assertEquals(2, result.getState().get("widgetValueCount"));
    }
}
