package com.zqnt.sdk.edge.application;

import com.google.protobuf.Timestamp;
import com.zequent.framework.common.proto.*;
import com.zqnt.sdk.edge.adapter.domains.ChangeLensRequest;
import com.zqnt.sdk.edge.adapter.domains.ChangeZoomRequest;
import com.zqnt.sdk.edge.adapter.domains.Coordinates;
import com.zqnt.sdk.edge.adapter.domains.GoToRequest;
import com.zqnt.sdk.edge.adapter.domains.LiveStreamStartRequest;
import com.zqnt.sdk.edge.adapter.domains.LiveStreamStopRequest;
import com.zqnt.sdk.edge.adapter.domains.LookAtRequest;
import com.zqnt.sdk.edge.adapter.domains.ManualControlInput;
import com.zqnt.sdk.edge.adapter.domains.ReturnToHomeRequest;
import com.zqnt.sdk.edge.adapter.domains.TakeOffRequest;
import com.zequent.framework.sdks.edge.proto.*;
import com.zequent.framework.utils.missionautonomy.dto.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Simple mapper implementation for converting between Proto and POJO models
 */
public class ProtoJsonMapper {

    // Edge Request Mappings

    public Coordinates map(com.zequent.framework.common.proto.Coordinates proto) {
        if (proto == null) return null;
        return new Coordinates(proto.getLatitude(), proto.getLongitude(), proto.getAltitude());
    }

    public TakeOffRequest map(EdgeTakeOffRequest request) {
        if (request == null) return null;
        return TakeOffRequest.builder()
                .sn(request.getBase().getSn())
                .tid(request.getBase().getTid())
                .coordinates(map(request.getRequest()))
                .build();
    }

    public GoToRequest map(EdgeGoToRequest request) {
        if (request == null) return null;
        return GoToRequest.builder()
                .sn(request.getBase().getSn())
                .tid(request.getBase().getTid())
                .coordinates(map(request.getRequest()))
                .build();
    }

    public ReturnToHomeRequest map(EdgeReturnToHomeRequest request) {
        if (request == null) return null;
        return ReturnToHomeRequest.builder()
                .sn(request.getBase().getSn())
                .tid(request.getBase().getTid())
                .build();
    }

    public LiveStreamStartRequest map(EdgeStartLiveStreamRequest request) {
        if (request == null) return null;
        return LiveStreamStartRequest.builder()
                .sn(request.getBase().getSn())
                .tid(request.getBase().getTid())
                .videoId(request.getRequest().getVideoId())
                .streamServer(request.getRequest().getStreamServer())
                .videoType(request.getRequest().getStreamType().name())
                .build();
    }

    public LiveStreamStopRequest map(EdgeStopLiveStreamRequest request) {
        if (request == null) return null;
        return LiveStreamStopRequest.builder()
                .sn(request.getBase().getSn())
                .tid(request.getBase().getTid())
                .videoId(request.getRequest().getVideoId())
                .build();
    }

    public ChangeLensRequest map(EdgeChangeCameraLensRequest request) {
        if (request == null) return null;
        return ChangeLensRequest.builder()
                .sn(request.getBase().getSn())
                .lens(request.getRequest().getLens())
                .build();
    }

    public ChangeZoomRequest map(EdgeChangeCameraZoomRequest request) {
        if (request == null) return null;
        return ChangeZoomRequest.builder()
                .sn(request.getBase().getSn())
                .lens(request.getRequest().getLens())
                .zoom((float) request.getRequest().getZoom())
                .build();
    }

    public LookAtRequest map(EdgeLookAtRequest request) {
        if (request == null) return null;
        return LookAtRequest.builder()
                .sn(request.getBase().getSn())
                .latitude(request.getRequest().getLatitude())
                .longitude(request.getRequest().getLongitude())
                .altitude((float) request.getRequest().getAltitude())
                .locked(request.getLocked())
                .payloadIndex(request.getPayloadIndex())
                .build();
    }

    public ManualControlInput map(EdgeManualControlInputRequest request) {
        if (request == null) return null;
        return ManualControlInput.builder()
                .sn(request.getBase().getSn())
                .roll(request.getRequest().getRoll())
                .pitch(request.getRequest().getPitch())
                .yaw(request.getRequest().getYaw())
                .throttle(request.getRequest().getThrottle())
                .gimbalPitch(request.getRequest().getGimbalPitch())
                .build();
    }

    // Asset/Mission DTO Mappings

