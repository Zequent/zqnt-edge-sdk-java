package com.zqnt.sdk.edge.adapter.domains;

import com.zqnt.utils.events.proto.NotificationEventType;
import com.zqnt.utils.events.proto.NotificationSeverity;
import com.zqnt.utils.mission.proto.MissionStatus;
import com.zqnt.utils.mission.proto.MissionType;
import com.zqnt.utils.mission.proto.TaskStatus;
import com.zqnt.utils.mission.proto.TaskTypeProto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestData {

	private String tid;
	private String sn;
	private LocalDateTime timestamp;

	// Only one of the following event fields should be set (oneof)
	private AssetStatusEventData assetStatusEvent;
	private TaskEventData taskEvent;
	private MissionEventData missionEvent;
	private NotificationSeverity severity;
	private NotificationEventType eventType;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AssetStatusEventData {
		private String sn;
		private String assetId;
		private boolean online;
		private String message;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TaskEventData {
		private String taskId;
		private TaskTypeProto taskType;
		private TaskStatus status;
		private Float progress;
		private String message;
		/** Set when taskType == TASK_TYPE_EXTERNAL; carries the edge-device-specific task name. */
		private String externalTaskType;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MissionEventData {
		private String missionId;
		private MissionType missionType;
		private MissionStatus status;
		private String message;
	}
}
