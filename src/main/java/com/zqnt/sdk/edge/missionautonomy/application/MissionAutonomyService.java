package com.zqnt.sdk.edge.missionautonomy.application;

import com.zqnt.utils.missionautonomy.domains.MissionDTO;
import com.zqnt.utils.missionautonomy.domains.SchedulerDTO;
import com.zqnt.utils.missionautonomy.domains.TaskDTO;
import com.zqnt.utils.workflow.proto.CreateMissionRequest;
import com.zqnt.utils.workflow.proto.GetMissionRequest;
import com.zqnt.utils.workflow.proto.GetSchedulerRequest;
import com.zqnt.utils.workflow.proto.GetTaskByFlightIdRequest;
import com.zqnt.utils.workflow.proto.GetTaskRequest;
import com.zqnt.utils.workflow.proto.UpdateMissionRequest;

import java.util.concurrent.CompletableFuture;

public interface MissionAutonomyService {

	CompletableFuture<MissionDTO> createMission(CreateMissionRequest createMissionRequest);

	CompletableFuture<MissionDTO> updateMission(UpdateMissionRequest updateMissionRequest);

	CompletableFuture<MissionDTO> getMission(GetMissionRequest getRequest);

	CompletableFuture<TaskDTO> getTask(GetTaskRequest getTaskRequest);

	CompletableFuture<TaskDTO> getTaskByFlightId(GetTaskByFlightIdRequest getTaskRequest);

	CompletableFuture<SchedulerDTO> getScheduler(GetSchedulerRequest getSchedulerRequest);
}