    public SubAssetDTO map(SubAssetProtoDTO proto) {
        if (proto == null) return null;

        SubAssetDTO.SubAssetDTOBuilder builder = SubAssetDTO.builder();
        builder.sn(proto.getSn());
        builder.name(proto.getName());
        builder.type(proto.getType());
        builder.vendor(proto.getVendor());
        builder.model(proto.getModel());
        builder.connection(proto.getConnection());

        if (proto.hasConnectionString()) {
            builder.connectionString(proto.getConnectionString());
        }
        if (proto.hasPort()) {
            builder.port(proto.getPort());
        }
        if (proto.hasLiveStreamServer()) {
            builder.liveStreamServer(proto.getLiveStreamServer());
        }
        if (proto.hasExternalDeviceType()) {
            builder.externalDeviceType(proto.getExternalDeviceType());
        }
        if (proto.hasExternalDeviceSubType()) {
            builder.externalDeviceSubType(proto.getExternalDeviceSubType());
        }
        if (proto.hasExternalId()) {
            builder.externalId(proto.getExternalId());
        }

        return builder.build();
    }

    public SubAssetProtoDTO map(SubAssetDTO dto) {
        if (dto == null) return null;

        SubAssetProtoDTO.Builder builder = SubAssetProtoDTO.newBuilder();

        if (dto.getId() != null) {
            builder.setId(dto.getId().toString());
        }
        if (dto.getSn() != null) {
            builder.setSn(dto.getSn());
        }
        if (dto.getName() != null) {
            builder.setName(dto.getName());
        }
        if (dto.getType() != null) {
            builder.setType(dto.getType());
        }
        if (dto.getVendor() != null) {
            builder.setVendor(dto.getVendor());
        }
        if (dto.getConnection() != null) {
            builder.setConnection(dto.getConnection());
        }
        if (dto.getConnectionString() != null) {
            builder.setConnectionString(dto.getConnectionString());
        }
        if (dto.getModel() != null) {
            builder.setModel(dto.getModel());
        }
        if (dto.getPort() != null) {
            builder.setPort(dto.getPort());
        }
        if (dto.getLiveStreamServer() != null) {
            builder.setLiveStreamServer(dto.getLiveStreamServer());
        }
        if (dto.getExternalDeviceType() != null) {
            builder.setExternalDeviceType(dto.getExternalDeviceType());
        }
        if (dto.getExternalDeviceSubType() != null) {
            builder.setExternalDeviceSubType(dto.getExternalDeviceSubType());
        }
        if (dto.getExternalId() != null) {
            builder.setExternalId(dto.getExternalId());
        }

        return builder.build();
    }

    public AssetDTO map(AssetProtoDTO proto) {
        if (proto == null) return null;

        AssetDTO.AssetDTOBuilder builder = AssetDTO.builder();

        builder.sn(proto.getSn());

        builder.name(proto.getName());

        builder.type(proto.getType());

        builder.vendor(proto.getVendor());

        builder.connection(proto.getConnection());

        builder.model(proto.getModel());

        if (proto.hasConnectionString()) {
            builder.connectionString(proto.getConnectionString());
        }
        if (proto.hasLiveStreamServer()) {
            builder.liveStreamServer(proto.getLiveStreamServer());
        }
        if (proto.hasExternalId()) {
            builder.externalId(proto.getExternalId());
        }
        if (proto.hasExternalDeviceType()) {
            builder.externalDeviceType(proto.getExternalDeviceType());
        }
        if (proto.hasExternalDeviceSubType()) {
            builder.externalDeviceSubType(proto.getExternalDeviceSubType());
        }
        if (proto.hasSubAssetDTO()) {
            builder.subAsset(map(proto.getSubAssetDTO()));
        }
        builder.organization(UUID.fromString(proto.getOrganization()));


        return builder.build();
    }

