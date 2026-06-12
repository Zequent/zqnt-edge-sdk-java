package com.zqnt.sdk.edge.livedata.application;

import com.google.protobuf.Timestamp;
import com.zqnt.sdk.edge.adapter.domains.NotificationRequestData;
import com.zqnt.utils.common.proto.RequestBase;
import com.zqnt.utils.core.ProtobufHelpers;
import com.zqnt.utils.livedata.proto.AssetStatusEvent;
import com.zqnt.utils.livedata.proto.OperationEvent;
import com.zqnt.utils.livedata.proto.ProduceNotificationRequest;
import com.zqnt.utils.livedata.proto.TaskEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Mapper for converting notification POJO data to Proto messages and vice versa.
 */
public class NotificationMapper {

	public NotificationRequestData map(ProduceNotificationRequest request) {
		if (request == null) return null;

		NotificationRequestData data = new NotificationRequestData();
		data.setSn(request.getBase().getSn());
		data.setTid(request.getBase().getTid());
		data.setTimestamp(toLocalDateTime(request.getBase().getTimestamp()));

		switch (request.getEventCase()) {
			case ASSET_STATUS -> data.setAssetStatusEvent(map(request.getAssetStatus()));
			case TASK_EVENT -> data.setTaskEvent(map(request.getTaskEvent()));
			case OPERATION_EVENT -> data.setOperationEvent(map(request.getOperationEvent()));
			default -> { /* no-op for ERROR or EVENT_NOT_SET */ }
		}

		return data;
	}

	public ProduceNotificationRequest map(NotificationRequestData requestData) {
		if (requestData == null) return null;

		RequestBase.Builder baseBuilder = RequestBase.newBuilder()
				.setSn(requestData.getSn() != null ? requestData.getSn() : "")
				.setTid(requestData.getTid() != null ? requestData.getTid() : "");

		if (requestData.getTimestamp() != null) {
			baseBuilder.setTimestamp(toTimestamp(requestData.getTimestamp()));
		} else {
			baseBuilder.setTimestamp(ProtobufHelpers.now());
		}

		ProduceNotificationRequest.Builder builder = ProduceNotificationRequest.newBuilder()
				.setBase(baseBuilder.build());

		if (requestData.getAssetStatusEvent() != null) {
			builder.setAssetStatus(map(requestData.getAssetStatusEvent()));
		} else if (requestData.getTaskEvent() != null) {
			builder.setTaskEvent(map(requestData.getTaskEvent()));
		} else if (requestData.getOperationEvent() != null) {
			builder.setOperationEvent(map(requestData.getOperationEvent()));
		}

		return builder.build();
	}

	private NotificationRequestData.AssetStatusEventData map(AssetStatusEvent proto) {
		return NotificationRequestData.AssetStatusEventData.builder()
				.sn(proto.getSn())
				.assetId(proto.hasAssetId() ? proto.getAssetId() : null)
				.online(proto.getOnline())
				.build();
	}

	private AssetStatusEvent map(NotificationRequestData.AssetStatusEventData data) {
		AssetStatusEvent.Builder builder = AssetStatusEvent.newBuilder()
				.setSn(data.getSn() != null ? data.getSn() : "")
				.setOnline(data.isOnline());
		if (data.getAssetId() != null) builder.setAssetId(data.getAssetId());
		return builder.build();
	}

	private NotificationRequestData.TaskEventData map(TaskEvent proto) {
		return NotificationRequestData.TaskEventData.builder()
				.taskId(proto.getTaskId())
				.taskType(proto.getTaskType())
				.status(proto.getStatus())
				.progress(proto.hasProgress() ? proto.getProgress() : null)
				.message(proto.hasMessage() ? proto.getMessage() : null)
				.externalTaskType(proto.hasExternalTaskType() ? proto.getExternalTaskType() : null)
				.build();
	}

	private TaskEvent map(NotificationRequestData.TaskEventData data) {
		TaskEvent.Builder builder = TaskEvent.newBuilder()
				.setTaskId(data.getTaskId() != null ? data.getTaskId() : "");
		if (data.getTaskType() != null) builder.setTaskType(data.getTaskType());
		if (data.getStatus() != null) builder.setStatus(data.getStatus());
		if (data.getProgress() != null) builder.setProgress(data.getProgress());
		if (data.getMessage() != null) builder.setMessage(data.getMessage());
		if (data.getExternalTaskType() != null) builder.setExternalTaskType(data.getExternalTaskType());
		return builder.build();
	}

	private NotificationRequestData.OperationEventData map(OperationEvent proto) {
		return NotificationRequestData.OperationEventData.builder()
				.operationId(proto.getOperationId())
				.missionType(proto.getMissionType())
				.status(proto.getStatus())
				.message(proto.hasMessage() ? proto.getMessage() : null)
				.build();
	}

	private OperationEvent map(NotificationRequestData.OperationEventData data) {
		OperationEvent.Builder builder = OperationEvent.newBuilder()
				.setOperationId(data.getOperationId() != null ? data.getOperationId() : "");
		if (data.getMissionType() != null) builder.setMissionType(data.getMissionType());
		if (data.getStatus() != null) builder.setStatus(data.getStatus());
		if (data.getMessage() != null) builder.setMessage(data.getMessage());
		return builder.build();
	}

	private LocalDateTime toLocalDateTime(Timestamp timestamp) {
		if (timestamp == null) return null;
		Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	private Timestamp toTimestamp(LocalDateTime localDateTime) {
		if (localDateTime == null) return null;
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Timestamp.newBuilder()
				.setSeconds(instant.getEpochSecond())
				.setNanos(instant.getNano())
				.build();
	}
}
