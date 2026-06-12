package com.zqnt.sdk.edge.adapter.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionRequestData {

	private String tid;
	private String sn;
	private LocalDateTime timestamp;
	private String streamUrl;
	private List<DetectionResultData> detections;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DetectionResultData {
		private String objectId;
		private String objectType;
		private Float confidence;
		private BoundingBoxData boundingBox;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BoundingBoxData {
		private Float x;
		private Float y;
		private Float width;
		private Float height;
	}
}
