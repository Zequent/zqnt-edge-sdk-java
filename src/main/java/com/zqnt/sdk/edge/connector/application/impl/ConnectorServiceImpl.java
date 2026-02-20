package com.zqnt.sdk.edge.connector.application.impl;

import com.zequent.framework.common.proto.RequestBase;
import com.zqnt.sdk.edge.application.ProtoJsonMapper;
import com.zqnt.sdk.edge.connector.application.ConnectorService;
import com.zequent.framework.services.connector.proto.*;
import com.zequent.framework.utils.core.ProtobufHelpers;
import com.zequent.framework.utils.missionautonomy.dto.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
public class ConnectorServiceImpl implements ConnectorService {

	private final ProtoJsonMapper protoJsonMapper;
	private final ConnectorServiceGrpc.ConnectorServiceStub connectorServiceStub;

	public ConnectorServiceImpl(ProtoJsonMapper protoJsonMapper, ConnectorServiceGrpc.ConnectorServiceStub connectorServiceStub) {
		this.protoJsonMapper = protoJsonMapper;
		this.connectorServiceStub = connectorServiceStub;
	}

	/**
	 * Helper method to wrap gRPC async calls into CompletableFuture
	 */
	private <REQ, RES> CompletableFuture<RES> callAsync(
			REQ request,
			BiConsumer<REQ, StreamObserver<RES>> grpcMethod) {

		CompletableFuture<RES> future = new CompletableFuture<>();

		grpcMethod.accept(request, new StreamObserver<RES>() {
			private RES response;

			@Override
			public void onNext(RES value) {
				response = value;
			}

			@Override
			public void onError(Throwable t) {
				future.completeExceptionally(t);
			}

			@Override
			public void onCompleted() {
				future.complete(response);
			}
		});

		return future;
	}

