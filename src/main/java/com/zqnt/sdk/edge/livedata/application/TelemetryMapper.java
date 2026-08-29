package com.zqnt.sdk.edge.livedata.application;

import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import com.zqnt.sdk.edge.adapter.domains.TelemetryRequestData;
import com.zqnt.sdk.edge.support.MapperSupport;
import com.zqnt.utils.edge.sdk.domains.TelemetryData;
import com.zqnt.utils.livedata.proto.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Maps the unified telemetry domain model to and from its protobuf representation. */
public class TelemetryMapper {

    public TelemetryRequestData map(ProduceTelemetryRequest request) {
        if (request == null) return null;

        TelemetryRequestData data = new TelemetryRequestData();
        if (request.hasBase()) {
            data.setSn(request.getBase().getSn());
            data.setTid(request.getBase().getTid());
            data.setTimestamp(MapperSupport.toLocalDateTime(request.getBase().getTimestamp()));
        }
        if (request.hasData()) data.setTelemetry(map(request.getData()));
        return data;
    }

    public ProduceTelemetryRequest map(TelemetryRequestData requestData) {
        if (requestData == null) return null;

        ProduceTelemetryRequest.Builder builder = ProduceTelemetryRequest.newBuilder()
                .setBase(MapperSupport.requestBase(requestData.getSn(), requestData.getTid(), requestData.getTimestamp()));
        setIfNotNull(builder::setData, map(requestData.getTelemetry()));
        return builder.build();
    }

    public Telemetry map(TelemetryData data) {
        if (data == null) return null;

        Telemetry.Builder builder = Telemetry.newBuilder();
        setIfNotNull(builder::setId, data.getId());
        setIfNotNull(builder::setTimestamp, MapperSupport.toTimestamp(data.getTimestamp()));
        setIfNotNull(builder::setSn, data.getSn());
        setIfNotNull(builder::setLatitude, data.getLatitude());
        setIfNotNull(builder::setLongitude, data.getLongitude());
        setIfNotNull(builder::setAbsoluteAltitude, data.getAbsoluteAltitude());
        setIfNotNull(builder::setRelativeAltitude, data.getRelativeAltitude());
        setIfNotNull(builder::setWindSpeed, data.getWindSpeed());
        setIfNotNull(builder::setHeading, data.getHeading());
        setIfNotNull(builder::setAsset, map(data.getAsset()));
        setIfNotNull(builder::setSubAsset, map(data.getSubAsset()));
        return builder.build();
    }

    public TelemetryData map(Telemetry proto) {
        if (proto == null) return null;

        return TelemetryData.builder()
                .id(proto.getId())
                .timestamp(valueIf(proto::hasTimestamp, () -> MapperSupport.toLocalDateTime(proto.getTimestamp())))
                .sn(proto.getSn())
                .latitude(valueIf(proto::hasLatitude, proto::getLatitude))
                .longitude(valueIf(proto::hasLongitude, proto::getLongitude))
                .absoluteAltitude(valueIf(proto::hasAbsoluteAltitude, proto::getAbsoluteAltitude))
                .relativeAltitude(valueIf(proto::hasRelativeAltitude, proto::getRelativeAltitude))
                .windSpeed(valueIf(proto::hasWindSpeed, proto::getWindSpeed))
                .heading(valueIf(proto::hasHeading, proto::getHeading))
                .asset(valueIf(proto::hasAsset, () -> map(proto.getAsset())))
                .subAsset(valueIf(proto::hasSubAsset, () -> map(proto.getSubAsset())))
                .build();
    }

