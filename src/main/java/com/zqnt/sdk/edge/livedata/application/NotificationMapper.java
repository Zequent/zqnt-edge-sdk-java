package com.zqnt.sdk.edge.livedata.application;

import com.zqnt.sdk.edge.adapter.domains.NotificationRequestData;
import com.zqnt.sdk.edge.support.MapperSupport;
import com.zqnt.utils.livedata.proto.AssetStatusEvent;
import com.zqnt.utils.livedata.proto.OperationEvent;
import com.zqnt.utils.livedata.proto.ProduceNotificationRequest;
import com.zqnt.utils.livedata.proto.TaskEvent;

/**
 * Mapper for converting notification POJO data to Proto messages and vice versa.
 */
public class NotificationMapper {

	public NotificationRequestData map(ProduceNotificationRequest request) {
		if (request == null) return null;

		NotificationRequestData data = new NotificationRequestData();
		data.setSeverity(request.getSeverity());
		data.setEventType(request.getEventType());
		if (request.hasBase()) {
			data.setSn(request.getBase().getSn());
			data.setTid(request.getBase().getTid());
			data.setTimestamp(MapperSupport.toLocalDateTime(request.getBase().getTimestamp()));
		}

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

		var baseBuilder = MapperSupport.requestBase(requestData.getSn(), requestData.getTid(), requestData.getTimestamp());

		ProduceNotificationRequest.Builder builder = ProduceNotificationRequest.newBuilder()
				.setBase(baseBuilder.build());
		builder.setSeverity(requestData.getSeverity());
		builder.setEventType(requestData.getEventType());
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
				.message(proto.hasMessage() ? proto.getMessage() : null)
				.build();
	}

	private AssetStatusEvent map(NotificationRequestData.AssetStatusEventData data) {
		AssetStatusEvent.Builder builder = AssetStatusEvent.newBuilder()
				.setSn(MapperSupport.defaultString(data.getSn()))
				.setOnline(data.isOnline())
				.setMessage(data.getMessage());
		MapperSupport.set(builder::setAssetId, data.getAssetId());
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
				.setTaskId(MapperSupport.defaultString(data.getTaskId()));
		MapperSupport.set(builder::setTaskType, data.getTaskType());
		MapperSupport.set(builder::setStatus, data.getStatus());
		MapperSupport.set(builder::setProgress, data.getProgress());
		MapperSupport.set(builder::setMessage, data.getMessage());
		MapperSupport.set(builder::setExternalTaskType, data.getExternalTaskType());
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
				.setOperationId(MapperSupport.defaultString(data.getOperationId()));
		MapperSupport.set(builder::setMissionType, data.getMissionType());
		MapperSupport.set(builder::setStatus, data.getStatus());
		MapperSupport.set(builder::setMessage, data.getMessage());
		return builder.build();
	}
}
