package com.zequent.framework.edge.sdk.interfaces.impl;

import com.zequent.framework.edge.sdk.interfaces.MissionAutonomyService;
import com.zequent.framework.edge.sdk.mapper.ProtoJsonMapper;
import com.zequent.framework.services.mission.proto.*;
import com.zequent.framework.utils.missionautonomy.dto.MissionDTO;
import com.zequent.framework.utils.missionautonomy.dto.SchedulerDTO;
import com.zequent.framework.utils.missionautonomy.dto.TaskDTO;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
public class MissionAutonomyServiceImpl implements MissionAutonomyService {

	private final MissionAutonomyServiceGrpc.MissionAutonomyServiceStub missionAutonomyServiceStub;
	private final ProtoJsonMapper protoJsonMapper;

	public MissionAutonomyServiceImpl(MissionAutonomyServiceGrpc.MissionAutonomyServiceStub missionAutonomyServiceStub, ProtoJsonMapper protoJsonMapper) {
		this.missionAutonomyServiceStub = missionAutonomyServiceStub;
        this.protoJsonMapper = protoJsonMapper;
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
	public CompletableFuture<MissionDTO> createMission(CreateMissionRequest createMissionRequest) {
		return callAsync(createMissionRequest, missionAutonomyServiceStub::createMission)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error creating mission: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getMissionDTO());
				})
				.exceptionally(t -> {
					log.error("Error creating mission", t);
					return null;
				});
	}

	@Override
	public CompletableFuture<MissionDTO> updateMission(UpdateMissionRequest updateMissionRequest) {
        // TODO: Map response to MissionData
        return callAsync(updateMissionRequest, missionAutonomyServiceStub::updateMission)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error updating mission: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getMissionDTO());
				})
				.exceptionally(t -> {
					log.error("Error updating mission", t);
					return null;
				});
	}

	@Override
	public CompletableFuture<MissionDTO> getMission(GetMissionRequest getRequest) {
		return callAsync(getRequest, missionAutonomyServiceStub::getMission)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting mission: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getMissionDTO());
				})
				.exceptionally(t -> {
					log.error("Error getting mission", t);
					return null;
				});
	}

	@Override
	public CompletableFuture<TaskDTO> getTask(GetTaskRequest getTaskRequest) {
		return callAsync(getTaskRequest, missionAutonomyServiceStub::getTask)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting task: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getTaskDTO());
				})
				.exceptionally(t -> {
					log.error("Error getting task", t);
					return null;
				});
	}

	@Override
	public CompletableFuture<TaskDTO> getTaskByFlightId(GetTaskRequest getTaskRequest) {
		return callAsync(getTaskRequest, missionAutonomyServiceStub::getTaskByFlightId)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting task by flight id: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getTaskDTO());
				})
				.exceptionally(t -> {
					log.error("Error getting task by flight id", t);
					return null;
				});
	}

	@Override
	public CompletableFuture<SchedulerDTO> getScheduler(GetSchedulerRequest getSchedulerRequest) {
		return callAsync(getSchedulerRequest, missionAutonomyServiceStub::getScheduler)
				.thenApply(response -> {
					if (response.getHasErrors()) {
						log.error("Error getting scheduler: {}", response.getError());
						return null;
					}
					return protoJsonMapper.map(response.getSchedulerDTO());
				})
				.exceptionally(t -> {
					log.error("Error getting scheduler", t);
					return null;
				});
	}
}
