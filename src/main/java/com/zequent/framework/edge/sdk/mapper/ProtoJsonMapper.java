package com.zequent.framework.edge.sdk.mapper;


import com.google.protobuf.Timestamp;
import com.zequent.framework.common.proto.*;
import com.zequent.framework.edge.sdk.models.*;
import com.zequent.framework.edge.sdk.models.Coordinates;
import com.zequent.framework.edge.sdk.models.ReturnToHomeRequest;
import com.zequent.framework.sdks.edge.proto.*;
import com.zequent.framework.utils.missionautonomy.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE,  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProtoJsonMapper {


	Coordinates map(com.zequent.framework.common.proto.Coordinates coordinates);

	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "tid", source = "base.tid")
	@Mapping(target = "coordinates", source = "request")
	TakeOffRequest map(EdgeTakeOffRequest request);


	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "tid", source = "base.tid")
	@Mapping(target = "coordinates", source = "request")
	GoToRequest map(EdgeGoToRequest request);

	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "tid", source = "base.tid")
	@Mapping(target = "videoId", source = "request.videoId")
	@Mapping(target = "streamServer", source = "request.streamServer")
	@Mapping(target = "videoType", source = "request.streamType")
	LiveStreamStartRequest map(EdgeStartLiveStreamRequest request);

	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "tid", source = "base.tid")
	LiveStreamStopRequest map(EdgeStopLiveStreamRequest request);

	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "tid", source = "base.tid")
	ReturnToHomeRequest map(EdgeReturnToHomeRequest request);

	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "lens", source = "request.lens")
	@Mapping(target = "videoId", ignore = true)
	ChangeLensRequest map(EdgeChangeCameraLensRequest request);

	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "lens", source = "request.lens")
	@Mapping(target = "zoom", source = "request.zoom")
	@Mapping(target = "payloadIndex", ignore = true)
	ChangeZoomRequest map(EdgeChangeCameraZoomRequest request);

	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "roll", source = "request.roll")
	@Mapping(target = "pitch", source = "request.pitch")
	@Mapping(target = "yaw", source = "request.yaw")
	@Mapping(target = "throttle", source = "request.throttle")
	@Mapping(target = "gimbalPitch", source = "request.gimbalPitch")
	com.zequent.framework.edge.sdk.models.ManualControlInput map(EdgeManualControlInputRequest request);


	SubAssetDTO map(SubAssetProtoDTO subAssetProtoDTO);

	SubAssetProtoDTO map(SubAssetDTO subAssetDTO);

	AssetDTO map(AssetProtoDTO assetProtoDTO);

	AssetProtoDTO map(AssetDTO assetDTO);
	
	OrganizationDTO map(OrganizationProtoDTO organizationProtoDTO);

	OrganizationProtoDTO map(OrganizationDTO organizationDTO);
	
	MissionDTO map(MissionProtoDTO missionProtoDTO);

	MissionProtoDTO map(MissionDTO missionDTO);

	TaskDTO map(TaskProtoDTO taskProtoDTO);

	TaskProtoDTO map(TaskDTO taskDTO);

	SchedulerDTO map(SchedulerProtoDTO schedulerProtoDTO);

	SchedulerProtoDTO map(SchedulerDTO schedulerDTO);

	WaypointDTO map(WaypointProtoDTO waypointProtoDTO);

	List<WaypointDTO> map(List<WaypointProtoDTO> waypointProtoDTOList);

	@Mapping(target = "sn", source = "base.sn")
	@Mapping(target = "altitude", source = "request.altitude")
	@Mapping(target = "latitude", source = "request.latitude")
	@Mapping(target = "longitude", source = "request.longitude")
	@Mapping(target = "locked", source = "locked")
	@Mapping(target = "payloadIndex", source = "payloadIndex")
	LookAtRequest map(EdgeLookAtRequest request);

	default LocalDateTime toLocalDateTime(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}


	default Timestamp toTimestamp(LocalDateTime localDateTime){
		if (localDateTime == null) {
			return null;
		}
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Timestamp.newBuilder()
				.setSeconds(instant.getEpochSecond())
				.setNanos(instant.getNano())
				.build();
	}


}