    private AssetTelemetryDetails map(TelemetryData.AssetDetails data) {
        if (data == null) return null;
        AssetTelemetryDetails.Builder builder = AssetTelemetryDetails.newBuilder();
        setIfNotNull(builder::setEnvironmentTemp, data.getEnvironmentTemp());
        setIfNotNull(builder::setInsideTemp, data.getInsideTemp());
        setIfNotNull(builder::setHumidity, data.getHumidity());
        setIfNotNull(builder::setMode, data.getMode());
        setIfNotNull(builder::setRainfall, data.getRainfall());
        setIfNotNull(builder::setSubAssetInformation, map(data.getSubAssetInformation()));
        setIfNotNull(builder::setSubAssetAtHome, data.getSubAssetAtHome());
        setIfNotNull(builder::setSubAssetCharging, data.getSubAssetCharging());
        setIfNotNull(builder::setSubAssetPercentage, data.getSubAssetPercentage());
        setIfNotNull(builder::setDebugModeOpen, data.getDebugModeOpen());
        setIfNotNull(builder::setHasActiveManualControlSession, data.getHasActiveManualControlSession());
        setIfNotNull(builder::setCoverState, data.getCoverState());
        setIfNotNull(builder::setWorkingVoltage, data.getWorkingVoltage());
        setIfNotNull(builder::setWorkingCurrent, data.getWorkingCurrent());
        setIfNotNull(builder::setSupplyVoltage, data.getSupplyVoltage());
        setIfNotNull(builder::setPositionValid, data.getPositionValid());
        setIfNotNull(builder::setNetworkInformation, map(data.getNetworkInformation()));
        setIfNotNull(builder::setAirConditioner, map(data.getAirConditioner()));
        setIfNotNull(builder::setManualControlState, data.getManualControlState());
        setIfNotNull(builder::setPositionState, map(data.getPositionState()));
        setIfNotNull(builder::setWirelessLink, map(data.getWirelessLink()));
        setIfNotNull(builder::setSdrState, map(data.getSdrState()));
        return builder.build();
    }

    private TelemetryData.AssetDetails map(AssetTelemetryDetails proto) {
        return TelemetryData.AssetDetails.builder()
                .environmentTemp(valueIf(proto::hasEnvironmentTemp, proto::getEnvironmentTemp))
                .insideTemp(valueIf(proto::hasInsideTemp, proto::getInsideTemp))
                .humidity(valueIf(proto::hasHumidity, proto::getHumidity))
                .mode(valueIf(proto::hasMode, proto::getMode))
                .rainfall(valueIf(proto::hasRainfall, proto::getRainfall))
                .subAssetInformation(valueIf(proto::hasSubAssetInformation, () -> map(proto.getSubAssetInformation())))
                .subAssetAtHome(valueIf(proto::hasSubAssetAtHome, proto::getSubAssetAtHome))
                .subAssetCharging(valueIf(proto::hasSubAssetCharging, proto::getSubAssetCharging))
                .subAssetPercentage(valueIf(proto::hasSubAssetPercentage, proto::getSubAssetPercentage))
                .debugModeOpen(valueIf(proto::hasDebugModeOpen, proto::getDebugModeOpen))
                .hasActiveManualControlSession(valueIf(proto::hasHasActiveManualControlSession,
                        proto::getHasActiveManualControlSession))
                .coverState(valueIf(proto::hasCoverState, proto::getCoverState))
                .workingVoltage(valueIf(proto::hasWorkingVoltage, proto::getWorkingVoltage))
                .workingCurrent(valueIf(proto::hasWorkingCurrent, proto::getWorkingCurrent))
                .supplyVoltage(valueIf(proto::hasSupplyVoltage, proto::getSupplyVoltage))
                .positionValid(valueIf(proto::hasPositionValid, proto::getPositionValid))
                .networkInformation(valueIf(proto::hasNetworkInformation, () -> map(proto.getNetworkInformation())))
                .airConditioner(valueIf(proto::hasAirConditioner, () -> map(proto.getAirConditioner())))
                .manualControlState(valueIf(proto::hasManualControlState, proto::getManualControlState))
                .positionState(valueIf(proto::hasPositionState, () -> map(proto.getPositionState())))
                .wirelessLink(valueIf(proto::hasWirelessLink, () -> map(proto.getWirelessLink())))
                .sdrState(valueIf(proto::hasSdrState, () -> map(proto.getSdrState())))
                .build();
    }

    private SubAssetTelemetryDetails map(TelemetryData.SubAssetDetails data) {
        if (data == null) return null;
        SubAssetTelemetryDetails.Builder builder = SubAssetTelemetryDetails.newBuilder();
        setIfNotNull(builder::setHorizontalSpeed, data.getHorizontalSpeed());
        setIfNotNull(builder::setVerticalSpeed, data.getVerticalSpeed());
        setIfNotNull(builder::setWindDirection, data.getWindDirection());
        setIfNotNull(builder::setGear, data.getGear());
        setIfNotNull(builder::setPayloadTelemetry, map(data.getPayloadTelemetry()));
        setIfNotNull(builder::setBatteryInformation, map(data.getBatteryInformation()));
        setIfNotNull(builder::setHeightLimit, data.getHeightLimit());
        setIfNotNull(builder::setHomeDistance, data.getHomeDistance());
        setIfNotNull(builder::setTotalMovementDistance, data.getTotalMovementDistance());
        setIfNotNull(builder::setTotalMovementTime, data.getTotalMovementTime());
        setIfNotNull(builder::setMode, data.getMode());
        setIfNotNull(builder::setCountry, data.getCountry());
        if (data.getComponentTelemetry() != null) {
            data.getComponentTelemetry().stream().map(this::map).forEach(builder::addComponentTelemetry);
        }
        return builder.build();
    }

