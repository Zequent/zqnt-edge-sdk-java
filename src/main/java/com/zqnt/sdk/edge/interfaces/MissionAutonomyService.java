package com.zequent.framework.edge.sdk.interfaces;

import com.zequent.framework.services.mission.proto.*;
import com.zequent.framework.utils.missionautonomy.dto.MissionDTO;
import com.zequent.framework.utils.missionautonomy.dto.SchedulerDTO;
import com.zequent.framework.utils.missionautonomy.dto.TaskDTO;

import java.util.concurrent.CompletableFuture;

public interface MissionAutonomyService {

	CompletableFuture<MissionDTO> createMission(CreateMissionRequest createMissionRequest);

	CompletableFuture<MissionDTO> updateMission(UpdateMissionRequest updateMissionRequest);

	CompletableFuture<MissionDTO> getMission(GetMissionRequest getRequest);

	CompletableFuture<TaskDTO> getTask(GetTaskRequest getTaskRequest);

	CompletableFuture<TaskDTO> getTaskByFlightId(GetTaskRequest getTaskRequest);

	CompletableFuture<SchedulerDTO> getScheduler(GetSchedulerRequest getSchedulerRequest);
}
