package com.zqnt.sdk.edge.livedata.application;

import com.google.protobuf.Timestamp;
import com.zqnt.sdk.edge.adapter.domains.TelemetryRequestData;
import com.zqnt.utils.edge.sdk.domains.AssetTelemetryData;
import com.zqnt.utils.edge.sdk.domains.SubAssetTelemetryData;
import com.zqnt.utils.livedata.proto.AssetTelemetry;
import com.zqnt.utils.livedata.proto.PayloadTelemetry;
import com.zqnt.utils.livedata.proto.ProduceTelemetryRequest;
import com.zqnt.utils.livedata.proto.SubAssetTelemetry;
import com.zqnt.sdk.edge.support.MapperSupport;

import java.time.LocalDateTime;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Mapper for converting telemetry POJO data to Proto messages and back.
 */
public class TelemetryMapper {

    public TelemetryRequestData map(ProduceTelemetryRequest request) {
        if (request == null) {
            return null;
        }

        TelemetryRequestData data = new TelemetryRequestData();
        if (request.hasBase()) {
            data.setSn(request.getBase().getSn());
            data.setTid(request.getBase().getTid());
            data.setTimestamp(MapperSupport.toLocalDateTime(request.getBase().getTimestamp()));
        }
        data.setType(request.getType());

        if (request.hasAssetTelemetry()) {
            data.setAssetTelemetry(map(request.getAssetTelemetry()));
        }
        if (request.hasSubAssetTelemetry()) {
            data.setSubAssetTelemetry(map(request.getSubAssetTelemetry()));
        }

        return data;
    }

    public ProduceTelemetryRequest map(TelemetryRequestData requestData) {
        if (requestData == null) {
            return null;
        }

        var baseBuilder = MapperSupport.requestBase(
                requestData.getSn(),
                requestData.getTid(),
                requestData.getTimestamp());

        ProduceTelemetryRequest.Builder builder = ProduceTelemetryRequest.newBuilder()
                .setBase(baseBuilder.build());

        setIfNotNull(builder::setType, requestData.getType());
        setIfNotNull(builder::setAssetTelemetry, map(requestData.getAssetTelemetry()));
        setIfNotNull(builder::setSubAssetTelemetry, map(requestData.getSubAssetTelemetry()));

        return builder.build();
    }

    public AssetTelemetry map(AssetTelemetryData telemetryData) {
        if (telemetryData == null) {
            return null;
        }

        AssetTelemetry.Builder builder = AssetTelemetry.newBuilder();

        setIfNotNull(builder::setSn, telemetryData.getSn());
        setIfNotNull(builder::setId, telemetryData.getId());
        setIfNotNull(builder::setTimestamp, MapperSupport.toTimestamp(telemetryData.getTimestamp()));
        setIfNotNull(builder::setLatitude, telemetryData.getLatitude());
        setIfNotNull(builder::setLongitude, telemetryData.getLongitude());
        setIfNotNull(builder::setAbsoluteAltitude, telemetryData.getAbsoluteAltitude());
        setIfNotNull(builder::setRelativeAltitude, telemetryData.getRelativeAltitude());
        setIfNotNull(builder::setHeading, telemetryData.getHeading());
        setIfNotNull(builder::setEnvironmentTemp, telemetryData.getEnvironmentTemp());
        setIfNotNull(builder::setInsideTemp, telemetryData.getInsideTemp());
        setIfNotNull(builder::setHumidity, telemetryData.getHumidity());
        setIfNotNull(builder::setRainfall, telemetryData.getRainfall());
        setIfNotNull(builder::setWindSpeed, telemetryData.getWindSpeed());
        setIfNotNull(builder::setWorkingVoltage, telemetryData.getWorkingVoltage());
        setIfNotNull(builder::setWorkingCurrent, telemetryData.getWorkingCurrent());
        setIfNotNull(builder::setSupplyVoltage, telemetryData.getSupplyVoltage());
        setIfNotNull(builder::setCoverState, telemetryData.getCoverState());
        setIfNotNull(builder::setSubAssetAtHome, telemetryData.getSubAssetAtHome());
        setIfNotNull(builder::setSubAssetCharging, telemetryData.getSubAssetCharging());
        setIfNotNull(builder::setSubAssetPercentage, telemetryData.getSubAssetPercentage());
        setIfNotNull(builder::setDebugModeOpen, telemetryData.getDebugModeOpen());
        setIfNotNull(builder::setHasActiveManualControlSession, telemetryData.getHasActiveManualControlSession());
        setIfNotNull(builder::setManualControlState, telemetryData.getManualControlState());
        setIfNotNull(builder::setPositionValid, telemetryData.getPositionValid());
        setIfNotNull(builder::setMode, telemetryData.getMode());
        setIfNotNull(builder::setPositionState, mapPositionState(telemetryData.getPositionState()));
        setIfNotNull(builder::setSubAssetInformation, mapSubAssetInformation(telemetryData.getSubAssetInformation()));
        setIfNotNull(builder::setNetworkInformation, mapNetworkInformation(telemetryData.getNetworkInformation()));
        setIfNotNull(builder::setWirelessLink, mapWirelessLinkInformation(telemetryData.getWirelessLink()));
        setIfNotNull(builder::setSdrState, mapSdrState(telemetryData.getSdrState()));
        setIfNotNull(builder::setAirConditioner, mapAirConditioner(telemetryData.getAirConditioner()));

        return builder.build();
    }


