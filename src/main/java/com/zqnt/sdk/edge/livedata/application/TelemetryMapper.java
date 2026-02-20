package com.zqnt.sdk.edge.livedata.application;

import com.google.protobuf.Timestamp;
import com.zequent.framework.common.proto.RequestBase;
import com.zequent.framework.services.livedata.proto.AssetTelemetry;
import com.zequent.framework.services.livedata.proto.ProduceTelemetryRequest;
import com.zequent.framework.services.livedata.proto.SubAssetTelemetry;
import com.zqnt.utils.core.ProtobufHelpers;
import com.zqnt.utils.edge.sdk.domains.AssetTelemetryData;
import com.zqnt.utils.edge.sdk.domains.SubAssetTelemetryData;
import com.zqnt.sdk.edge.adapter.domains.TelemetryRequestData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Simple mapper for converting telemetry POJO data to Proto messages
 */
public class TelemetryMapper {

    public TelemetryRequestData map(ProduceTelemetryRequest request) {
        if (request == null) return null;

        TelemetryRequestData data = new TelemetryRequestData();
        data.setSn(request.getBase().getSn());
        data.setTid(request.getBase().getTid());
        data.setTimestamp(toLocalDateTime(request.getBase().getTimestamp()));

        if (request.hasAssetTelemetry()) {
            data.setAssetTelemetry(map(request.getAssetTelemetry()));
        }
        if (request.hasSubAssetTelemetry()) {
            data.setSubAssetTelemetry(map(request.getSubAssetTelemetry()));
        }

        return data;
    }

    public ProduceTelemetryRequest map(TelemetryRequestData requestData) {
        if (requestData == null) return null;

        RequestBase.Builder baseBuilder = RequestBase.newBuilder()
                .setSn(requestData.getSn() != null ? requestData.getSn() : "")
                .setTid(requestData.getTid() != null ? requestData.getTid() : "");

        if (requestData.getTimestamp() != null) {
            baseBuilder.setTimestamp(toTimestamp(requestData.getTimestamp()));
        } else {
            baseBuilder.setTimestamp(ProtobufHelpers.now());
        }

        ProduceTelemetryRequest.Builder builder = ProduceTelemetryRequest.newBuilder()
                .setBase(baseBuilder.build());

        if (requestData.getAssetTelemetry() != null) {
            builder.setAssetTelemetry(map(requestData.getAssetTelemetry()));
        }
        if (requestData.getSubAssetTelemetry() != null) {
            builder.setSubAssetTelemetry(map(requestData.getSubAssetTelemetry()));
        }

        return builder.build();
    }