    private TelemetryData.SubAssetDetails map(SubAssetTelemetryDetails proto) {
        return TelemetryData.SubAssetDetails.builder()
                .horizontalSpeed(valueIf(proto::hasHorizontalSpeed, proto::getHorizontalSpeed))
                .verticalSpeed(valueIf(proto::hasVerticalSpeed, proto::getVerticalSpeed))
                .windDirection(valueIf(proto::hasWindDirection, proto::getWindDirection))
                .gear(valueIf(proto::hasGear, proto::getGear))
                .payloadTelemetry(valueIf(proto::hasPayloadTelemetry, () -> map(proto.getPayloadTelemetry())))
                .batteryInformation(valueIf(proto::hasBatteryInformation, () -> map(proto.getBatteryInformation())))
                .heightLimit(valueIf(proto::hasHeightLimit, proto::getHeightLimit))
                .homeDistance(valueIf(proto::hasHomeDistance, proto::getHomeDistance))
                .totalMovementDistance(valueIf(proto::hasTotalMovementDistance, proto::getTotalMovementDistance))
                .totalMovementTime(valueIf(proto::hasTotalMovementTime, proto::getTotalMovementTime))
                .mode(valueIf(proto::hasMode, proto::getMode))
                .country(valueIf(proto::hasCountry, proto::getCountry))
                .componentTelemetry(proto.getComponentTelemetryList().stream().map(this::map).toList())
                .build();
    }

    private ComponentTelemetry map(TelemetryData.ComponentTelemetryData data) {
        ComponentTelemetry.Builder builder = ComponentTelemetry.newBuilder();
        setIfNotNull(builder::setComponentId, data.getComponentId());
        setIfNotNull(builder::setExternalId, data.getExternalId());
        setIfNotNull(builder::setKind, data.getKind());
        setIfNotNull(builder::setTimestamp, MapperSupport.toTimestamp(data.getTimestamp()));
        setIfNotNull(builder::setCameraData, map(data.getCameraData()));
        setIfNotNull(builder::setRangeFinderData, map(data.getRangeFinderData()));
        setIfNotNull(builder::setSensorData, map(data.getSensorData()));
        if (data.getAttributes() != null) builder.setAttributes(mapToStruct(data.getAttributes()));
        return builder.build();
    }

    private TelemetryData.ComponentTelemetryData map(ComponentTelemetry proto) {
        return TelemetryData.ComponentTelemetryData.builder()
                .componentId(proto.getComponentId())
                .externalId(valueIf(proto::hasExternalId, proto::getExternalId))
                .kind(valueIf(proto::hasKind, proto::getKind))
                .timestamp(valueIf(proto::hasTimestamp, () -> MapperSupport.toLocalDateTime(proto.getTimestamp())))
                .cameraData(valueIf(proto::hasCameraData, () -> map(proto.getCameraData())))
                .rangeFinderData(valueIf(proto::hasRangeFinderData, () -> map(proto.getRangeFinderData())))
                .sensorData(valueIf(proto::hasSensorData, () -> map(proto.getSensorData())))
                .attributes(proto.hasAttributes() ? structToMap(proto.getAttributes()) : new LinkedHashMap<>())
                .build();
    }

    private PayloadTelemetry map(TelemetryData.PayloadTelemetry data) {
        if (data == null) return null;
        PayloadTelemetry.Builder builder = PayloadTelemetry.newBuilder();
        setIfNotNull(builder::setId, data.getId());
        setIfNotNull(builder::setTimestamp, MapperSupport.toTimestamp(data.getTimestamp()));
        setIfNotNull(builder::setCameraData, map(data.getCameraData()));
        setIfNotNull(builder::setRangeFinderData, map(data.getRangeFinderData()));
        setIfNotNull(builder::setSensorData, map(data.getSensorData()));
        setIfNotNull(builder::setName, data.getName());
        return builder.build();
    }