    public SubAssetTelemetry map(SubAssetTelemetryData telemetryData) {
        if (telemetryData == null) {
            return null;
        }

        SubAssetTelemetry.Builder builder = SubAssetTelemetry.newBuilder();

        setIfNotNull(builder::setId, telemetryData.getId());
        setIfNotNull(builder::setTimestamp, MapperSupport.toTimestamp(telemetryData.getTimestamp()));
        setIfNotNull(builder::setLatitude, telemetryData.getLatitude());
        setIfNotNull(builder::setLongitude, telemetryData.getLongitude());
        setIfNotNull(builder::setAbsoluteAltitude, telemetryData.getAbsoluteAltitude());
        setIfNotNull(builder::setRelativeAltitude, telemetryData.getRelativeAltitude());
        setIfNotNull(builder::setHeading, telemetryData.getHeading());
        setIfNotNull(builder::setHorizontalSpeed, telemetryData.getHorizontalSpeed());
        setIfNotNull(builder::setVerticalSpeed, telemetryData.getVerticalSpeed());
        setIfNotNull(builder::setWindSpeed, telemetryData.getWindSpeed());
        setIfNotNull(builder::setWindDirection, telemetryData.getWindDirection());
        setIfNotNull(builder::setGear, telemetryData.getGear());
        setIfNotNull(builder::setHeightLimit, telemetryData.getHeightLimit());
        setIfNotNull(builder::setHomeDistance, telemetryData.getHomeDistance());
        setIfNotNull(builder::setTotalMovementDistance, telemetryData.getTotalMovementDistance());
        setIfNotNull(builder::setTotalMovementTime, telemetryData.getTotalMovementTime());
        setIfNotNull(builder::setCountry, telemetryData.getCountry());
        setIfNotNull(builder::setMode, telemetryData.getMode());
        setIfNotNull(builder::setBatteryInformation, mapBatteryInformation(telemetryData.getBatteryInformation()));
        setIfNotNull(builder::setPayloadTelemetry, mapPayloadTelemetry(telemetryData.getPayloadTelemetry()));

        return builder.build();
    }