    public AssetTelemetry map(AssetTelemetryData telemetryData) {
        if (telemetryData == null) return null;

        AssetTelemetry.Builder builder = AssetTelemetry.newBuilder();

        // ID and identification fields
        if (telemetryData.getId() != null) {
            builder.setId(telemetryData.getId());
        }

        // Timestamp
        if (telemetryData.getTimestamp() != null) {
            builder.setTimestamp(toTimestamp(telemetryData.getTimestamp()));
        }

        // Position fields
        if (telemetryData.getLatitude() != null) {
            builder.setLatitude(telemetryData.getLatitude());
        }
        if (telemetryData.getLongitude() != null) {
            builder.setLongitude(telemetryData.getLongitude());
        }
        if (telemetryData.getAbsoluteAltitude() != null) {
            builder.setAbsoluteAltitude(telemetryData.getAbsoluteAltitude());
        }
        if (telemetryData.getRelativeAltitude() != null) {
            builder.setRelativeAltitude(telemetryData.getRelativeAltitude());
        }
        if (telemetryData.getHeading() != null) {
            builder.setHeading(telemetryData.getHeading());
        }

        // Environmental fields
        if (telemetryData.getEnvironmentTemp() != null) {
            builder.setEnvironmentTemp(telemetryData.getEnvironmentTemp());
        }
        if (telemetryData.getInsideTemp() != null) {
            builder.setInsideTemp(telemetryData.getInsideTemp());
        }
        if (telemetryData.getHumidity() != null) {
            builder.setHumidity(telemetryData.getHumidity());
        }
        if (telemetryData.getRainfall() != null) {
            builder.setRainfall(telemetryData.getRainfall());
        }
        if (telemetryData.getWindSpeed() != null) {
            builder.setWindSpeed(telemetryData.getWindSpeed());
        }

        // Power and voltage fields
        if (telemetryData.getWorkingVoltage() != null) {
            builder.setWorkingVoltage(telemetryData.getWorkingVoltage());
        }
        if (telemetryData.getWorkingCurrent() != null) {
            builder.setWorkingCurrent(telemetryData.getWorkingCurrent());
        }
        if (telemetryData.getSupplyVoltage() != null) {
            builder.setSupplyVoltage(telemetryData.getSupplyVoltage());
        }

        // Status fields
        if (telemetryData.getCoverState() != null) {
            builder.setCoverState(telemetryData.getCoverState());
        }
        if (telemetryData.getSubAssetAtHome() != null) {
            builder.setSubAssetAtHome(telemetryData.getSubAssetAtHome());
        }
        if (telemetryData.getSubAssetCharging() != null) {
            builder.setSubAssetCharging(telemetryData.getSubAssetCharging());
        }
        if (telemetryData.getSubAssetPercentage() != null) {
            builder.setSubAssetPercentage(telemetryData.getSubAssetPercentage());
        }
        if (telemetryData.getDebugModeOpen() != null) {
            builder.setDebugModeOpen(telemetryData.getDebugModeOpen());
        }
        if (telemetryData.getHasActiveManualControlSession() != null) {
            builder.setHasActiveManualControlSession(telemetryData.getHasActiveManualControlSession());
        }
        if (telemetryData.getManualControlState() != null) {
            builder.setManualControlState(telemetryData.getManualControlState());
        }

        // GPS fields
        if (telemetryData.getPositionValid() != null) {
            builder.setPositionValid(telemetryData.getPositionValid());
        }

        // Mode
        if (telemetryData.getMode() != null) {
            builder.setMode(telemetryData.getMode());
        }

        // Nested objects

        // PositionState (contains gpsNumber, rtkNumber, quality)
        if (telemetryData.getPositionState() != null) {
            com.zequent.framework.services.livedata.proto.AssetTelemetry.PositionState.Builder posStateBuilder =
                    com.zequent.framework.services.livedata.proto.AssetTelemetry.PositionState.newBuilder();

            if (telemetryData.getPositionState().getGpsNumber() != null) {
                posStateBuilder.setGpsNumber(telemetryData.getPositionState().getGpsNumber());
            }
            if (telemetryData.getPositionState().getRtkNumber() != null) {
                posStateBuilder.setRtkNumber(telemetryData.getPositionState().getRtkNumber());
            }
            if (telemetryData.getPositionState().getQuality() != null) {
                posStateBuilder.setQuality(telemetryData.getPositionState().getQuality());
            }
            builder.setPositionState(posStateBuilder.build());
        }

        // SubAssetInformation (contains sn, model, paired, online)
        if (telemetryData.getSubAssetInformation() != null) {
            AssetTelemetry.AssetSubAssetInformation.Builder subAssetBuilder = AssetTelemetry.AssetSubAssetInformation.newBuilder();

            if (telemetryData.getSubAssetInformation().getSn() != null) {
                subAssetBuilder.setSn(telemetryData.getSubAssetInformation().getSn());
            }
            if (telemetryData.getSubAssetInformation().getModel() != null) {
                subAssetBuilder.setModel(telemetryData.getSubAssetInformation().getModel());
            }
            if (telemetryData.getSubAssetInformation().getPaired() != null) {
                subAssetBuilder.setPaired(telemetryData.getSubAssetInformation().getPaired());
            }
            if (telemetryData.getSubAssetInformation().getOnline() != null) {
                subAssetBuilder.setOnline(telemetryData.getSubAssetInformation().getOnline());
            }
            builder.setSubAssetInformation(subAssetBuilder.build());
        }

        // NetworkInformation (contains type, rate, quality)
        if (telemetryData.getNetworkInformation() != null) {
            AssetTelemetry.AssetNetworkInformation.Builder netInfoBuilder = AssetTelemetry.AssetNetworkInformation.newBuilder();

            if (telemetryData.getNetworkInformation().getType() != null) {
                netInfoBuilder.setType(telemetryData.getNetworkInformation().getType());
            }
            if (telemetryData.getNetworkInformation().getRate() != null) {
                netInfoBuilder.setRate(telemetryData.getNetworkInformation().getRate());
            }
            if (telemetryData.getNetworkInformation().getQuality() != null) {
                netInfoBuilder.setQuality(telemetryData.getNetworkInformation().getQuality());
            }
            builder.setNetworkInformation(netInfoBuilder.build());
        }

        // AirConditioner (contains state, switchTime)
        if (telemetryData.getAirConditioner() != null) {
            AssetTelemetry.AssetAirConditioner.Builder airCondBuilder = AssetTelemetry.AssetAirConditioner.newBuilder();

            if (telemetryData.getAirConditioner().getState() != null) {
                airCondBuilder.setState(telemetryData.getAirConditioner().getState());
            }
            if (telemetryData.getAirConditioner().getSwitchTime() != null) {
                airCondBuilder.setSwitchTime(telemetryData.getAirConditioner().getSwitchTime());
            }
            builder.setAirConditioner(airCondBuilder.build());
        }

        return builder.build();
    }

