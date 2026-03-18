package com.zqnt.sdk.edge.connector.application.impl;

import com.zqnt.utils.common.proto.RequestBase;
import com.zqnt.sdk.edge.application.ProtoJsonMapper;
import com.zqnt.sdk.edge.connector.application.ConnectorService;
import com.zqnt.utils.connector.proto.*;
import com.zqnt.utils.asset.domains.AssetDTO;
import com.zqnt.utils.asset.domains.SubAssetDTO;
import com.zqnt.utils.core.ProtobufHelpers;
import com.zqnt.utils.missionautonomy.domains.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@Slf4j
public class ConnectorServiceImpl implements ConnectorService {

	private static final int INITIAL_RETRY_DELAY_SECONDS = 1;
	private static final int MAX_RETRY_DELAY_SECONDS = 30;
	private static final int MAX_RETRY_ATTEMPTS = 5;

	private final ProtoJsonMapper protoJsonMapper;
	private final ConnectorServiceGrpc.ConnectorServiceStub connectorServiceStub;
	private final ScheduledExecutorService retryScheduler = Executors.newScheduledThreadPool(2);

	public ConnectorServiceImpl(ProtoJsonMapper protoJsonMapper, ConnectorServiceGrpc.ConnectorServiceStub connectorServiceStub) {
		this.protoJsonMapper = protoJsonMapper;
		this.connectorServiceStub = connectorServiceStub;
	}

	private boolean shouldReconnect(Throwable t) {
		Status status = Status.fromThrowable(t);
		if (status == null) {
			return true;
		}
		return switch (status.getCode()) {
			case UNAVAILABLE,
				DEADLINE_EXCEEDED,
				RESOURCE_EXHAUSTED,
				INTERNAL,
				UNKNOWN -> true;
			case UNAUTHENTICATED,
				PERMISSION_DENIED,
				FAILED_PRECONDITION,
				UNIMPLEMENTED,
				DATA_LOSS -> false;
			default -> true;
		};
	}

	private int computeNextDelay(int attempts) {
		int next = INITIAL_RETRY_DELAY_SECONDS * (1 << Math.min(attempts - 1, 5)); // max 32x base
		next = Math.min(next, MAX_RETRY_DELAY_SECONDS);
		int jitter = ThreadLocalRandom.current().nextInt(0, Math.max(1, next / 4));
		return next + jitter;
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

	/**
	 * Helper method to wrap gRPC async calls with automatic retry on transient failures
	 */
	private <REQ, RES> CompletableFuture<RES> callAsyncWithRetry(
			REQ request,
			BiConsumer<REQ, StreamObserver<RES>> grpcMethod) {
		return callAsyncWithRetry(request, grpcMethod, 1);
	}

	private <REQ, RES> CompletableFuture<RES> callAsyncWithRetry(
			REQ request,
			BiConsumer<REQ, StreamObserver<RES>> grpcMethod,
			int attempt) {

		CompletableFuture<RES> future = new CompletableFuture<>();

		grpcMethod.accept(request, new StreamObserver<RES>() {
			private RES response;

			@Override
			public void onNext(RES value) {
				response = value;
			}

			@Override
			public void onError(Throwable t) {
				if (attempt < MAX_RETRY_ATTEMPTS && shouldReconnect(t)) {
					int nextDelay = computeNextDelay(attempt);
					log.warn("gRPC call failed (attempt {}/{}). Retrying in {}s: {}", attempt, MAX_RETRY_ATTEMPTS, nextDelay, t.getMessage());
					retryScheduler.schedule(() ->
							callAsyncWithRetry(request, grpcMethod, attempt + 1).whenComplete((res, ex) -> {
								if (ex != null) {
									future.completeExceptionally(ex);
								} else {
									future.complete(res);
								}
							}),
							nextDelay, TimeUnit.SECONDS);
				} else {
					log.error("gRPC call failed after {} attempts: {}", attempt, t.getMessage());
					future.completeExceptionally(t);
				}
			}

			@Override
			public void onCompleted() {
				future.complete(response);
			}
		});

		return future;
	}

	public void shutdown() {
		retryScheduler.shutdown();
		try {
			if (!retryScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				retryScheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			retryScheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
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

		return callAsyncWithRetry(request, connectorServiceStub::getAssetBySn)
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

		return callAsyncWithRetry(request, connectorServiceStub::getAssetById)
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

		return callAsyncWithRetry(request, connectorServiceStub::getSubAssetBySn)
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

		return callAsyncWithRetry(request, connectorServiceStub::updateAsset)
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

		return callAsyncWithRetry(request, connectorServiceStub::registerAsset)
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

		return callAsyncWithRetry(request, connectorServiceStub::deRegisterAsset)
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

		return callAsyncWithRetry(request, connectorServiceStub::getMission)
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

		return callAsyncWithRetry(request, connectorServiceStub::createMission)
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

		return callAsyncWithRetry(request, connectorServiceStub::updateMission)
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

		return callAsyncWithRetry(request, connectorServiceStub::deleteMission)
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

		return callAsyncWithRetry(request, connectorServiceStub::getTask)
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

		return callAsyncWithRetry(request, connectorServiceStub::createTask)
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

		return callAsyncWithRetry(request, connectorServiceStub::updateTask)
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

		return callAsyncWithRetry(request, connectorServiceStub::deleteTask)
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

		return callAsyncWithRetry(request, connectorServiceStub::getTaskByFlightId)
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

		return callAsyncWithRetry(request, connectorServiceStub::getScheduler)
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

		return callAsyncWithRetry(request, connectorServiceStub::createScheduler)
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

		return callAsyncWithRetry(request, connectorServiceStub::updateScheduler)
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

		return callAsyncWithRetry(request, connectorServiceStub::deleteScheduler)
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

		return callAsyncWithRetry(request, connectorServiceStub::getOrganization)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting Organization: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getOrganizationDTO());
				});
	}


}