    public AssetTelemetryData map(AssetTelemetry telemetry) {
        if (telemetry == null) {
            return null;
        }

        AssetTelemetryData.AssetTelemetryDataBuilder builder = AssetTelemetryData.builder()
                .sn(valueIf(telemetry::hasSn, telemetry::getSn))
                .timestamp(valueIf(telemetry::hasTimestamp, () -> MapperSupport.toLocalDateTime(telemetry.getTimestamp())))
                .latitude(valueIf(telemetry::hasLatitude, telemetry::getLatitude))
                .longitude(valueIf(telemetry::hasLongitude, telemetry::getLongitude))
                .absoluteAltitude(valueIf(telemetry::hasAbsoluteAltitude, telemetry::getAbsoluteAltitude))
                .relativeAltitude(valueIf(telemetry::hasRelativeAltitude, telemetry::getRelativeAltitude))
                .heading(valueIf(telemetry::hasHeading, telemetry::getHeading))
                .environmentTemp(valueIf(telemetry::hasEnvironmentTemp, telemetry::getEnvironmentTemp))
                .insideTemp(valueIf(telemetry::hasInsideTemp, telemetry::getInsideTemp))
                .humidity(valueIf(telemetry::hasHumidity, telemetry::getHumidity))
                .rainfall(valueIf(telemetry::hasRainfall, telemetry::getRainfall))
                .windSpeed(valueIf(telemetry::hasWindSpeed, telemetry::getWindSpeed))
                .workingVoltage(valueIf(telemetry::hasWorkingVoltage, telemetry::getWorkingVoltage))
                .workingCurrent(valueIf(telemetry::hasWorkingCurrent, telemetry::getWorkingCurrent))
                .supplyVoltage(valueIf(telemetry::hasSupplyVoltage, telemetry::getSupplyVoltage))
                .coverState(valueIf(telemetry::hasCoverState, telemetry::getCoverState))
                .subAssetAtHome(valueIf(telemetry::hasSubAssetAtHome, telemetry::getSubAssetAtHome))
                .subAssetCharging(valueIf(telemetry::hasSubAssetCharging, telemetry::getSubAssetCharging))
                .subAssetPercentage(valueIf(telemetry::hasSubAssetPercentage, telemetry::getSubAssetPercentage))
                .debugModeOpen(valueIf(telemetry::hasDebugModeOpen, telemetry::getDebugModeOpen))
                .hasActiveManualControlSession(valueIf(
                        telemetry::hasHasActiveManualControlSession,
                        telemetry::getHasActiveManualControlSession))
                .manualControlState(valueIf(telemetry::hasManualControlState, telemetry::getManualControlState))
                .positionValid(valueIf(telemetry::hasPositionValid, telemetry::getPositionValid))
                .mode(valueIf(telemetry::hasMode, telemetry::getMode));

        setIfNotNull(builder::positionState, valueIf(telemetry::hasPositionState,
                () -> mapPositionState(telemetry.getPositionState())));
        setIfNotNull(builder::subAssetInformation, valueIf(telemetry::hasSubAssetInformation,
                () -> mapSubAssetInformation(telemetry.getSubAssetInformation())));
        setIfNotNull(builder::networkInformation, valueIf(telemetry::hasNetworkInformation,
                () -> mapNetworkInformation(telemetry.getNetworkInformation())));
        setIfNotNull(builder::wirelessLink, valueIf(telemetry::hasWirelessLink,
                () -> mapWirelessLinkInformation(telemetry.getWirelessLink())));
        setIfNotNull(builder::airConditioner, valueIf(telemetry::hasAirConditioner,
                () -> mapAirConditioner(telemetry.getAirConditioner())));

        return builder.build();
    }

