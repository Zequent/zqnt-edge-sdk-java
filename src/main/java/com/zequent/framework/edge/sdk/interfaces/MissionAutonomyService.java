package com.zequent.framework.edge.sdk.interfaces;

import com.zequent.framework.common.proto.SchedulerProtoDTO;
import com.zequent.framework.common.proto.TaskProtoDTO;
import com.zequent.framework.edge.sdk.models.MissionData;
import com.zequent.framework.services.mission.proto.*;
import io.smallrye.mutiny.Uni;

public interface MissionAutonomyService {

	Uni<MissionData> createMission(CreateMissionRequest createMissionRequest);

	Uni<MissionData> updateMission(UpdateMissionRequest updateMissionRequest);

	Uni<MissionData> getMission(GetMissionRequest getRequest);

	Uni<TaskProtoDTO> getTask(GetTaskRequest getTaskRequest);

	Uni<TaskProtoDTO> getTaskByFlightId(GetTaskRequest getTaskRequest);

	Uni<SchedulerProtoDTO> getScheduler(GetSchedulerRequest getSchedulerRequest);



}
