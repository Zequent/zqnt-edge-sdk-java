package com.zqnt.sdk.edge.livedata.application;

import com.google.protobuf.Timestamp;
import com.zqnt.sdk.edge.adapter.domains.DetectionRequestData;
import com.zqnt.utils.common.proto.BoundingBox;
import com.zqnt.utils.common.proto.DetectionBatch;
import com.zqnt.utils.common.proto.DetectionResult;
import com.zqnt.utils.common.proto.RequestBase;
import com.zqnt.utils.core.ProtobufHelpers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

/**
 * Mapper for converting detection POJO data to Proto messages and vice versa.
 */
public class DetectionMapper {

	public DetectionRequestData map(DetectionBatch batch) {
		if (batch == null) return null;

		DetectionRequestData data = new DetectionRequestData();
		data.setSn(batch.getBase().getSn());
		data.setTid(batch.getBase().getTid());
		data.setTimestamp(toLocalDateTime(batch.getBase().getTimestamp()));

		if (batch.hasStreamUrl()) {
			data.setStreamUrl(batch.getStreamUrl());
		}

		List<DetectionRequestData.DetectionResultData> results = batch.getDetectionsList().stream()
				.map(this::map)
				.toList();
		data.setDetections(results);

		return data;
	}

	public DetectionBatch map(DetectionRequestData requestData) {
		if (requestData == null) return null;

		RequestBase.Builder baseBuilder = RequestBase.newBuilder()
				.setSn(requestData.getSn() != null ? requestData.getSn() : "")
				.setTid(requestData.getTid() != null ? requestData.getTid() : "");

		if (requestData.getTimestamp() != null) {
			baseBuilder.setTimestamp(toTimestamp(requestData.getTimestamp()));
		} else {
			baseBuilder.setTimestamp(ProtobufHelpers.now());
		}

		DetectionBatch.Builder builder = DetectionBatch.newBuilder()
				.setBase(baseBuilder.build());

		if (requestData.getStreamUrl() != null) {
			builder.setStreamUrl(requestData.getStreamUrl());
		}

		List<DetectionRequestData.DetectionResultData> detections =
				requestData.getDetections() != null ? requestData.getDetections() : Collections.emptyList();

		for (DetectionRequestData.DetectionResultData result : detections) {
			builder.addDetections(map(result));
		}

		return builder.build();
	}

	private DetectionRequestData.DetectionResultData map(DetectionResult proto) {
		DetectionRequestData.DetectionResultData result = new DetectionRequestData.DetectionResultData();

		if (proto.hasObjectId()) result.setObjectId(proto.getObjectId());
		if (proto.hasObjectType()) result.setObjectType(proto.getObjectType());
		if (proto.hasConfidence()) result.setConfidence(proto.getConfidence());

		if (proto.hasBoundingBox()) {
			DetectionRequestData.BoundingBoxData box = new DetectionRequestData.BoundingBoxData();
			box.setX(proto.getBoundingBox().getX());
			box.setY(proto.getBoundingBox().getY());
			box.setWidth(proto.getBoundingBox().getWidth());
			box.setHeight(proto.getBoundingBox().getHeight());
			result.setBoundingBox(box);
		}

		return result;
	}

	private DetectionResult map(DetectionRequestData.DetectionResultData data) {
		DetectionResult.Builder builder = DetectionResult.newBuilder();

		if (data.getObjectId() != null) builder.setObjectId(data.getObjectId());
		if (data.getObjectType() != null) builder.setObjectType(data.getObjectType());
		if (data.getConfidence() != null) builder.setConfidence(data.getConfidence());

		if (data.getBoundingBox() != null) {
			BoundingBox.Builder boxBuilder = BoundingBox.newBuilder();
			DetectionRequestData.BoundingBoxData box = data.getBoundingBox();
			if (box.getX() != null) boxBuilder.setX(box.getX());
			if (box.getY() != null) boxBuilder.setY(box.getY());
			if (box.getWidth() != null) boxBuilder.setWidth(box.getWidth());
			if (box.getHeight() != null) boxBuilder.setHeight(box.getHeight());
			builder.setBoundingBox(boxBuilder.build());
		}

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
