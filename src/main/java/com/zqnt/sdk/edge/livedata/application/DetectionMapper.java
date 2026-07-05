package com.zqnt.sdk.edge.livedata.application;

import com.zqnt.sdk.edge.adapter.domains.DetectionRequestData;
import com.zqnt.sdk.edge.support.MapperSupport;
import com.zqnt.utils.common.proto.BoundingBox;
import com.zqnt.utils.common.proto.DetectionBatch;
import com.zqnt.utils.common.proto.DetectionResult;

import java.util.Collections;
import java.util.List;

/**
 * Mapper for converting detection POJO data to Proto messages and vice versa.
 */
public class DetectionMapper {

	public DetectionRequestData map(DetectionBatch batch) {
		if (batch == null) return null;

		DetectionRequestData data = new DetectionRequestData();
		if (batch.hasBase()) {
			data.setSn(batch.getBase().getSn());
			data.setTid(batch.getBase().getTid());
			data.setTimestamp(MapperSupport.toLocalDateTime(batch.getBase().getTimestamp()));
		}

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

		var baseBuilder = MapperSupport.requestBase(requestData.getSn(), requestData.getTid(), requestData.getTimestamp());

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

		MapperSupport.set(result::setObjectId, proto.hasObjectId() ? proto.getObjectId() : null);
		MapperSupport.set(result::setObjectType, proto.hasObjectType() ? proto.getObjectType() : null);
		MapperSupport.set(result::setConfidence, proto.hasConfidence() ? proto.getConfidence() : null);

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

		MapperSupport.set(builder::setObjectId, data.getObjectId());
		MapperSupport.set(builder::setObjectType, data.getObjectType());
		MapperSupport.set(builder::setConfidence, data.getConfidence());

		if (data.getBoundingBox() != null) {
			BoundingBox.Builder boxBuilder = BoundingBox.newBuilder();
			DetectionRequestData.BoundingBoxData box = data.getBoundingBox();
			MapperSupport.set(boxBuilder::setX, box.getX());
			MapperSupport.set(boxBuilder::setY, box.getY());
			MapperSupport.set(boxBuilder::setWidth, box.getWidth());
			MapperSupport.set(boxBuilder::setHeight, box.getHeight());
			builder.setBoundingBox(boxBuilder.build());
		}

		return builder.build();
	}
}