    public SubAssetTelemetryData map(SubAssetTelemetry telemetry) {
        if (telemetry == null) {
            return null;
        }

        SubAssetTelemetryData.SubAssetTelemetryDataBuilder builder = SubAssetTelemetryData.builder()
                .timestamp(valueIf(telemetry::hasTimestamp, () -> MapperSupport.toLocalDateTime(telemetry.getTimestamp())))
                .latitude(valueIf(telemetry::hasLatitude, telemetry::getLatitude))
                .longitude(valueIf(telemetry::hasLongitude, telemetry::getLongitude))
                .absoluteAltitude(valueIf(telemetry::hasAbsoluteAltitude, telemetry::getAbsoluteAltitude))
                .relativeAltitude(valueIf(telemetry::hasRelativeAltitude, telemetry::getRelativeAltitude))
                .heading(valueIf(telemetry::hasHeading, telemetry::getHeading))
                .horizontalSpeed(valueIf(telemetry::hasHorizontalSpeed, telemetry::getHorizontalSpeed))
                .verticalSpeed(valueIf(telemetry::hasVerticalSpeed, telemetry::getVerticalSpeed))
                .windSpeed(valueIf(telemetry::hasWindSpeed, telemetry::getWindSpeed))
                .windDirection(valueIf(telemetry::hasWindDirection, telemetry::getWindDirection))
                .gear(valueIf(telemetry::hasGear, telemetry::getGear))
                .heightLimit(valueIf(telemetry::hasHeightLimit, telemetry::getHeightLimit))
                .homeDistance(valueIf(telemetry::hasHomeDistance, telemetry::getHomeDistance))
                .totalMovementDistance(valueIf(
                        telemetry::hasTotalMovementDistance,
                        telemetry::getTotalMovementDistance))
                .totalMovementTime(valueIf(telemetry::hasTotalMovementTime, telemetry::getTotalMovementTime))
                .country(valueIf(telemetry::hasCountry, telemetry::getCountry))
                .mode(valueIf(telemetry::hasMode, telemetry::getMode));

        setIfNotNull(builder::batteryInformation, valueIf(telemetry::hasBatteryInformation,
                () -> mapBatteryInformation(telemetry.getBatteryInformation())));
        setIfNotNull(builder::payloadTelemetry, valueIf(telemetry::hasPayloadTelemetry,
                () -> mapPayloadTelemetry(telemetry.getPayloadTelemetry())));

        return builder.build();
    }

    private AssetTelemetry.PositionState mapPositionState(AssetTelemetryData.PositionState positionState) {
        if (positionState == null) {
            return null;
        }

        AssetTelemetry.PositionState.Builder builder = AssetTelemetry.PositionState.newBuilder();
        setIfNotNull(builder::setGpsNumber, positionState.getGpsNumber());
        setIfNotNull(builder::setRtkNumber, positionState.getRtkNumber());
        setIfNotNull(builder::setQuality, positionState.getQuality());
        return builder.build();
    }

    private AssetTelemetry.AssetSubAssetInformation mapSubAssetInformation(
            AssetTelemetryData.SubAssetInformation subAssetInformation) {
        if (subAssetInformation == null) {
            return null;
        }

        AssetTelemetry.AssetSubAssetInformation.Builder builder =
                AssetTelemetry.AssetSubAssetInformation.newBuilder();
        setIfNotNull(builder::setSn, subAssetInformation.getSn());
        setIfNotNull(builder::setModel, subAssetInformation.getModel());
        setIfNotNull(builder::setPaired, subAssetInformation.getPaired());
        setIfNotNull(builder::setOnline, subAssetInformation.getOnline());
        return builder.build();
    }

    private AssetTelemetry.AssetNetworkInformation mapNetworkInformation(
            AssetTelemetryData.NetworkInformation networkInformation) {
        if (networkInformation == null) {
            return null;
        }

        AssetTelemetry.AssetNetworkInformation.Builder builder = AssetTelemetry.AssetNetworkInformation.newBuilder();
        setIfNotNull(builder::setType, networkInformation.getType());
        setIfNotNull(builder::setRate, networkInformation.getRate());
        setIfNotNull(builder::setQuality, networkInformation.getQuality());
        return builder.build();
    }

