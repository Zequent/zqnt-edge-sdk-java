package com.zqnt.sdk.edge.connector.application;


import com.zqnt.utils.asset.domains.AssetDTO;
import com.zqnt.utils.asset.domains.AssetPayloadDTO;
import com.zqnt.utils.asset.domains.SubAssetDTO;
import com.zqnt.utils.missionautonomy.domains.OrganizationDTO;
import com.zqnt.utils.missionautonomy.domains.SchedulerDTO;

import java.util.concurrent.CompletableFuture;

public interface ConnectorService {


	CompletableFuture<AssetDTO> getAssetBySn(String sn);

	CompletableFuture<AssetDTO> getAssetById(String id);

	CompletableFuture<SubAssetDTO> getSubAssetBySn(String sn);

	CompletableFuture<AssetPayloadDTO> upsertAssetPayload(String assetSn, String subAssetSn,
			AssetPayloadDTO payload);

	CompletableFuture<AssetDTO> updateAsset(String id, AssetDTO assetDTO);

	CompletableFuture<AssetDTO> registerAsset(AssetDTO assetDTO);

	CompletableFuture<Boolean> deRegisterAsset(String id);

	// Mission/Task CRUD was retired from ConnectorService in favor of the capability-execution
	// model (CapabilityPackage/CapabilityExecution). Use MissionAutonomyService's capability
	// execution APIs (via the client SDK) instead.

	CompletableFuture<SchedulerDTO> getSchedulerById(String id);

	CompletableFuture<SchedulerDTO> createScheduler(SchedulerDTO schedulerDTO);

	CompletableFuture<SchedulerDTO> updateScheduler(String id, SchedulerDTO schedulerDTO);

	CompletableFuture<Boolean> deleteScheduler(String id);

	CompletableFuture<OrganizationDTO> getOrganizationById(String id);



}