    public AssetProtoDTO map(AssetDTO dto) {
        if (dto == null) return null;

        AssetProtoDTO.Builder builder = AssetProtoDTO.newBuilder();

        if (dto.getId() != null) {
            builder.setId(dto.getId().toString());
        }

        if (dto.getSn() != null) {
            builder.setSn(dto.getSn());
        }
        if (dto.getName() != null) {
            builder.setName(dto.getName());
        }
        if (dto.getType() != null) {
            builder.setType(dto.getType());
        }
        if (dto.getVendor() != null) {
            builder.setVendor(dto.getVendor());
        }
        if (dto.getConnection() != null) {
            builder.setConnection(dto.getConnection());
        }
        if (dto.getModel() != null) {
            builder.setModel(dto.getModel());
        }
        if (dto.getConnectionString() != null) {
            builder.setConnectionString(dto.getConnectionString());
        }
        if (dto.getLiveStreamServer() != null) {
            builder.setLiveStreamServer(dto.getLiveStreamServer());
        }
        if (dto.getExternalId() != null) {
            builder.setExternalId(dto.getExternalId());
        }
        if (dto.getExternalDeviceType() != null) {
            builder.setExternalDeviceType(dto.getExternalDeviceType());
        }
        if (dto.getExternalDeviceSubType() != null) {
            builder.setExternalDeviceSubType(dto.getExternalDeviceSubType());
        }
        if (dto.getSubAsset() != null) {
            builder.setSubAssetDTO(map(dto.getSubAsset()));
        }
        if (dto.getOrganization() != null) {
            builder.setOrganization(dto.getOrganization().toString());
        }

        return builder.build();
    }

    public OrganizationDTO map(OrganizationProtoDTO proto) {
        if (proto == null) return null;

        OrganizationDTO.OrganizationDTOBuilder builder = OrganizationDTO.builder();
        builder.id(UUID.fromString(proto.getId()));
        builder.name(proto.getName());
        builder.description(proto.getDescription());
        return builder.build();
    }

    public OrganizationProtoDTO map(OrganizationDTO dto) {
        if (dto == null) return null;

        OrganizationProtoDTO.Builder builder = OrganizationProtoDTO.newBuilder();

        if (dto.getId() != null) {
            builder.setId(dto.getId().toString());
        }
        if (dto.getName() != null) {
            builder.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            builder.setDescription(dto.getDescription());
        }
        // Note: assets Set<UUID> would need proto repeated field mapping if available

        return builder.build();
    }

    public MissionDTO map(MissionProtoDTO proto) {
        if (proto == null) return null;

        MissionDTO.MissionDTOBuilder builder = MissionDTO.builder();
        builder.name(proto.getName());
        builder.description(proto.getDescription());
        builder.status(proto.getStatus());
        builder.type(proto.getType());

        if (proto.hasId()) {
            builder.id(UUID.fromString(proto.getId()));
        }
        if (proto.hasCreatedAt()) {
            builder.createdAt(toLocalDateTime(proto.getCreatedAt()));
        }
        if (proto.hasUpdatedAt()) {
            builder.modifiedAt(toLocalDateTime(proto.getUpdatedAt()));
        }
        if (proto.hasUpdatedUser()) {
            builder.modifiedFrom(proto.getUpdatedUser());
        }

        if (proto.hasGeoJson()) {
            builder.geoJson(proto.getGeoJson());
        }
        if (proto.hasStartDate()) {
            builder.startDate(toLocalDateTime(proto.getStartDate()));
        }
        if (proto.hasEndDate()) {
            builder.endDate(toLocalDateTime(proto.getEndDate()));
        }
        if (proto.hasUpdatedUser()) {
            builder.updatedUser(proto.getUpdatedUser());
        }
        // Note: assignedAssets Set<String> would need proto repeated field mapping if available

        return builder.build();
    }

    public MissionProtoDTO map(MissionDTO dto) {
        if (dto == null) return null;

        MissionProtoDTO.Builder builder = MissionProtoDTO.newBuilder();

        if (dto.getId() != null) {
            builder.setId(dto.getId().toString());
        }
        if (dto.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(dto.getCreatedAt()));
        }
        if (dto.getName() != null) {
            builder.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            builder.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            builder.setStatus(dto.getStatus());
        }
        if (dto.getType() != null) {
            builder.setType(dto.getType());
        }
        if (dto.getGeoJson() != null) {
            builder.setGeoJson(dto.getGeoJson());
        }
        if (dto.getStartDate() != null) {
            builder.setStartDate(toTimestamp(dto.getStartDate()));
        }
        if (dto.getEndDate() != null) {
            builder.setEndDate(toTimestamp(dto.getEndDate()));
        }
        if (dto.getUpdatedUser() != null) {
            builder.setUpdatedUser(dto.getUpdatedUser());
        }
        // Note: assignedAssets Set<String> would need proto repeated field mapping if available