	@Override
	public CompletableFuture<AssetDTO> getAssetBySn(String sn) {
		var request = ConnectorGetAssetBySnRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setSn(sn)
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.build();

		return callAsync(request, connectorServiceStub::getAssetBySn)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting Asset from Connector Service");
						return null;
					}
					return protoJsonMapper.map(response.getAssetDTO());
				});
	}

	@Override
	public CompletableFuture<AssetDTO> getAssetById(String id) {
		var request = ConnectorGetAssetByIdRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTimestamp(ProtobufHelpers.now())
						.setTid(UUID.randomUUID().toString())
						.build())
				.setId(id)
				.build();

		return callAsync(request, connectorServiceStub::getAssetById)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting Asset from Connector Service");
						return null;
					}
					return protoJsonMapper.map(response.getAssetDTO());
				});
	}

	@Override
	public CompletableFuture<SubAssetDTO> getSubAssetBySn(String sn) {
		var request = ConnectorGetSubAssetBySnRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setSn(sn)
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.build();

		return callAsync(request, connectorServiceStub::getSubAssetBySn)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting SubAsset from Connector Service");
						return null;
					}
					return protoJsonMapper.map(response.getSubAssetDTO());
				});
	}

	@Override
	public CompletableFuture<AssetDTO> updateAsset(String id, AssetDTO assetDTO) {
		var request = ConnectorUpdateAssetRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setSn(assetDTO.getSn())
						.setTimestamp(ProtobufHelpers.now())
						.setTid(UUID.randomUUID().toString())
						.build())
				.setAssetId(id)
				.setAssetDTO(protoJsonMapper.map(assetDTO))
				.build();

		return callAsync(request, connectorServiceStub::updateAsset)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error updating asset: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getAssetDTO());
				});
	}

	@Override
	public CompletableFuture<AssetDTO> registerAsset(AssetDTO assetDTO) {
		var request = ConnectorRegisterAssetRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.setSn(assetDTO.getSn())
						.build())
				.setAssetDTO(protoJsonMapper.map(assetDTO))
				.build();

		return callAsync(request, connectorServiceStub::registerAsset)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error registering asset: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getAssetDTO());
				});
	}

	@Override
	public CompletableFuture<Boolean> deRegisterAsset(String id) {
		var request = ConnectorDeRegisterAssetRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.build();

		return callAsync(request, connectorServiceStub::deRegisterAsset)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error deregistering asset: {}", response.getError());
						return false;
					}
					return true;
				});
	}

	@Override
	public CompletableFuture<MissionDTO> getMissionById(String id) {
		var request = ConnectorGetMissionRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTimestamp(ProtobufHelpers.now())
						.setTid(UUID.randomUUID().toString())
						.build())
				.setMissionId(id)
				.build();

		return callAsync(request, connectorServiceStub::getMission)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting Mission: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getMissionDTO());
				});
	}

	@Override
	public CompletableFuture<MissionDTO> createMission(MissionDTO missionDTO) {
		var request = ConnectorCreateMissionRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setMissionDTO(protoJsonMapper.map(missionDTO))
				.build();

		return callAsync(request, connectorServiceStub::createMission)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error creating mission: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getMissionDTO());
				});
	}

	@Override
	public CompletableFuture<MissionDTO> updateMission(String id, MissionDTO missionDTO) {
		var request = ConnectorUpdateMissionRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setMissionId(id)
				.setMissionDTO(protoJsonMapper.map(missionDTO))
				.build();

		return callAsync(request, connectorServiceStub::updateMission)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error updating mission: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getMissionDTO());
				});
	}

	@Override
	public CompletableFuture<Boolean> deleteMission(String id) {
		var request = ConnectorDeleteMissionRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setMissionId(id)
				.build();

		return callAsync(request, connectorServiceStub::deleteMission)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error deleting mission: {}", response.getError());
						return false;
					}
					return true;
				});
	}

	@Override
	public CompletableFuture<TaskDTO> getTaskById(String id) {
		var request = ConnectorGetTaskRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setTaskId(id)
				.build();

		return callAsync(request, connectorServiceStub::getTask)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting task: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getTaskDTO());
				});
	}

	@Override
	public CompletableFuture<TaskDTO> createTask(TaskDTO taskDTO) {
		var request = ConnectorCreateTaskRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setTaskDTO(protoJsonMapper.map(taskDTO))
				.build();

		return callAsync(request, connectorServiceStub::createTask)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error creating task: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getTaskDTO());
				});
	}

	@Override
	public CompletableFuture<TaskDTO> updateTask(String id, TaskDTO taskDTO) {
		var request = ConnectorUpdateTaskRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setTaskId(id)
				.setTaskDTO(protoJsonMapper.map(taskDTO))
				.build();

		return callAsync(request, connectorServiceStub::updateTask)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error updating task: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getTaskDTO());
				});
	}

	@Override
	public CompletableFuture<Boolean> deleteTask(String id) {
		var request = ConnectorDeleteTaskRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setTaskId(id)
				.build();

		return callAsync(request, connectorServiceStub::deleteTask)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error deleting task: {}", response.getError());
						return false;
					}
					return true;
				});
	}

	@Override
	public CompletableFuture<TaskDTO> getTaskByFlightId(String flightId) {
		var request = ConnectorGetTaskRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setTaskId(flightId)
				.build();

		return callAsync(request, connectorServiceStub::getTaskByFlightId)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting task by flight id: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getTaskDTO());
				});
	}

	@Override
	public CompletableFuture<SchedulerDTO> getSchedulerById(String id) {
		var request = ConnectorGetSchedulerRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setSchedulerId(id)
				.build();

		return callAsync(request, connectorServiceStub::getScheduler)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting scheduler: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getSchedulerDTO());
				});
	}

	@Override
	public CompletableFuture<SchedulerDTO> createScheduler(SchedulerDTO schedulerDTO) {
		var request = ConnectorCreateSchedulerRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setSchedulerDTO(protoJsonMapper.map(schedulerDTO))
				.build();

		return callAsync(request, connectorServiceStub::createScheduler)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error creating scheduler: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getSchedulerDTO());
				});
	}

	@Override
	public CompletableFuture<SchedulerDTO> updateScheduler(String id, SchedulerDTO schedulerDTO) {
		var request = ConnectorUpdateSchedulerRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setSchedulerId(id)
				.setSchedulerDTO(protoJsonMapper.map(schedulerDTO))
				.build();

		return callAsync(request, connectorServiceStub::updateScheduler)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error updating scheduler: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getSchedulerDTO());
				});
	}

	@Override
	public CompletableFuture<Boolean> deleteScheduler(String id) {
		var request = ConnectorDeleteSchedulerRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.setSchedulerId(id)
				.build();

		return callAsync(request, connectorServiceStub::deleteScheduler)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error deleting scheduler: {}", response.getError());
						return false;
					}
					return true;
				});
	}

	@Override
	public CompletableFuture<OrganizationDTO> getOrganizationById(String id) {
		var request = ConnectorGetOrganizationRequest.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTid(UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.build();

		return callAsync(request, connectorServiceStub::getOrganization)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting Organization: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getOrganizationDTO());
				});
	}

	@Override
	public CompletableFuture<List<WaypointDTO>> getWaypointsByTaskId(String id) {
		var request = ConnectorGetWaypointsByTaskId.newBuilder()
				.setBase(RequestBase.newBuilder()
						.setTimestamp(ProtobufHelpers.now())
						.setTid(UUID.randomUUID().toString())
						.build())
				.setTaskId(id)
				.build();

		return callAsync(request, connectorServiceStub::getWaypointsByTaskId)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting Waypoints: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getWaypointDTOList().getWaypointsList());
				});
	}
}