    private TelemetryData.PayloadTelemetry map(PayloadTelemetry proto) {
        return TelemetryData.PayloadTelemetry.builder()
                .id(proto.getId())
                .timestamp(valueIf(proto::hasTimestamp, () -> MapperSupport.toLocalDateTime(proto.getTimestamp())))
                .cameraData(valueIf(proto::hasCameraData, () -> map(proto.getCameraData())))
                .rangeFinderData(valueIf(proto::hasRangeFinderData, () -> map(proto.getRangeFinderData())))
                .sensorData(valueIf(proto::hasSensorData, () -> map(proto.getSensorData())))
                .name(proto.getName())
                .build();
    }

    private SubAssetTelemetryDetails.SubAssetBatteryInformation map(TelemetryData.BatteryInformation data) {
        if (data == null) return null;
        var builder = SubAssetTelemetryDetails.SubAssetBatteryInformation.newBuilder();
        setIfNotNull(builder::setPercentage, data.getPercentage());
        setIfNotNull(builder::setRemainingTime, data.getRemainingTime());
        setIfNotNull(builder::setReturnToHomePower, data.getReturnToHomePower());
        return builder.build();
    }

    private TelemetryData.BatteryInformation map(SubAssetTelemetryDetails.SubAssetBatteryInformation proto) {
        return TelemetryData.BatteryInformation.builder()
                .percentage(valueIf(proto::hasPercentage, proto::getPercentage))
                .remainingTime(valueIf(proto::hasRemainingTime, proto::getRemainingTime))
                .returnToHomePower(valueIf(proto::hasReturnToHomePower, proto::getReturnToHomePower))
                .build();
    }

    private PayloadTelemetry.CameraData map(TelemetryData.CameraData data) {
        if (data == null) return null;
        var builder = PayloadTelemetry.CameraData.newBuilder();
        setIfNotNull(builder::setCurrentLens, data.getCurrentLens());
        setIfNotNull(builder::setGimbalPitch, data.getGimbalPitch());
        setIfNotNull(builder::setGimbalYaw, data.getGimbalYaw());
        setIfNotNull(builder::setZoomFactor, data.getZoomFactor());
        setIfNotNull(builder::setGimbalRoll, data.getGimbalRoll());
        return builder.build();
    }

    private TelemetryData.CameraData map(PayloadTelemetry.CameraData proto) {
        return TelemetryData.CameraData.builder()
                .currentLens(valueIf(proto::hasCurrentLens, proto::getCurrentLens))
                .gimbalPitch(valueIf(proto::hasGimbalPitch, proto::getGimbalPitch))
                .gimbalYaw(valueIf(proto::hasGimbalYaw, proto::getGimbalYaw))
                .zoomFactor(valueIf(proto::hasZoomFactor, proto::getZoomFactor))
                .gimbalRoll(valueIf(proto::hasGimbalRoll, proto::getGimbalRoll))
                .build();
    }

    private PayloadTelemetry.RangeFinderData map(TelemetryData.RangeFinderData data) {
        if (data == null) return null;
        var builder = PayloadTelemetry.RangeFinderData.newBuilder();
        setIfNotNull(builder::setTargetLatitude, data.getTargetLatitude());
        setIfNotNull(builder::setTargetLongitude, data.getTargetLongitude());
        setIfNotNull(builder::setTargetDistance, data.getTargetDistance());
        setIfNotNull(builder::setTargetAltitude, data.getTargetAltitude());
        return builder.build();
    }

    private TelemetryData.RangeFinderData map(PayloadTelemetry.RangeFinderData proto) {
        return TelemetryData.RangeFinderData.builder()
                .targetLatitude(valueIf(proto::hasTargetLatitude, proto::getTargetLatitude))
                .targetLongitude(valueIf(proto::hasTargetLongitude, proto::getTargetLongitude))
                .targetDistance(valueIf(proto::hasTargetDistance, proto::getTargetDistance))
                .targetAltitude(valueIf(proto::hasTargetAltitude, proto::getTargetAltitude))
                .build();
    }

    private PayloadTelemetry.SensorData map(TelemetryData.SensorData data) {
        if (data == null) return null;
        var builder = PayloadTelemetry.SensorData.newBuilder();
        setIfNotNull(builder::setTargetTemperature, data.getTargetTemperature());
        return builder.build();
    }

    private TelemetryData.SensorData map(PayloadTelemetry.SensorData proto) {
        return TelemetryData.SensorData.builder()
                .targetTemperature(valueIf(proto::hasTargetTemperature, proto::getTargetTemperature))
                .build();
    }

