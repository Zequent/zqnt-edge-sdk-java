package com.zequent.framework.edge.sdk.interfaces.impl;

import com.zequent.framework.common.proto.RequestBase;
import com.zequent.framework.edge.sdk.interfaces.ConnectorService;
import com.zequent.framework.edge.sdk.mapper.ProtoJsonMapper;
import com.zequent.framework.services.connector.proto.*;
import com.zequent.framework.utils.core.ProtobufHelpers;
import com.zequent.framework.utils.missionautonomy.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ConnectorServiceImpl implements ConnectorService {

	private final ProtoJsonMapper protoJsonMapper;
	private final MutinyConnectorServiceGrpc.MutinyConnectorServiceStub connectorServiceStub;

	public ConnectorServiceImpl(ProtoJsonMapper protoJsonMapper, MutinyConnectorServiceGrpc.MutinyConnectorServiceStub connectorServiceStub) {
		this.protoJsonMapper = protoJsonMapper;
		this.connectorServiceStub = connectorServiceStub;
	}


	@Override
	public CompletableFuture<AssetDTO> getAssetBySn(String sn) {
		return connectorServiceStub.getAssetBySn(ConnectorGetAssetBySnRequest.newBuilder()
						.setBase(RequestBase.newBuilder().setTid(UUID.randomUUID().toString())
								.setSn(sn)
								.setTimestamp(ProtobufHelpers.now()).build()).build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting Asset from Connector Service");
						return null;
					} else {
						return protoJsonMapper.map(response.getAssetDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<AssetDTO> getAssetById(String id) {
		return connectorServiceStub.getAssetById(ConnectorGetAssetByIdRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTimestamp(ProtobufHelpers.now())
								.setTid(UUID.randomUUID().toString())
								.build())
						.setId(id).build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting Asset from Connector Service");
						return null;
					} else {
						return protoJsonMapper.map(response.getAssetDTO());
					}

				}).subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<SubAssetDTO> getSubAssetBySn(String sn) {
		return connectorServiceStub.getSubAssetBySn(ConnectorGetSubAssetBySnRequest.newBuilder()
						.setBase(RequestBase.newBuilder().setTid(UUID.randomUUID().toString())
								.setSn(sn)
								.setTimestamp(ProtobufHelpers.now()).build()).build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting Asset from Connector Service");
						return null;
					} else {
						return protoJsonMapper.map(response.getSubAssetDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<AssetDTO> updateAsset(String id, AssetDTO assetDTO) {
		return connectorServiceStub.updateAsset(ConnectorUpdateAssetRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setSn(assetDTO.getSn())
								.setTimestamp(ProtobufHelpers.now())
								.setTid(UUID.randomUUID().toString())
								.build())
						.setAssetId(id)
						.setAssetDTO(protoJsonMapper.map(assetDTO)).build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on updating asset: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getAssetDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<AssetDTO> registerAsset(AssetDTO assetDTO) {
		return connectorServiceStub.registerAsset(ConnectorRegisterAssetRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.setSn(assetDTO.getSn())
								.build())
						.setAssetDTO(protoJsonMapper.map(assetDTO))
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on registering asset {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getAssetDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<Boolean> deRegisterAsset(String id) {
		return connectorServiceStub.deRegisterAsset(ConnectorDeRegisterAssetRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on deregistering asset: {}", response.getError());
						return false;
					} else {
						return true;
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<MissionDTO> getMissionById(String id) {
		return connectorServiceStub.getMission(
						ConnectorGetMissionRequest.newBuilder()
								.setBase(RequestBase.newBuilder()
										.setTimestamp(ProtobufHelpers.now())
										.setTid(UUID.randomUUID().toString())
										.build())
								.setMissionId(id)
								.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting Mission: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getMissionDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<MissionDTO> createMission(MissionDTO missionDTO) {
		return connectorServiceStub.createMission(ConnectorCreateMissionRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setMissionDTO(protoJsonMapper.map(missionDTO))
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on creating mission: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getMissionDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<MissionDTO> updateMission(String id, MissionDTO missionDTO) {
		return connectorServiceStub.updateMission(ConnectorUpdateMissionRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setMissionId(id)
						.setMissionDTO(protoJsonMapper.map(missionDTO))
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on updating mission: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getMissionDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<Boolean> deleteMission(String id) {
		return connectorServiceStub.deleteMission(ConnectorDeleteMissionRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setMissionId(id)
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on deleting mission: {}", response.getError());
						return false;
					} else {
						return true;
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<TaskDTO> getTaskById(String id) {
		return connectorServiceStub.getTask(ConnectorGetTaskRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setTaskId(id)
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting task: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getTaskDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<TaskDTO> createTask(TaskDTO taskDTO) {
		return connectorServiceStub.createTask(ConnectorCreateTaskRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setTaskDTO(protoJsonMapper.map(taskDTO))
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on creating task: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getTaskDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<TaskDTO> updateTask(String id, TaskDTO taskDTO) {
		return connectorServiceStub.updateTask(ConnectorUpdateTaskRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setTaskId(id)
						.setTaskDTO(protoJsonMapper.map(taskDTO))
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on updating task: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getTaskDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<Boolean> deleteTask(String id) {
		return connectorServiceStub.deleteTask(ConnectorDeleteTaskRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setTaskId(id)
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on deleting task: {}", response.getError());
						return false;
					} else {
						return true;
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<TaskDTO> getTaskByFlightId(String flightId) {
		return connectorServiceStub.getTaskByFlightId(ConnectorGetTaskRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setTaskId(flightId)
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting task by flight id: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getTaskDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<SchedulerDTO> getSchedulerById(String id) {
		return connectorServiceStub.getScheduler(ConnectorGetSchedulerRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setSchedulerId(id)
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting scheduler: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getSchedulerDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<SchedulerDTO> createScheduler(SchedulerDTO schedulerDTO) {
		return connectorServiceStub.createScheduler(ConnectorCreateSchedulerRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setSchedulerDTO(protoJsonMapper.map(schedulerDTO))
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on creating scheduler: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getSchedulerDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<SchedulerDTO> updateScheduler(String id, SchedulerDTO schedulerDTO) {
		return connectorServiceStub.updateScheduler(ConnectorUpdateSchedulerRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setSchedulerId(id)
						.setSchedulerDTO(protoJsonMapper.map(schedulerDTO))
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on updating scheduler: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getSchedulerDTO());
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<Boolean> deleteScheduler(String id) {
		return connectorServiceStub.deleteScheduler(ConnectorDeleteSchedulerRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now())
								.build())
						.setSchedulerId(id)
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on deleting scheduler: {}", response.getError());
						return false;
					} else {
						return true;
					}
				})
				.subscribeAsCompletionStage();
	}

	@Override
	public CompletableFuture<OrganizationDTO> getOrganizationById(String id) {
		return connectorServiceStub.getOrganization(ConnectorGetOrganizationRequest.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTid(UUID.randomUUID().toString())
								.setTimestamp(ProtobufHelpers.now()).build())
						.build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting Organization: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getOrganizationDTO());
					}
				})
				.subscribeAsCompletionStage();
	}


	@Override
	public CompletableFuture<List<WaypointDTO>> getWaypointsByTaskId(String id) {
		return connectorServiceStub.getWaypointsByTaskId(ConnectorGetWaypointsByTaskId.newBuilder()
						.setBase(RequestBase.newBuilder()
								.setTimestamp(ProtobufHelpers.now())
								.setTid(UUID.randomUUID().toString())
								.build())
						.setTaskId(id).build())
				.map(response -> {
					if (response.getHasErrors()) {
						log.error("Error on getting Waypoint: {}", response.getError());
						return null;
					} else {
						return protoJsonMapper.map(response.getWaypointDTOList().getWaypointsList());
					}
				})
				.subscribeAsCompletionStage();
	}
}