    private AssetTelemetry.AssetWirelessLinkInformation mapWirelessLinkInformation(
            AssetTelemetryData.WirelessLinkInformation wirelessLinkInformation) {
        if (wirelessLinkInformation == null) {
            return null;
        }

        AssetTelemetry.AssetWirelessLinkInformation.Builder builder =
                AssetTelemetry.AssetWirelessLinkInformation.newBuilder();
        setIfNotNull(builder::setSdrFreqBand, wirelessLinkInformation.getSdrFreqBand());
        setIfNotNull(builder::setSdrLinkState, wirelessLinkInformation.getSdrLinkState());
        setIfNotNull(builder::setSdrQuality, wirelessLinkInformation.getSdrQuality());
        setIfNotNull(builder::setDongleNumber, wirelessLinkInformation.getDongleNumber());
        setIfNotNull(builder::setFourthGenerationFreqBand, wirelessLinkInformation.getFourthGenerationFreqBand());
        setIfNotNull(builder::setFourthGenerationLinkState, wirelessLinkInformation.getFourthGenerationLinkState());
        setIfNotNull(builder::setFourthGenerationQuality, wirelessLinkInformation.getFourthGenerationQuality());
        setIfNotNull(builder::setFourthGenerationGndQuality, wirelessLinkInformation.getFourthGenerationGndQuality());
        setIfNotNull(builder::setFourthGenerationUavQuality, wirelessLinkInformation.getFourthGenerationUavQuality());
        setIfNotNull(builder::setLinkWorkmode, wirelessLinkInformation.getLinkWorkmode());
        return builder.build();
    }


    private AssetTelemetry.AssetSdrState mapSdrState(AssetTelemetryData.SdrState sdrState) {
        if (sdrState == null) {
            return null;
        }
        AssetTelemetry.AssetSdrState.Builder builder = AssetTelemetry.AssetSdrState.newBuilder();
        setIfNotNull(builder::setDownQuality, sdrState.getDownQuality());
        setIfNotNull(builder::setUpQuality, sdrState.getUpQuality());
        setIfNotNull(builder::setFrequencyBand, sdrState.getFrequencyBand());
        return  builder.build();
    }

    private AssetTelemetry.AssetAirConditioner mapAirConditioner(
            AssetTelemetryData.AirConditioner airConditioner) {
        if (airConditioner == null) {
            return null;
        }

        AssetTelemetry.AssetAirConditioner.Builder builder = AssetTelemetry.AssetAirConditioner.newBuilder();
        setIfNotNull(builder::setState, airConditioner.getState());
        setIfNotNull(builder::setSwitchTime, airConditioner.getSwitchTime());
        return builder.build();
    }

    private SubAssetTelemetry.SubAssetBatteryInformation mapBatteryInformation(
            SubAssetTelemetryData.BatteryInformation batteryInformation) {
        if (batteryInformation == null) {
            return null;
        }

        SubAssetTelemetry.SubAssetBatteryInformation.Builder builder =
                SubAssetTelemetry.SubAssetBatteryInformation.newBuilder();
        setIfNotNull(builder::setPercentage, batteryInformation.getPercentage());
        setIfNotNull(builder::setRemainingTime, batteryInformation.getRemainingTime());
        setIfNotNull(builder::setReturnToHomePower, batteryInformation.getReturnToHomePower());
        return builder.build();
    }

    private PayloadTelemetry mapPayloadTelemetry(SubAssetTelemetryData.PayloadTelemetry payloadTelemetry) {
        if (payloadTelemetry == null) {
            return null;
        }

        PayloadTelemetry.Builder builder = PayloadTelemetry.newBuilder();
        setIfNotNull(builder::setId, payloadTelemetry.getId());
        setIfNotNull(builder::setName, payloadTelemetry.getName());
        setIfNotNull(builder::setTimestamp, MapperSupport.toTimestamp(payloadTelemetry.getTimestamp()));
        setIfNotNull(builder::setCameraData, mapCameraData(payloadTelemetry.getCameraData()));
        setIfNotNull(builder::setRangeFinderData, mapRangeFinderData(payloadTelemetry.getRangeFinderData()));
        setIfNotNull(builder::setSensorData, mapSensorData(payloadTelemetry.getSensorData()));
        return builder.build();
    }

