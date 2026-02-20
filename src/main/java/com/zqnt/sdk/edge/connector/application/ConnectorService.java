package com.zqnt.sdk.edge.connector.application;


import com.zequent.framework.utils.missionautonomy.dto.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ConnectorService {


	CompletableFuture<AssetDTO> getAssetBySn(String sn);

	CompletableFuture<AssetDTO> getAssetById(String id);

	CompletableFuture<SubAssetDTO> getSubAssetBySn(String sn);

	CompletableFuture<AssetDTO> updateAsset(String id, AssetDTO assetDTO);

	CompletableFuture<AssetDTO> registerAsset(AssetDTO assetDTO);

	CompletableFuture<Boolean> deRegisterAsset(String id);

	CompletableFuture<MissionDTO> getMissionById(String id);

	CompletableFuture<MissionDTO> createMission(MissionDTO missionDTO);

	CompletableFuture<MissionDTO> updateMission(String id, MissionDTO missionDTO);

	CompletableFuture<Boolean> deleteMission(String id);

	CompletableFuture<TaskDTO> getTaskById(String id);

	CompletableFuture<TaskDTO> createTask(TaskDTO taskDTO);

	CompletableFuture<TaskDTO> updateTask(String id, TaskDTO taskDTO);

	CompletableFuture<Boolean> deleteTask(String id);

	CompletableFuture<TaskDTO> getTaskByFlightId(String flightId);

	CompletableFuture<SchedulerDTO> getSchedulerById(String id);

	CompletableFuture<SchedulerDTO> createScheduler(SchedulerDTO schedulerDTO);

	CompletableFuture<SchedulerDTO> updateScheduler(String id, SchedulerDTO schedulerDTO);

	CompletableFuture<Boolean> deleteScheduler(String id);

	CompletableFuture<OrganizationDTO> getOrganizationById(String id);

	CompletableFuture<List<WaypointDTO>> getWaypointsByTaskId(String id);


}