        return builder.build();
    }

    public TaskDTO map(TaskProtoDTO proto) {
        if (proto == null) return null;

        TaskDTO.TaskDTOBuilder builder = TaskDTO.builder();
        builder.status(proto.getStatus());
        if (proto.hasId()) {
            builder.id(UUID.fromString(proto.getId()));
        }
        if (proto.hasCreatedAt()) {
            builder.createdAt(toLocalDateTime(proto.getCreatedAt()));
        }
        if (proto.hasMissionId()) {
            builder.missionId(UUID.fromString(proto.getMissionId()));
        }
        if (proto.hasName()) {
            builder.name(proto.getName());
        }
        if (proto.hasDescription()) {
            builder.description(proto.getDescription());
        }

        if (proto.hasAssetId()) {
            builder.assetId(proto.getAssetId());
        }
        if (proto.hasSnNumber()) {
            builder.snNumber(proto.getSnNumber());
        }
        if (proto.hasFlightId()) {
            builder.flightId(proto.getFlightId());
        }
        if (proto.hasFlyToWaylineMode()) {
            builder.flyToWaylineMode(proto.getFlyToWaylineMode());
        }
        if (proto.hasWaylineFinishAction()) {
            builder.waylineFinishAction(proto.getWaylineFinishAction());
        }
        if (proto.hasExitWaylineWhenRcLostEnum()) {
            builder.exitWaylineWhenRcLostEnum(proto.getExitWaylineWhenRcLostEnum());
        }
        if (proto.hasRcLostActionEnum()) {
            builder.rcLostActionEnum(proto.getRcLostActionEnum());
        }
        if (proto.hasTakeOffSecurityHeight()) {
            builder.takeOffSecurityHeight(proto.getTakeOffSecurityHeight());
        }
        if (proto.hasGlobalTransitionSpeed()) {
            builder.globalTransitionSpeed(proto.getGlobalTransitionSpeed());
        }
        if (proto.hasWaylineType()) {
            builder.waylineType(proto.getWaylineType());
        }
        if (proto.hasPayloadImagingType()) {
            builder.payloadImagingType(proto.getPayloadImagingType());
        }
        if (proto.hasWaylineTurnMode()) {
            builder.waylineTurnMode(proto.getWaylineTurnMode());
        }
        if (proto.hasUseStraightLine()) {
            builder.useStraightLine(proto.getUseStraightLine());
        }
        if (proto.hasGimbalPitchMode()) {
            builder.gimbalPitchMode(proto.getGimbalPitchMode());
        }
        if (proto.hasGlobalSpeed()) {
            builder.globalSpeed(proto.getGlobalSpeed());
        }
        if (proto.hasGlobalHeight()) {
            builder.globalHeight(proto.getGlobalHeight());
        }
        if (proto.hasFileUrl()) {
            builder.fileUrl(proto.getFileUrl());
        }
        if (proto.hasFileMd5()) {
            builder.fileMd5(proto.getFileMd5());
        }
        if (proto.hasFlightAreaFileUrl()) {
            builder.flightAreaFileUrl(proto.getFlightAreaFileUrl());
        }
        if (proto.hasFlightAreaChecksum()) {
            builder.flightAreaChecksum(proto.getFlightAreaChecksum());
        }
        if (proto.hasRthAltitude()) {
            builder.rthAltitude(proto.getRthAltitude());
        }
        if (proto.hasRthMode()) {
            builder.rthMode(proto.getRthMode());
        }
        if (proto.hasRthSpeed()) {
            builder.rthSpeed(proto.getRthSpeed());
        }
        if (proto.hasOutOfControlAction()) {
            builder.outOfControlAction(proto.getOutOfControlAction());
        }
        if (proto.hasWaylinePrecisionType()) {
            builder.waylinePrecisionType(proto.getWaylinePrecisionType());
        }
        if (proto.hasCurrentProgress()) {
            builder.currentProgress(proto.getCurrentProgress());
        }
        if (proto.hasCurrentStep()) {
            builder.currentStep(proto.getCurrentStep());
        }
        if (proto.hasBreakReason()) {
            builder.breakReason(proto.getBreakReason());
        }

        return builder.build();
    }

    public TaskProtoDTO map(TaskDTO dto) {
        if (dto == null) return null;

        TaskProtoDTO.Builder builder = TaskProtoDTO.newBuilder();

        if (dto.getId() != null) {
            builder.setId(dto.getId().toString());
        }
        if (dto.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(dto.getCreatedAt()));
        }
        if (dto.getMissionId() != null) {
            builder.setMissionId(dto.getMissionId().toString());
        }
        if (dto.getName() != null) {
            builder.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            builder.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            builder.setStatus(dto.getStatus());
        }
        if (dto.getAssetId() != null) {
            builder.setAssetId(dto.getAssetId());
        }
        if (dto.getSnNumber() != null) {
            builder.setSnNumber(dto.getSnNumber());
        }
        if (dto.getFlightId() != null) {
            builder.setFlightId(dto.getFlightId());
        }
        if (dto.getFlyToWaylineMode() != null) {
            builder.setFlyToWaylineMode(dto.getFlyToWaylineMode());
        }
        if (dto.getWaylineFinishAction() != null) {
            builder.setWaylineFinishAction(dto.getWaylineFinishAction());
        }
        if (dto.getExitWaylineWhenRcLostEnum() != null) {
            builder.setExitWaylineWhenRcLostEnum(dto.getExitWaylineWhenRcLostEnum());
        }
        if (dto.getRcLostActionEnum() != null) {
            builder.setRcLostActionEnum(dto.getRcLostActionEnum());
        }
        if (dto.getTakeOffSecurityHeight() != null) {
            builder.setTakeOffSecurityHeight(dto.getTakeOffSecurityHeight());
        }
        if (dto.getGlobalTransitionSpeed() != null) {
            builder.setGlobalTransitionSpeed(dto.getGlobalTransitionSpeed());
        }
        if (dto.getWaylineType() != null) {
            builder.setWaylineType(dto.getWaylineType());
        }
        if (dto.getPayloadImagingType() != null) {
            builder.setPayloadImagingType(dto.getPayloadImagingType());
        }
        if (dto.getWaylineTurnMode() != null) {
            builder.setWaylineTurnMode(dto.getWaylineTurnMode());
        }
        if (dto.getUseStraightLine() != null) {
            builder.setUseStraightLine(dto.getUseStraightLine());
        }
        if (dto.getGimbalPitchMode() != null) {
            builder.setGimbalPitchMode(dto.getGimbalPitchMode());
        }
        if (dto.getGlobalGimbalPitch() != null) {
            builder.setGlobalGimbalPitch(dto.getGlobalGimbalPitch());
        }
        if (dto.getGlobalSpeed() != null) {
            builder.setGlobalSpeed(dto.getGlobalSpeed());
        }
        if (dto.getGlobalHeight() != null) {
            builder.setGlobalHeight(dto.getGlobalHeight());
        }
        if (dto.getFileUrl() != null) {
            builder.setFileUrl(dto.getFileUrl());
        }
        if (dto.getFileMd5() != null) {
            builder.setFileMd5(dto.getFileMd5());
        }
        if (dto.getFlightAreaFileUrl() != null) {
            builder.setFlightAreaFileUrl(dto.getFlightAreaFileUrl());
        }
        if (dto.getFlightAreaChecksum() != null) {
            builder.setFlightAreaChecksum(dto.getFlightAreaChecksum());
        }
        if (dto.getRthAltitude() != null) {
            builder.setRthAltitude(dto.getRthAltitude());
        }
        if (dto.getRthMode() != null) {
            builder.setRthMode(dto.getRthMode());
        }
        if (dto.getRthSpeed() != null) {
            builder.setRthSpeed(dto.getRthSpeed());
        }
        if (dto.getOutOfControlAction() != null) {
            builder.setOutOfControlAction(dto.getOutOfControlAction());
        }
        if (dto.getWaylinePrecisionType() != null) {
            builder.setWaylinePrecisionType(dto.getWaylinePrecisionType());
        }
        if (dto.getCurrentProgress() != null) {
            builder.setCurrentProgress(dto.getCurrentProgress());
        }
        if (dto.getCurrentStep() != null) {
            builder.setCurrentStep(dto.getCurrentStep());
        }
        if (dto.getBreakReason() != null) {
            builder.setBreakReason(dto.getBreakReason());
        }

        return builder.build();
    }

    public SchedulerDTO map(SchedulerProtoDTO proto) {
        if (proto == null) return null;

        SchedulerDTO.SchedulerDTOBuilder builder = SchedulerDTO.builder();

        builder.id(UUID.fromString(proto.getId()));
        builder.name(proto.getName());

        if (proto.hasMissionId()) {
            builder.missionId(UUID.fromString(proto.getMissionId()));
        }
        if (proto.hasTaskId()) {
            builder.taskId(UUID.fromString(proto.getTaskId()));
        }
        builder.cronExpression(proto.getCronExpression());

        if (proto.hasActive()) {
            builder.active(proto.getActive());
        }
        builder.type(proto.getType());

        if (proto.hasClientTimeZone()) {
            builder.clientTimeZone(proto.getClientTimeZone());
        }

        return builder.build();
    }

    public SchedulerProtoDTO map(SchedulerDTO dto) {
        if (dto == null) return null;

        SchedulerProtoDTO.Builder builder = SchedulerProtoDTO.newBuilder();

        if (dto.getId() != null) {
            builder.setId(dto.getId().toString());
        }
        if (dto.getName() != null) {
            builder.setName(dto.getName());
        }
        if (dto.getMissionId() != null) {
            builder.setMissionId(dto.getMissionId().toString());
        }
        if (dto.getTaskId() != null) {
            builder.setTaskId(dto.getTaskId().toString());
        }
        if (dto.getCronExpression() != null) {
            builder.setCronExpression(dto.getCronExpression());
        }
        if (dto.getActive() != null) {
            builder.setActive(dto.getActive());
        }
        if (dto.getType() != null) {
            builder.setType(dto.getType());
        }
        if (dto.getClientTimeZone() != null) {
            builder.setClientTimeZone(dto.getClientTimeZone());
        }

        return builder.build();
    }

    public WaypointDTO map(WaypointProtoDTO proto) {
        if (proto == null) return null;

        WaypointDTO.WaypointDTOBuilder builder = WaypointDTO.builder();

        if (proto.hasId()) {
            builder.id(UUID.fromString(proto.getId()));
        }
            builder.latitude(proto.getLatitude());

            builder.longitude(proto.getLongitude());

        if (proto.hasAltitude()) {
            builder.altitude(proto.getAltitude());
        }
        if (proto.hasSpeed()) {
            builder.speed(proto.getSpeed());
        }
        if (proto.hasFlyTrough()) {
            builder.flyThrough(proto.getFlyTrough());
        }
        if (proto.hasVehicleAction()) {
            builder.vehicleAction(proto.getVehicleAction());
        }
        if (proto.hasWpOrder()) {
            builder.wpOrder(proto.getWpOrder());
        }
        if (proto.hasGimbalPitch()) {
            builder.gimbalPitch(proto.getGimbalPitch());
        }
        if (proto.hasTaskId()) {
            builder.task(UUID.fromString(proto.getTaskId()));
        }

        return builder.build();
    }

    public WaypointProtoDTO map(WaypointDTO dto) {
        if (dto == null) return null;

        WaypointProtoDTO.Builder builder = WaypointProtoDTO.newBuilder();

        if (dto.getId() != null) {
            builder.setId(dto.getId().toString());
        }

        if (dto.getLatitude() != null) {
            builder.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            builder.setLongitude(dto.getLongitude());
        }
        if (dto.getAltitude() != null) {
            builder.setAltitude(dto.getAltitude());
        }
        if (dto.getSpeed() != null) {
            builder.setSpeed(dto.getSpeed());
        }
        if (dto.getFlyThrough() != null) {
            builder.setFlyTrough(dto.getFlyThrough());
        }
        if (dto.getVehicleAction() != null) {
            builder.setVehicleAction(dto.getVehicleAction());
        }
        if (dto.getWpOrder() != null) {
            builder.setWpOrder(dto.getWpOrder());
        }
        if (dto.getGimbalPitch() != null) {
            builder.setGimbalPitch(dto.getGimbalPitch());
        }
        if (dto.getTask() != null) {
            builder.setTaskId(dto.getTask().toString());
        }

        return builder.build();
    }


    public List<WaypointDTO> map(List<WaypointProtoDTO> waypointProtos) {
        List<WaypointDTO> waypoints = new ArrayList<>();
        for (WaypointProtoDTO waypointProto : waypointProtos) {
            waypoints.add(map(waypointProto));
        }
        return waypoints;
    }

    public List<WaypointDTO> mapWaypointList(List<WaypointProtoDTO> protoList) {
        if (protoList == null) return null;
        List<WaypointDTO> dtoList = new ArrayList<>();
        for (WaypointProtoDTO proto : protoList) {
            dtoList.add(map(proto));
        }
        return dtoList;
    }

    public List<WaypointProtoDTO> mapWaypointProtoList(List<WaypointDTO> dtoList) {
        if (dtoList == null) return null;
        List<WaypointProtoDTO> protoList = new ArrayList<>();
        for (WaypointDTO dto : dtoList) {
            protoList.add(map(dto));
        }
        return protoList;
    }

    // Timestamp conversion utilities

    public LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public Timestamp toTimestamp(LocalDateTime localDateTime) {
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