    private PayloadTelemetry.CameraData mapCameraData(SubAssetTelemetryData.CameraData cameraData) {
        if (cameraData == null) {
            return null;
        }

        PayloadTelemetry.CameraData.Builder builder = PayloadTelemetry.CameraData.newBuilder();
        setIfNotNull(builder::setCurrentLens, cameraData.getCurrentLens());
        setIfNotNull(builder::setGimbalPitch, cameraData.getGimbalPitch());
        setIfNotNull(builder::setGimbalYaw, cameraData.getGimbalYaw());
        setIfNotNull(builder::setGimbalRoll, cameraData.getGimbalRoll());
        setIfNotNull(builder::setZoomFactor, cameraData.getZoomFactor());
        return builder.build();
    }

    private PayloadTelemetry.RangeFinderData mapRangeFinderData(SubAssetTelemetryData.RangeFinderData rangeFinderData) {
        if (rangeFinderData == null) {
            return null;
        }

        PayloadTelemetry.RangeFinderData.Builder builder = PayloadTelemetry.RangeFinderData.newBuilder();
        setIfNotNull(builder::setTargetLatitude, rangeFinderData.getTargetLatitude());
        setIfNotNull(builder::setTargetLongitude, rangeFinderData.getTargetLongitude());
        setIfNotNull(builder::setTargetDistance, rangeFinderData.getTargetDistance());
        setIfNotNull(builder::setTargetAltitude, rangeFinderData.getTargetAltitude());
        return builder.build();
    }

    private PayloadTelemetry.SensorData mapSensorData(SubAssetTelemetryData.SensorData sensorData) {
        if (sensorData == null) {
            return null;
        }

        PayloadTelemetry.SensorData.Builder builder = PayloadTelemetry.SensorData.newBuilder();
        setIfNotNull(builder::setTargetTemperature, sensorData.getTargetTemperature());
        return builder.build();
    }

    private AssetTelemetryData.PositionState mapPositionState(AssetTelemetry.PositionState positionState) {
        if (positionState == null) {
            return null;
        }

        AssetTelemetryData.PositionState.PositionStateBuilder builder = AssetTelemetryData.PositionState.builder();
        setIfNotNull(builder::gpsNumber, valueIf(positionState::hasGpsNumber, positionState::getGpsNumber));
        setIfNotNull(builder::rtkNumber, valueIf(positionState::hasRtkNumber, positionState::getRtkNumber));
        setIfNotNull(builder::quality, valueIf(positionState::hasQuality, positionState::getQuality));
        return builder.build();
    }

    private AssetTelemetryData.SubAssetInformation mapSubAssetInformation(
            AssetTelemetry.AssetSubAssetInformation subAssetInformation) {
        if (subAssetInformation == null) {
            return null;
        }

        AssetTelemetryData.SubAssetInformation.SubAssetInformationBuilder builder =
                AssetTelemetryData.SubAssetInformation.builder();
        setIfNotNull(builder::sn, valueIf(subAssetInformation::hasSn, subAssetInformation::getSn));
        setIfNotNull(builder::model, valueIf(subAssetInformation::hasModel, subAssetInformation::getModel));
        setIfNotNull(builder::paired, valueIf(subAssetInformation::hasPaired, subAssetInformation::getPaired));
        setIfNotNull(builder::online, valueIf(subAssetInformation::hasOnline, subAssetInformation::getOnline));
        return builder.build();
    }

