package com.zequent.framework.edge.sdk.mapper;

import com.google.protobuf.Timestamp;
import com.zequent.framework.services.livedata.proto.AssetTelemetry;
import com.zequent.framework.services.livedata.proto.ProduceTelemetryRequest;
import com.zequent.framework.services.livedata.proto.SubAssetTelemetry;
import com.zequent.framework.utils.edge.sdk.dto.AssetTelemetryData;
import com.zequent.framework.utils.edge.sdk.dto.SubAssetTelemetryData;
import com.zequent.framework.edge.sdk.models.TelemetryRequestData;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


/**
 * Mapper for converting POJO telemetry data to Proto messages.
 * Handles all field mappings including nested objects and enums.
 */
@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TelemetryMapper {

	TelemetryRequestData map(ProduceTelemetryRequest request);
	@Mapping(target = "base.sn", source = "sn")
	@Mapping(target = "base.tid", source = "tid")
	@Mapping(target = "base.timestamp", source = "timestamp")
	ProduceTelemetryRequest map(TelemetryRequestData requestData);

	AssetTelemetry map(AssetTelemetryData telemetryData);
	SubAssetTelemetry map(SubAssetTelemetryData telemetryData);

	AssetTelemetryData map(AssetTelemetry telemetry);
	SubAssetTelemetryData map(SubAssetTelemetry telemetry);



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