    public SubAssetTelemetry map(SubAssetTelemetryData telemetryData) {
        if (telemetryData == null) return null;

        SubAssetTelemetry.Builder builder = SubAssetTelemetry.newBuilder();

        // ID
        if (telemetryData.getId() != null) {
            builder.setId(telemetryData.getId());
        }

        // Timestamp
        if (telemetryData.getTimestamp() != null) {
            builder.setTimestamp(toTimestamp(telemetryData.getTimestamp()));
        }

        // Position fields
        if (telemetryData.getLatitude() != null) {
            builder.setLatitude(telemetryData.getLatitude());
        }
        if (telemetryData.getLongitude() != null) {
            builder.setLongitude(telemetryData.getLongitude());
        }
        if (telemetryData.getAbsoluteAltitude() != null) {
            builder.setAbsoluteAltitude(telemetryData.getAbsoluteAltitude());
        }
        if (telemetryData.getRelativeAltitude() != null) {
            builder.setRelativeAltitude(telemetryData.getRelativeAltitude());
        }
        if (telemetryData.getHeading() != null) {
            builder.setHeading(telemetryData.getHeading());
        }

        // Speed fields
        if (telemetryData.getHorizontalSpeed() != null) {
            builder.setHorizontalSpeed(telemetryData.getHorizontalSpeed());
        }
        if (telemetryData.getVerticalSpeed() != null) {
            builder.setVerticalSpeed(telemetryData.getVerticalSpeed());
        }

        // Wind
        if (telemetryData.getWindSpeed() != null) {
            builder.setWindSpeed(telemetryData.getWindSpeed());
        }
        if (telemetryData.getWindDirection() != null) {
            builder.setWindDirection(telemetryData.getWindDirection());
        }

        // Flight parameters
        if (telemetryData.getGear() != null) {
            builder.setGear(telemetryData.getGear());
        }
        if (telemetryData.getHeightLimit() != null) {
            builder.setHeightLimit(telemetryData.getHeightLimit());
        }
        if (telemetryData.getHomeDistance() != null) {
            builder.setHomeDistance(telemetryData.getHomeDistance());
        }
        if (telemetryData.getTotalMovementDistance() != null) {
            builder.setTotalMovementDistance(telemetryData.getTotalMovementDistance());
        }
        if (telemetryData.getTotalMovementTime() != null) {
            builder.setTotalMovementTime(telemetryData.getTotalMovementTime());
        }

        // Country
        if (telemetryData.getCountry() != null) {
            builder.setCountry(telemetryData.getCountry());
        }

        // Mode
        if (telemetryData.getMode() != null) {
            builder.setMode(telemetryData.getMode());
        }

        // Battery Information
        if (telemetryData.getBatteryInformation() != null && telemetryData.getBatteryInformation().getPercentage() != null) {
            SubAssetTelemetry.SubAssetBatteryInformation.Builder batteryBuilder =
                    SubAssetTelemetry.SubAssetBatteryInformation.newBuilder();

            if (telemetryData.getBatteryInformation().getPercentage() != null) {
                batteryBuilder.setPercentage(telemetryData.getBatteryInformation().getPercentage());
            }
            if (telemetryData.getBatteryInformation().getRemainingTime() != null) {
                batteryBuilder.setRemainingTime(telemetryData.getBatteryInformation().getRemainingTime());
            }
            if (telemetryData.getBatteryInformation().getReturnToHomePower() != null) {
                batteryBuilder.setReturnToHomePower(telemetryData.getBatteryInformation().getReturnToHomePower());
            }

            builder.setBatteryInformation(batteryBuilder.build());
        }

        // Payload Telemetry (nested object - would need proper mapping if available)
        // if (telemetryData.getPayloadTelemetry() != null) {
        //     builder.setPayloadTelemetry(...);
        // }

        return builder.build();
    }

