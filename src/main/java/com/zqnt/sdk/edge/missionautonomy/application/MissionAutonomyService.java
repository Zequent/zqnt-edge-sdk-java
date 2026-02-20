package com.zqnt.sdk.edge.missionautonomy.application;

import com.zequent.framework.services.mission.proto.*;
import com.zqnt.utils.missionautonomy.domains.MissionDTO;
import com.zqnt.utils.missionautonomy.domains.SchedulerDTO;
import com.zqnt.utils.missionautonomy.domains.TaskDTO;

import java.util.concurrent.CompletableFuture;

public interface MissionAutonomyService {

	CompletableFuture<MissionDTO> createMission(CreateMissionRequest createMissionRequest);

	CompletableFuture<MissionDTO> updateMission(UpdateMissionRequest updateMissionRequest);

	CompletableFuture<MissionDTO> getMission(GetMissionRequest getRequest);

	CompletableFuture<TaskDTO> getTask(GetTaskRequest getTaskRequest);

	CompletableFuture<TaskDTO> getTaskByFlightId(GetTaskRequest getTaskRequest);

	CompletableFuture<SchedulerDTO> getScheduler(GetSchedulerRequest getSchedulerRequest);
}