    private AssetTelemetryDetails.AssetSubAssetInformation map(TelemetryData.SubAssetInformation data) {
        if (data == null) return null;
        var builder = AssetTelemetryDetails.AssetSubAssetInformation.newBuilder();
        setIfNotNull(builder::setSn, data.getSn());
        setIfNotNull(builder::setModel, data.getModel());
        setIfNotNull(builder::setPaired, data.getPaired());
        setIfNotNull(builder::setOnline, data.getOnline());
        return builder.build();
    }

    private TelemetryData.SubAssetInformation map(AssetTelemetryDetails.AssetSubAssetInformation proto) {
        return TelemetryData.SubAssetInformation.builder()
                .sn(valueIf(proto::hasSn, proto::getSn)).model(valueIf(proto::hasModel, proto::getModel))
                .paired(valueIf(proto::hasPaired, proto::getPaired)).online(valueIf(proto::hasOnline, proto::getOnline))
                .build();
    }

    private AssetTelemetryDetails.AssetNetworkInformation map(TelemetryData.NetworkInformation data) {
        if (data == null) return null;
        var builder = AssetTelemetryDetails.AssetNetworkInformation.newBuilder();
        setIfNotNull(builder::setType, data.getType());
        setIfNotNull(builder::setRate, data.getRate());
        setIfNotNull(builder::setQuality, data.getQuality());
        return builder.build();
    }

    private TelemetryData.NetworkInformation map(AssetTelemetryDetails.AssetNetworkInformation proto) {
        return TelemetryData.NetworkInformation.builder()
                .type(valueIf(proto::hasType, proto::getType)).rate(valueIf(proto::hasRate, proto::getRate))
                .quality(valueIf(proto::hasQuality, proto::getQuality)).build();
    }

    private AssetTelemetryDetails.AssetAirConditioner map(TelemetryData.AirConditioner data) {
        if (data == null) return null;
        var builder = AssetTelemetryDetails.AssetAirConditioner.newBuilder();
        setIfNotNull(builder::setState, data.getState());
        setIfNotNull(builder::setSwitchTime, data.getSwitchTime());
        return builder.build();
    }

    private TelemetryData.AirConditioner map(AssetTelemetryDetails.AssetAirConditioner proto) {
        return TelemetryData.AirConditioner.builder()
                .state(valueIf(proto::hasState, proto::getState)).switchTime(valueIf(proto::hasSwitchTime, proto::getSwitchTime))
                .build();
    }

    private AssetTelemetryDetails.PositionState map(TelemetryData.PositionState data) {
        if (data == null) return null;
        var builder = AssetTelemetryDetails.PositionState.newBuilder();
        setIfNotNull(builder::setGpsNumber, data.getGpsNumber());
        setIfNotNull(builder::setRtkNumber, data.getRtkNumber());
        setIfNotNull(builder::setQuality, data.getQuality());
        return builder.build();
    }

    private TelemetryData.PositionState map(AssetTelemetryDetails.PositionState proto) {
        return TelemetryData.PositionState.builder()
                .gpsNumber(valueIf(proto::hasGpsNumber, proto::getGpsNumber))
                .rtkNumber(valueIf(proto::hasRtkNumber, proto::getRtkNumber))
                .quality(valueIf(proto::hasQuality, proto::getQuality)).build();
    }

    private AssetTelemetryDetails.AssetWirelessLinkInformation map(TelemetryData.WirelessLinkInformation data) {
        if (data == null) return null;
        var builder = AssetTelemetryDetails.AssetWirelessLinkInformation.newBuilder();
        setIfNotNull(builder::setFourthGenerationFreqBand, data.getFourthGenerationFreqBand());
        setIfNotNull(builder::setFourthGenerationGndQuality, data.getFourthGenerationGndQuality());
        setIfNotNull(builder::setFourthGenerationLinkState, data.getFourthGenerationLinkState());
        setIfNotNull(builder::setFourthGenerationQuality, data.getFourthGenerationQuality());
        setIfNotNull(builder::setFourthGenerationUavQuality, data.getFourthGenerationUavQuality());
        setIfNotNull(builder::setDongleNumber, data.getDongleNumber());
        setIfNotNull(builder::setLinkWorkmode, data.getLinkWorkmode());
        setIfNotNull(builder::setSdrFreqBand, data.getSdrFreqBand());
        setIfNotNull(builder::setSdrLinkState, data.getSdrLinkState());
        setIfNotNull(builder::setSdrQuality, data.getSdrQuality());
        return builder.build();
    }