    public AssetTelemetryData map(AssetTelemetry telemetry) {
        if (telemetry == null) return null;

        AssetTelemetryData.AssetTelemetryDataBuilder builder = AssetTelemetryData.builder()
                // Timestamp
                .timestamp(telemetry.hasTimestamp() ? toLocalDateTime(telemetry.getTimestamp()) : null)
                // Position
                .latitude(telemetry.hasLatitude() ? telemetry.getLatitude() : null)
                .longitude(telemetry.hasLongitude() ? telemetry.getLongitude() : null)
                .absoluteAltitude(telemetry.hasAbsoluteAltitude() ? telemetry.getAbsoluteAltitude() : null)
                .relativeAltitude(telemetry.hasRelativeAltitude() ? telemetry.getRelativeAltitude() : null)
                .heading(telemetry.hasHeading() ? telemetry.getHeading() : null)
                // Environmental
                .environmentTemp(telemetry.hasEnvironmentTemp() ? telemetry.getEnvironmentTemp() : null)
                .insideTemp(telemetry.hasInsideTemp() ? telemetry.getInsideTemp() : null)
                .humidity(telemetry.hasHumidity() ? telemetry.getHumidity() : null)
                .rainfall(telemetry.hasRainfall() ? telemetry.getRainfall() : null)
                .windSpeed(telemetry.hasWindSpeed() ? telemetry.getWindSpeed() : null)
                // Power
                .workingVoltage(telemetry.hasWorkingVoltage() ? telemetry.getWorkingVoltage() : null)
                .workingCurrent(telemetry.hasWorkingCurrent() ? telemetry.getWorkingCurrent() : null)
                .supplyVoltage(telemetry.hasSupplyVoltage() ? telemetry.getSupplyVoltage() : null)
                // Status
                .coverState(telemetry.hasCoverState() ? telemetry.getCoverState() : null)
                .subAssetAtHome(telemetry.hasSubAssetAtHome() ? telemetry.getSubAssetAtHome() : null)
                .subAssetCharging(telemetry.hasSubAssetCharging() ? telemetry.getSubAssetCharging() : null)
                .subAssetPercentage(telemetry.hasSubAssetPercentage() ? telemetry.getSubAssetPercentage() : null)
                .debugModeOpen(telemetry.hasDebugModeOpen() ? telemetry.getDebugModeOpen() : null)
                .hasActiveManualControlSession(telemetry.hasHasActiveManualControlSession() ? telemetry.getHasActiveManualControlSession() : null)
                .manualControlState(telemetry.hasManualControlState() ? telemetry.getManualControlState() : null)
                // GPS
                .positionValid(telemetry.hasPositionValid() ? telemetry.getPositionValid() : null)
                // Mode
                .mode(telemetry.hasMode() ? telemetry.getMode() : null);

        // Nested object: PositionState
        if (telemetry.hasPositionState()) {
            AssetTelemetryData.PositionState.PositionStateBuilder posStateBuilder = AssetTelemetryData.PositionState.builder();

            if (telemetry.getPositionState().hasGpsNumber()) {
                posStateBuilder.gpsNumber(telemetry.getPositionState().getGpsNumber());
            }
            if (telemetry.getPositionState().hasRtkNumber()) {
                posStateBuilder.rtkNumber(telemetry.getPositionState().getRtkNumber());
            }
            if (telemetry.getPositionState().hasQuality()) {
                posStateBuilder.quality(telemetry.getPositionState().getQuality());
            }

            builder.positionState(posStateBuilder.build());
        }

        // Nested object: SubAssetInformation
        if (telemetry.hasSubAssetInformation()) {
            AssetTelemetryData.SubAssetInformation.SubAssetInformationBuilder subAssetBuilder =
                    AssetTelemetryData.SubAssetInformation.builder();

            if (telemetry.getSubAssetInformation().hasSn()) {
                subAssetBuilder.sn(telemetry.getSubAssetInformation().getSn());
            }
            if (telemetry.getSubAssetInformation().hasModel()) {
                subAssetBuilder.model(telemetry.getSubAssetInformation().getModel());
            }
            if (telemetry.getSubAssetInformation().hasPaired()) {
                subAssetBuilder.paired(telemetry.getSubAssetInformation().getPaired());
            }
            if (telemetry.getSubAssetInformation().hasOnline()) {
                subAssetBuilder.online(telemetry.getSubAssetInformation().getOnline());
            }

            builder.subAssetInformation(subAssetBuilder.build());
        }

        // Nested object: NetworkInformation
        if (telemetry.hasNetworkInformation()) {
            AssetTelemetryData.NetworkInformation.NetworkInformationBuilder netInfoBuilder =
                    AssetTelemetryData.NetworkInformation.builder();

            if (telemetry.getNetworkInformation().hasType()) {
                netInfoBuilder.type(telemetry.getNetworkInformation().getType());
            }
            if (telemetry.getNetworkInformation().hasRate()) {
                netInfoBuilder.rate(telemetry.getNetworkInformation().getRate());
            }
            if (telemetry.getNetworkInformation().hasQuality()) {
                netInfoBuilder.quality(telemetry.getNetworkInformation().getQuality());
            }

            builder.networkInformation(netInfoBuilder.build());
        }

        // Nested object: AirConditioner
        if (telemetry.hasAirConditioner()) {
            AssetTelemetryData.AirConditioner.AirConditionerBuilder airCondBuilder =
                    AssetTelemetryData.AirConditioner.builder();

            if (telemetry.getAirConditioner().hasState()) {
                airCondBuilder.state(telemetry.getAirConditioner().getState());
            }
            if (telemetry.getAirConditioner().hasSwitchTime()) {
                airCondBuilder.switchTime(telemetry.getAirConditioner().getSwitchTime());
            }

            builder.airConditioner(airCondBuilder.build());
        }

        return builder.build();
    }