    private AssetTelemetryData.NetworkInformation mapNetworkInformation(
            AssetTelemetry.AssetNetworkInformation networkInformation) {
        if (networkInformation == null) {
            return null;
        }

        AssetTelemetryData.NetworkInformation.NetworkInformationBuilder builder =
                AssetTelemetryData.NetworkInformation.builder();
        setIfNotNull(builder::type, valueIf(networkInformation::hasType, networkInformation::getType));
        setIfNotNull(builder::rate, valueIf(networkInformation::hasRate, networkInformation::getRate));
        setIfNotNull(builder::quality, valueIf(networkInformation::hasQuality, networkInformation::getQuality));
        return builder.build();
    }

    private AssetTelemetryData.WirelessLinkInformation mapWirelessLinkInformation(
            AssetTelemetry.AssetWirelessLinkInformation wirelessLinkInformation) {
        if (wirelessLinkInformation == null) {
            return null;
        }

        AssetTelemetryData.WirelessLinkInformation.WirelessLinkInformationBuilder builder =
                AssetTelemetryData.WirelessLinkInformation.builder();
        setIfNotNull(builder::sdrFreqBand, valueIf(wirelessLinkInformation::hasSdrFreqBand, wirelessLinkInformation::getSdrFreqBand));
        setIfNotNull(builder::sdrLinkState, valueIf(wirelessLinkInformation::hasSdrLinkState, wirelessLinkInformation::getSdrLinkState));
        setIfNotNull(builder::sdrQuality, valueIf(wirelessLinkInformation::hasSdrQuality, wirelessLinkInformation::getSdrQuality));
        setIfNotNull(builder::dongleNumber, valueIf(wirelessLinkInformation::hasDongleNumber, wirelessLinkInformation::getDongleNumber));
        setIfNotNull(builder::fourthGenerationFreqBand, valueIf(wirelessLinkInformation::hasFourthGenerationFreqBand, wirelessLinkInformation::getFourthGenerationFreqBand));
        setIfNotNull(builder::fourthGenerationLinkState, valueIf(wirelessLinkInformation::hasFourthGenerationLinkState, wirelessLinkInformation::getFourthGenerationLinkState));
        setIfNotNull(builder::fourthGenerationQuality, valueIf(wirelessLinkInformation::hasFourthGenerationQuality, wirelessLinkInformation::getFourthGenerationQuality));
        setIfNotNull(builder::fourthGenerationGndQuality, valueIf(wirelessLinkInformation::hasFourthGenerationGndQuality, wirelessLinkInformation::getFourthGenerationGndQuality));
        setIfNotNull(builder::fourthGenerationUavQuality, valueIf(wirelessLinkInformation::hasFourthGenerationUavQuality, wirelessLinkInformation::getFourthGenerationUavQuality));
        setIfNotNull(builder::linkWorkmode, valueIf(wirelessLinkInformation::hasLinkWorkmode, wirelessLinkInformation::getLinkWorkmode));
        return builder.build();
    }

    private AssetTelemetryData.AirConditioner mapAirConditioner(
            AssetTelemetry.AssetAirConditioner airConditioner) {
        if (airConditioner == null) {
            return null;
        }

        AssetTelemetryData.AirConditioner.AirConditionerBuilder builder = AssetTelemetryData.AirConditioner.builder();
        setIfNotNull(builder::state, valueIf(airConditioner::hasState, airConditioner::getState));
        setIfNotNull(builder::switchTime, valueIf(airConditioner::hasSwitchTime, airConditioner::getSwitchTime));
        return builder.build();
    }

    private SubAssetTelemetryData.BatteryInformation mapBatteryInformation(
            SubAssetTelemetry.SubAssetBatteryInformation batteryInformation) {
        if (batteryInformation == null) {
            return null;
        }

        SubAssetTelemetryData.BatteryInformation.BatteryInformationBuilder builder =
                SubAssetTelemetryData.BatteryInformation.builder();
        setIfNotNull(builder::percentage, valueIf(batteryInformation::hasPercentage, batteryInformation::getPercentage));
        setIfNotNull(builder::remainingTime, valueIf(batteryInformation::hasRemainingTime, batteryInformation::getRemainingTime));
        setIfNotNull(builder::returnToHomePower, valueIf(batteryInformation::hasReturnToHomePower, batteryInformation::getReturnToHomePower));
        return builder.build();
    }

