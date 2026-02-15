package com.zequent.framework.edge.sdk.interfaces.impl;

import com.zequent.framework.common.proto.SchedulerProtoDTO;
import com.zequent.framework.common.proto.TaskProtoDTO;
import com.zequent.framework.edge.sdk.interfaces.MissionAutonomyService;
import com.zequent.framework.edge.sdk.models.MissionData;
import com.zequent.framework.services.mission.proto.*;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MissionAutonomyServiceImpl implements MissionAutonomyService {

	private final MutinyMissionAutonomyServiceGrpc.MutinyMissionAutonomyServiceStub missionAutonomyServiceStub;

	public MissionAutonomyServiceImpl(MutinyMissionAutonomyServiceGrpc.MutinyMissionAutonomyServiceStub missionAutonomyServiceStub) {
		this.missionAutonomyServiceStub = missionAutonomyServiceStub;
	}


	@Override
	public Uni<MissionData> createMission(CreateMissionRequest createMissionRequest) {
		return null;
	}

	@Override
	public Uni<MissionData> updateMission(UpdateMissionRequest updateMissionRequest) {
		return null;
	}

	@Override
	public Uni<MissionData> getMission(GetMissionRequest getRequest) {
		return null;
	}

	@Override
	public Uni<TaskProtoDTO> getTask(GetTaskRequest getTaskRequest) {
		return null;
	}

	@Override
	public Uni<TaskProtoDTO> getTaskByFlightId(GetTaskRequest getTaskRequest) {
		return null;
	}

	@Override
	public Uni<SchedulerProtoDTO> getScheduler(GetSchedulerRequest getSchedulerRequest) {
		return null;
	}
}