    public SubAssetTelemetryData map(SubAssetTelemetry telemetry) {
        if (telemetry == null) return null;

        SubAssetTelemetryData.SubAssetTelemetryDataBuilder builder = SubAssetTelemetryData.builder()
                // Timestamp
                .timestamp(telemetry.hasTimestamp() ? toLocalDateTime(telemetry.getTimestamp()) : null)
                // Position
                .latitude(telemetry.hasLatitude() ? telemetry.getLatitude() : null)
                .longitude(telemetry.hasLongitude() ? telemetry.getLongitude() : null)
                .absoluteAltitude(telemetry.hasAbsoluteAltitude() ? telemetry.getAbsoluteAltitude() : null)
                .relativeAltitude(telemetry.hasRelativeAltitude() ? telemetry.getRelativeAltitude() : null)
                .heading(telemetry.hasHeading() ? telemetry.getHeading() : null)
                // Speed
                .horizontalSpeed(telemetry.hasHorizontalSpeed() ? telemetry.getHorizontalSpeed() : null)
                .verticalSpeed(telemetry.hasVerticalSpeed() ? telemetry.getVerticalSpeed() : null)
                // Wind
                .windSpeed(telemetry.hasWindSpeed() ? telemetry.getWindSpeed() : null)
                .windDirection(telemetry.hasWindDirection() ? telemetry.getWindDirection() : null)
                // Flight parameters
                .gear(telemetry.hasGear() ? telemetry.getGear() : null)
                .heightLimit(telemetry.hasHeightLimit() ? telemetry.getHeightLimit() : null)
                .homeDistance(telemetry.hasHomeDistance() ? telemetry.getHomeDistance() : null)
                .totalMovementDistance(telemetry.hasTotalMovementDistance() ? telemetry.getTotalMovementDistance() : null)
                .totalMovementTime(telemetry.hasTotalMovementTime() ? telemetry.getTotalMovementTime() : null)
                // Country
                .country(telemetry.hasCountry() ? telemetry.getCountry() : null)
                // Mode
                .mode(telemetry.hasMode() ? telemetry.getMode() : null);

        // Nested object: BatteryInformation
        if (telemetry.hasBatteryInformation()) {
            SubAssetTelemetryData.BatteryInformation.BatteryInformationBuilder batteryBuilder =
                    SubAssetTelemetryData.BatteryInformation.builder();

            if (telemetry.getBatteryInformation().hasPercentage()) {
                batteryBuilder.percentage(telemetry.getBatteryInformation().getPercentage());
            }
            if (telemetry.getBatteryInformation().hasRemainingTime()) {
                batteryBuilder.remainingTime(telemetry.getBatteryInformation().getRemainingTime());
            }
            if (telemetry.getBatteryInformation().hasReturnToHomePower()) {
                batteryBuilder.returnToHomePower(telemetry.getBatteryInformation().getReturnToHomePower());
            }

            builder.batteryInformation(batteryBuilder.build());
        }

        // Nested object: PayloadTelemetry (if available in proto)
        if (telemetry.hasPayloadTelemetry()) {
            SubAssetTelemetryData.PayloadTelemetry.PayloadTelemetryBuilder payloadBuilder =
                    SubAssetTelemetryData.PayloadTelemetry.builder();

            payloadBuilder.id(telemetry.getPayloadTelemetry().getId());
            if (telemetry.getPayloadTelemetry().hasTimestamp()) {
                payloadBuilder.timestamp(toLocalDateTime(telemetry.getPayloadTelemetry().getTimestamp()));
            }
            payloadBuilder.name(telemetry.getPayloadTelemetry().getName());

            // CameraData
            if (telemetry.getPayloadTelemetry().hasCameraData()) {
                SubAssetTelemetryData.CameraData.CameraDataBuilder cameraBuilder =
                        SubAssetTelemetryData.CameraData.builder();

                if (telemetry.getPayloadTelemetry().getCameraData().hasCurrentLens()) {
                    cameraBuilder.currentLens(telemetry.getPayloadTelemetry().getCameraData().getCurrentLens());
                }
                if (telemetry.getPayloadTelemetry().getCameraData().hasGimbalPitch()) {
                    cameraBuilder.gimbalPitch(telemetry.getPayloadTelemetry().getCameraData().getGimbalPitch());
                }
                if (telemetry.getPayloadTelemetry().getCameraData().hasGimbalYaw()) {
                    cameraBuilder.gimbalYaw(telemetry.getPayloadTelemetry().getCameraData().getGimbalYaw());
                }
                if (telemetry.getPayloadTelemetry().getCameraData().hasGimbalRoll()) {
                    cameraBuilder.gimbalRoll(telemetry.getPayloadTelemetry().getCameraData().getGimbalRoll());
                }
                if (telemetry.getPayloadTelemetry().getCameraData().hasZoomFactor()) {
                    cameraBuilder.zoomFactor(telemetry.getPayloadTelemetry().getCameraData().getZoomFactor());
                }

                payloadBuilder.cameraData(cameraBuilder.build());
            }

            // RangeFinderData
            if (telemetry.getPayloadTelemetry().hasRangeFinderData()) {
                SubAssetTelemetryData.RangeFinderData.RangeFinderDataBuilder rangeFinderBuilder =
                        SubAssetTelemetryData.RangeFinderData.builder();

                if (telemetry.getPayloadTelemetry().getRangeFinderData().hasTargetLatitude()) {
                    rangeFinderBuilder.targetLatitude(telemetry.getPayloadTelemetry().getRangeFinderData().getTargetLatitude());
                }
                if (telemetry.getPayloadTelemetry().getRangeFinderData().hasTargetLongitude()) {
                    rangeFinderBuilder.targetLongitude(telemetry.getPayloadTelemetry().getRangeFinderData().getTargetLongitude());
                }
                if (telemetry.getPayloadTelemetry().getRangeFinderData().hasTargetDistance()) {
                    rangeFinderBuilder.targetDistance(telemetry.getPayloadTelemetry().getRangeFinderData().getTargetDistance());
                }
                if (telemetry.getPayloadTelemetry().getRangeFinderData().hasTargetAltitude()) {
                    rangeFinderBuilder.targetAltitude(telemetry.getPayloadTelemetry().getRangeFinderData().getTargetAltitude());
                }

                payloadBuilder.rangeFinderData(rangeFinderBuilder.build());
            }

            // SensorData
            if (telemetry.getPayloadTelemetry().hasSensorData()) {
                SubAssetTelemetryData.SensorData.SensorDataBuilder sensorBuilder =
                        SubAssetTelemetryData.SensorData.builder();

                if (telemetry.getPayloadTelemetry().getSensorData().hasTargetTemperature()) {
                    sensorBuilder.targetTemperature(telemetry.getPayloadTelemetry().getSensorData().getTargetTemperature());
                }

                payloadBuilder.sensorData(sensorBuilder.build());
            }

            builder.payloadTelemetry(payloadBuilder.build());
        }

        return builder.build();
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