    private SubAssetTelemetryData.PayloadTelemetry mapPayloadTelemetry(PayloadTelemetry payloadTelemetry) {
        if (payloadTelemetry == null) {
            return null;
        }

        SubAssetTelemetryData.PayloadTelemetry.PayloadTelemetryBuilder builder =
                SubAssetTelemetryData.PayloadTelemetry.builder();
        setIfNotNull(builder::id, payloadTelemetry.getId());
        setIfNotNull(builder::name, payloadTelemetry.getName());
        setIfNotNull(builder::timestamp, valueIf(payloadTelemetry::hasTimestamp, () -> MapperSupport.toLocalDateTime(payloadTelemetry.getTimestamp())));
        setIfNotNull(builder::cameraData, mapCameraData(payloadTelemetry.getCameraData()));
        setIfNotNull(builder::rangeFinderData, mapRangeFinderData(payloadTelemetry.getRangeFinderData()));
        setIfNotNull(builder::sensorData, mapSensorData(payloadTelemetry.getSensorData()));
        return builder.build();
    }

    private SubAssetTelemetryData.CameraData mapCameraData(PayloadTelemetry.CameraData cameraData) {
        if (cameraData == null) {
            return null;
        }

        SubAssetTelemetryData.CameraData.CameraDataBuilder builder = SubAssetTelemetryData.CameraData.builder();
        setIfNotNull(builder::currentLens, valueIf(cameraData::hasCurrentLens, cameraData::getCurrentLens));
        setIfNotNull(builder::gimbalPitch, valueIf(cameraData::hasGimbalPitch, cameraData::getGimbalPitch));
        setIfNotNull(builder::gimbalYaw, valueIf(cameraData::hasGimbalYaw, cameraData::getGimbalYaw));
        setIfNotNull(builder::gimbalRoll, valueIf(cameraData::hasGimbalRoll, cameraData::getGimbalRoll));
        setIfNotNull(builder::zoomFactor, valueIf(cameraData::hasZoomFactor, cameraData::getZoomFactor));
        return builder.build();
    }

    private SubAssetTelemetryData.RangeFinderData mapRangeFinderData(PayloadTelemetry.RangeFinderData rangeFinderData) {
        if (rangeFinderData == null) {
            return null;
        }

        SubAssetTelemetryData.RangeFinderData.RangeFinderDataBuilder builder = SubAssetTelemetryData.RangeFinderData.builder();
        setIfNotNull(builder::targetLatitude, valueIf(rangeFinderData::hasTargetLatitude, rangeFinderData::getTargetLatitude));
        setIfNotNull(builder::targetLongitude, valueIf(rangeFinderData::hasTargetLongitude, rangeFinderData::getTargetLongitude));
        setIfNotNull(builder::targetDistance, valueIf(rangeFinderData::hasTargetDistance, rangeFinderData::getTargetDistance));
        setIfNotNull(builder::targetAltitude, valueIf(rangeFinderData::hasTargetAltitude, rangeFinderData::getTargetAltitude));
        return builder.build();
    }

    private SubAssetTelemetryData.SensorData mapSensorData(PayloadTelemetry.SensorData sensorData) {
        if (sensorData == null) {
            return null;
        }

        SubAssetTelemetryData.SensorData.SensorDataBuilder builder = SubAssetTelemetryData.SensorData.builder();
        setIfNotNull(builder::targetTemperature, valueIf(sensorData::hasTargetTemperature, sensorData::getTargetTemperature));
        return builder.build();
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