    private TelemetryData.WirelessLinkInformation map(AssetTelemetryDetails.AssetWirelessLinkInformation proto) {
        return TelemetryData.WirelessLinkInformation.builder()
                .fourthGenerationFreqBand(valueIf(proto::hasFourthGenerationFreqBand, proto::getFourthGenerationFreqBand))
                .fourthGenerationGndQuality(valueIf(proto::hasFourthGenerationGndQuality, proto::getFourthGenerationGndQuality))
                .fourthGenerationLinkState(valueIf(proto::hasFourthGenerationLinkState, proto::getFourthGenerationLinkState))
                .fourthGenerationQuality(valueIf(proto::hasFourthGenerationQuality, proto::getFourthGenerationQuality))
                .fourthGenerationUavQuality(valueIf(proto::hasFourthGenerationUavQuality, proto::getFourthGenerationUavQuality))
                .dongleNumber(valueIf(proto::hasDongleNumber, proto::getDongleNumber))
                .linkWorkmode(valueIf(proto::hasLinkWorkmode, proto::getLinkWorkmode))
                .sdrFreqBand(valueIf(proto::hasSdrFreqBand, proto::getSdrFreqBand))
                .sdrLinkState(valueIf(proto::hasSdrLinkState, proto::getSdrLinkState))
                .sdrQuality(valueIf(proto::hasSdrQuality, proto::getSdrQuality)).build();
    }

    private AssetTelemetryDetails.AssetSdrState map(TelemetryData.SdrState data) {
        if (data == null) return null;
        var builder = AssetTelemetryDetails.AssetSdrState.newBuilder();
        setIfNotNull(builder::setDownQuality, data.getDownQuality());
        setIfNotNull(builder::setUpQuality, data.getUpQuality());
        setIfNotNull(builder::setFrequencyBand, data.getFrequencyBand());
        return builder.build();
    }

    private TelemetryData.SdrState map(AssetTelemetryDetails.AssetSdrState proto) {
        return TelemetryData.SdrState.builder()
                .downQuality(valueIf(proto::hasDownQuality, proto::getDownQuality))
                .upQuality(valueIf(proto::hasUpQuality, proto::getUpQuality))
                .frequencyBand(valueIf(proto::hasFrequencyBand, proto::getFrequencyBand)).build();
    }

    private Struct mapToStruct(Map<String, Object> values) {
        Struct.Builder builder = Struct.newBuilder();
        values.forEach((key, value) -> builder.putFields(key, objectToValue(value)));
        return builder.build();
    }

    private Value objectToValue(Object value) {
        Value.Builder builder = Value.newBuilder();
        if (value == null) return builder.setNullValue(com.google.protobuf.NullValue.NULL_VALUE).build();
        if (value instanceof Number number) return builder.setNumberValue(number.doubleValue()).build();
        if (value instanceof Boolean bool) return builder.setBoolValue(bool).build();
        if (value instanceof Map<?, ?> map) {
            Struct.Builder struct = Struct.newBuilder();
            map.forEach((key, item) -> struct.putFields(String.valueOf(key), objectToValue(item)));
            return builder.setStructValue(struct).build();
        }
        if (value instanceof Iterable<?> iterable) {
            ListValue.Builder list = ListValue.newBuilder();
            iterable.forEach(item -> list.addValues(objectToValue(item)));
            return builder.setListValue(list).build();
        }
        return builder.setStringValue(String.valueOf(value)).build();
    }

    private Map<String, Object> structToMap(Struct struct) {
        Map<String, Object> result = new LinkedHashMap<>();
        struct.getFieldsMap().forEach((key, value) -> result.put(key, valueToObject(value)));
        return result;
    }

    private Object valueToObject(Value value) {
        return switch (value.getKindCase()) {
            case NULL_VALUE, KIND_NOT_SET -> null;
            case NUMBER_VALUE -> value.getNumberValue();
            case STRING_VALUE -> value.getStringValue();
            case BOOL_VALUE -> value.getBoolValue();
            case STRUCT_VALUE -> structToMap(value.getStructValue());
            case LIST_VALUE -> value.getListValue().getValuesList().stream().map(this::valueToObject).toList();
        };
    }

    private static <T> void setIfNotNull(Consumer<T> setter, T value) {
        MapperSupport.set(setter, value);
    }

    private static <T> T valueIf(BooleanSupplier hasValue, Supplier<T> supplier) {
        return hasValue.getAsBoolean() ? supplier.get() : null;
    }

    public LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return MapperSupport.toLocalDateTime(timestamp);
    }

    public Timestamp toTimestamp(LocalDateTime localDateTime) {
        return MapperSupport.toTimestamp(localDateTime);
    }
}
