package com.zequent.framework.edge.sdk.grpc;

import com.zequent.framework.common.proto.ErrorCodes;
import com.zequent.framework.common.proto.GlobalErrorMessage;
import com.zequent.framework.common.proto.RequestBase;
import com.zequent.framework.edge.sdk.interfaces.EdgeAdapterService;
import com.zequent.framework.edge.sdk.mapper.ProtoJsonMapper;
import com.zequent.framework.sdks.edge.proto.*;
import com.zequent.framework.edge.sdk.models.CommandResult;
import com.zequent.framework.edge.sdk.models.LiveStreamStopRequest;
import com.zequent.framework.utils.core.ProtobufHelpers;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GrpcService
public class EdgeAdapterGrpcServiceImpl extends MutinyEdgeAdapterServiceGrpc.EdgeAdapterServiceImplBase {

	private final EdgeAdapterService edgeAdapterService;

	private final ProtoJsonMapper protoJsonMapper;

	public EdgeAdapterGrpcServiceImpl(EdgeAdapterService edgeAdapterService, ProtoJsonMapper protoJsonMapper) {
		this.edgeAdapterService = edgeAdapterService;
		this.protoJsonMapper = protoJsonMapper;
	}

	@Override
	public Uni<EdgeResponse> takeOff(EdgeTakeOffRequest request) {
		log.info("Trying to Takeoff to Edge SN :  {}", request.getBase().getSn());
		var takeOffRequest = protoJsonMapper.map(request);
		return Uni.createFrom().completionStage(edgeAdapterService.takeOff(takeOffRequest))
				.map(result -> toEdgeResponse(request.getBase(), result))
				.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> returnToHome(EdgeReturnToHomeRequest request) {
		log.info("ReturnToHome for Edge SN: {}", request.getBase().getSn());
		var rthRequest = protoJsonMapper.map(request);
		return Uni.createFrom().completionStage(edgeAdapterService.returnToHome(rthRequest))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> goTo(com.zequent.framework.sdks.edge.proto.EdgeGoToRequest request) {
		log.info("GoTo for Edge SN: {}", request.getBase().getSn());
		var goToRequest = protoJsonMapper.map(request);
		return Uni.createFrom().completionStage(edgeAdapterService.goTo(goToRequest))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> enterManualControl(com.zequent.framework.sdks.edge.proto.EdgeManualControlRequest request) {
		log.info("EnterManualControl for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.enterManualControl(request.getBase().getSn()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> exitManualControl(com.zequent.framework.sdks.edge.proto.EdgeManualControlRequest request) {
		log.info("ExitManualControl for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.exitManualControl(request.getBase().getSn()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> manualControlInput(Multi<EdgeManualControlInputRequest> request) {
		log.info("ManualControlInput stream started");

		// Map the proto requests to model objects and broadcast to allow multiple subscriptions
		Multi<com.zequent.framework.edge.sdk.models.ManualControlInput> inputStream = request
			.onItem().transform(protoJsonMapper::map)
			.broadcast().toAllSubscribers();

		// Get the SN from the first item
		Uni<String> snUni = inputStream
			.collect().first()
			.onItem().ifNull().failWith(() -> new IllegalArgumentException("Empty input stream"))
			.map(com.zequent.framework.edge.sdk.models.ManualControlInput::getSn)
			.onItem().invoke(sn -> log.info("Starting manual control input stream for SN: {}", sn));

		// Forward the stream to edge adapter service
		return snUni
			.onItem().transformToUni(sn ->
				Uni.createFrom().completionStage(
					edgeAdapterService.manualControlInput(inputStream)
				)
				.map(result -> {
					var base = RequestBase.newBuilder()
						.setSn(sn)
						.setTid(java.util.UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build();
					return toEdgeResponse(base, result);
				})
				.onFailure().recoverWithItem(throwable -> {
					log.error("Manual control input failed for SN: {}", sn, throwable);
					var base = RequestBase.newBuilder()
						.setSn(sn)
						.setTid(java.util.UUID.randomUUID().toString())
						.setTimestamp(ProtobufHelpers.now())
						.build();
					return toErrorResponse(base, throwable);
				})
			);
	}

	@Override
	public Uni<EdgeResponse> lookAt(com.zequent.framework.sdks.edge.proto.EdgeLookAtRequest request) {
		log.info("LookAt for Edge SN: {}", request.getBase().getSn());
		var lookAtRequest = protoJsonMapper.map(request);
		return Uni.createFrom().completionStage(edgeAdapterService.lookAt(lookAtRequest))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> enableGimbalTracking(com.zequent.framework.sdks.edge.proto.EdgeEnableGimbalTrackingRequest request) {
		log.info("EnableGimbalTracking for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.enableGimbalTracking(request.getBase().getSn(), request.getEnabled()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> openCover(com.zequent.framework.sdks.edge.proto.EdgeOpenCoverRequest request) {
		log.info("OpenCover for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.openCover(request.getBase().getSn()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> closeCover(com.zequent.framework.sdks.edge.proto.EdgeCloseCoverRequest request) {
		log.info("CloseCover for Edge SN: {}", request.getBase().getSn());
		Boolean force = request.hasForce() ? request.getForce() : null;
		return Uni.createFrom().completionStage(edgeAdapterService.closeCover(request.getBase().getSn(), force))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> startCharging(com.zequent.framework.sdks.edge.proto.EdgeStartChargingRequest request) {
		log.info("StartCharging for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.startCharging(request.getBase().getSn()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> stopCharging(com.zequent.framework.sdks.edge.proto.EdgeStopChargingRequest request) {
		log.info("StopCharging for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.stopCharging(request.getBase().getSn()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> rebootAsset(com.zequent.framework.sdks.edge.proto.EdgeRebootAssetRequest request) {
		log.info("RebootAsset for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.rebootAsset(request.getBase().getSn()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> bootUpSubAsset(com.zequent.framework.sdks.edge.proto.EdgeBootSubAssetRequest request) {
		log.info("BootUpSubAsset for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.bootUpSubAsset(request.getBase().getSn()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> bootDownSubAsset(com.zequent.framework.sdks.edge.proto.EdgeBootSubAssetRequest request) {
		log.info("BootDownSubAsset for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.bootDownSubAsset(request.getBase().getSn()))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> enterOrCloseRemoteDebugMode(com.zequent.framework.sdks.edge.proto.EdgeRemoteDebugModeRequest request) {
		log.info("EnterRemoteDebugMode for Edge SN: {}", request.getBase().getSn());
		if (request.getEnabled()){
			return Uni.createFrom().completionStage(edgeAdapterService.enterRemoteDebugMode(request.getBase().getSn()))
					.map(result -> toEdgeResponse(request.getBase(), result))
					.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
		}else{
			return Uni.createFrom().completionStage(edgeAdapterService.closeRemoteDebugMode(request.getBase().getSn()))
					.map(result -> toEdgeResponse(request.getBase(), result))
					.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
		}
	}



	@Override
	public Uni<EdgeResponse> startLiveStream(com.zequent.framework.sdks.edge.proto.EdgeStartLiveStreamRequest request) {
		log.info("StartLiveStream for Edge SN: {}", request.getBase().getSn());
		var liveStreamRequest = protoJsonMapper.map(request);
		return Uni.createFrom().completionStage(edgeAdapterService.startLiveStream(liveStreamRequest))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> stopLiveStream(com.zequent.framework.sdks.edge.proto.EdgeStopLiveStreamRequest request) {
		log.info("StopLiveStream for Edge SN: {}", request.getBase().getSn());
		var stopRequest = new LiveStreamStopRequest(
			request.getBase().getSn(),
			generateUUID(),
			request.getRequest().getVideoId()
		);
		return Uni.createFrom().completionStage(edgeAdapterService.stopLiveStream(stopRequest))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> changeLens(com.zequent.framework.sdks.edge.proto.EdgeChangeCameraLensRequest request) {
		log.info("ChangeLens for Edge SN: {}", request.getBase().getSn());
		var lensRequest = protoJsonMapper.map(request);
		return Uni.createFrom().completionStage(edgeAdapterService.changeLens(lensRequest))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> changeZoom(com.zequent.framework.sdks.edge.proto.EdgeChangeCameraZoomRequest request) {
		log.info("ChangeZoom for Edge SN: {}", request.getBase().getSn());
		var zoomRequest = protoJsonMapper.map(request);
		return Uni.createFrom().completionStage(edgeAdapterService.changeZoom(zoomRequest))
			.map(result -> toEdgeResponse(request.getBase(), result))
			.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}


	@Override
	public Uni<EdgeResponse> registerAsset(EdgeRegisterAssetRequest request) {
		return super.registerAsset(request);
	}

	@Override
	public Uni<EdgeResponse> deRegisterAsset(EdgeDeRegisterAssetRequest request) {
		return super.deRegisterAsset(request);
	}


	@Override
	public Uni<EdgeResponse> startTask(EdgeStartTaskRequest request) {
		log.info("StartTask for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.startTask(request.getTaskId(),
						request.getBase().getTid()))
				.map(result -> toEdgeResponse(request.getBase(), result))
				.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	@Override
	public Uni<EdgeResponse> stopTask(EdgeStopTaskRequest request) {
		log.warn("StopTask for Edge SN: {}", request.getBase().getSn());
		return Uni.createFrom().completionStage(edgeAdapterService.stopTask(request.getTaskId()))
				.map(result -> toEdgeResponse(request.getBase(), result))
				.onFailure().recoverWithItem(throwable -> toErrorResponse(request.getBase(), throwable));
	}

	protected EdgeResponse toEdgeResponse(RequestBase base, CommandResult result) {
		EdgeResponse.Builder builder = EdgeResponse.newBuilder()
				.setTid(base.getTid())
				.setSn(base.getSn());



		if (result.getMessage() != null) {
			builder.setResponseMessage(result.getMessage());
		}

		// Handle NOT_IMPLEMENTED specifically
		if (result.isNotImplemented()) {
			builder.setHasErrors(true)
					.setError(GlobalErrorMessage.newBuilder()
							.setErrorMessage(result.getMessage())
							.setErrorCode(ErrorCodes.CLIENT_ERROR)
							.setTimestamp(ProtobufHelpers.now())
							.build());

			log.warn("Command not implemented: {} for SN: {}", result.getMessage(), base.getSn());
			return builder.build();
		}

		// Handle success/error
		if (result.isSuccess()) {
			builder.setHasErrors(false);
		} else {
			builder.setHasErrors(true)
					.setError(GlobalErrorMessage.newBuilder()
							.setErrorMessage(result.getMessage())
							.setErrorCode(ErrorCodes.ASSET_ERROR)
							.setTimestamp(ProtobufHelpers.now())
							.build());
		}

		return builder.build();
	}

	/**
	 * Convert exception to EdgeResponse (global error handler)
	 */
	protected EdgeResponse toErrorResponse(RequestBase base, Throwable error) {
		log.error("Error processing command for SN: %s, TID: %s", base.getSn(), base.getTid());

		// Determine error code based on exception type
		ErrorCodes errorCode = determineErrorCode(error);

		return EdgeResponse.newBuilder()
				.setHasErrors(true)
				.setTid(base.getTid())
				.setSn(base.getSn())
				.setError(GlobalErrorMessage.newBuilder()
						.setErrorMessage(
								error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName())
						.setErrorCode(errorCode)
						.setTimestamp(ProtobufHelpers.now())
						.build())
				.build();
	}

	private ErrorCodes determineErrorCode(Throwable error) {
		// You can customize this based on exception types
		if (error instanceof IllegalArgumentException) {
			return ErrorCodes.CLIENT_ERROR;
		} else if (error instanceof UnsupportedOperationException) {
			return ErrorCodes.CLIENT_ERROR;
		} else if (error instanceof java.util.concurrent.TimeoutException) {
			return ErrorCodes.SYSTEM_ERROR;
		}
		return ErrorCodes.SYSTEM_ERROR;
	}

	/**
	 * Helper to create RequestBase for errors that occur before processing
	 */
	protected RequestBase createErrorBase(String sn, String tid) {
		return RequestBase.newBuilder()
				.setSn(sn)
				.setTid(tid)
				.setTimestamp(ProtobufHelpers.now())
				.build();
	}


	private String generateUUID() {
		return java.util.UUID.randomUUID().toString();
	}
}
