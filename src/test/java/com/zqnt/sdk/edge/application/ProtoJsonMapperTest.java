package com.zqnt.sdk.edge.application;

import com.zqnt.utils.mission.proto.*;
import com.zqnt.utils.missionautonomy.domains.config.WaypointTaskConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoJsonMapperTest {

    private final ProtoJsonMapper mapper = new ProtoJsonMapper();

    @Test
    void preservesTypedWaypointConfigForEdgeTaskExecution() {
        TaskProtoDTO source = TaskProtoDTO.newBuilder()
                .setName("NFZ rerouting task")
                .setTaskType(TaskTypeProto.TASK_TYPE_WAYPOINT)
                .setStatus(TaskStatus.TASK_DRAFT)
                .setWaypointConfig(WaypointTaskConfigProto.newBuilder()
                        .addWaypoints(waypoint(47.775300, 9.267800, 0))
                        .addWaypoints(waypoint(47.775511245, 9.268249301, 1))
                        .addWaypoints(waypoint(47.776583, 9.269973, 2)))
                .build();

        var dto = mapper.map(source);
        WaypointTaskConfig config = assertInstanceOf(WaypointTaskConfig.class, dto.getConfig());
        TaskProtoDTO restored = mapper.map(dto);

        assertEquals(TaskTypeProto.TASK_TYPE_WAYPOINT, dto.getTaskType());
        assertEquals(3, config.getWaypoints().size());
        assertTrue(restored.hasWaypointConfig());
        assertEquals(source.getWaypointConfig().getWaypointsList(),
                restored.getWaypointConfig().getWaypointsList());
    }

    private static WaypointProtoDTO waypoint(double latitude, double longitude, int order) {
        return WaypointProtoDTO.newBuilder()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setAltitude(50)
                .setWpOrder(order)
                .build();
    }
}
