package com.zqnt.sdk.edge.adapter.grpc;

import com.zequent.framework.common.proto.ErrorCodes;
import com.zequent.framework.common.proto.GlobalErrorMessage;
import com.zequent.framework.common.proto.RequestBase;
import com.zqnt.sdk.edge.adapter.application.EdgeAdapterService;
import com.zqnt.sdk.edge.adapter.domains.CommandResult;
import com.zqnt.sdk.edge.adapter.domains.LiveStreamStopRequest;
import com.zqnt.sdk.edge.adapter.domains.ManualControlInput;
import com.zqnt.sdk.edge.application.ProtoJsonMapper;
import com.zequent.framework.sdks.edge.proto.*;
import com.zequent.framework.utils.core.ProtobufHelpers;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EdgeAdapterGrpcServiceImpl extends EdgeAdapterServiceGrpc.EdgeAdapterServiceImplBase {

	private final EdgeAdapterService edgeAdapterService;
	private final ProtoJsonMapper protoJsonMapper;

	public EdgeAdapterGrpcServiceImpl(EdgeAdapterService edgeAdapterService, ProtoJsonMapper protoJsonMapper) {
		this.edgeAdapterService = edgeAdapterService;
		this.protoJsonMapper = protoJsonMapper;
	}

	@Override
	public void takeOff(EdgeTakeOffRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("Trying to Takeoff to Edge SN :  {}", request.getBase().getSn());
		var takeOffRequest = protoJsonMapper.map(request);
		edgeAdapterService.takeOff(takeOffRequest)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void returnToHome(EdgeReturnToHomeRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("ReturnToHome for Edge SN: {}", request.getBase().getSn());
		var rthRequest = protoJsonMapper.map(request);
		edgeAdapterService.returnToHome(rthRequest)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void goTo(EdgeGoToRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("GoTo for Edge SN: {}", request.getBase().getSn());
		var goToRequest = protoJsonMapper.map(request);
		edgeAdapterService.goTo(goToRequest)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void enterManualControl(EdgeManualControlRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("EnterManualControl for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.enterManualControl(request.getBase().getSn())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void exitManualControl(EdgeManualControlRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("ExitManualControl for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.exitManualControl(request.getBase().getSn())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public StreamObserver<EdgeManualControlInputRequest> manualControlInput(StreamObserver<EdgeResponse> responseObserver) {
		log.info("ManualControlInput stream started");

		List<ManualControlInput> inputs = new ArrayList<>();

		return new StreamObserver<EdgeManualControlInputRequest>() {
			private String sn;

			@Override
			public void onNext(EdgeManualControlInputRequest request) {
				ManualControlInput input = protoJsonMapper.map(request);
				inputs.add(input);
				if (sn == null) {
					sn = input.getSn();
					log.info("Starting manual control input stream for SN: {}", sn);
				}
			}

			@Override
			public void onError(Throwable t) {
				log.error("Manual control input stream error", t);
				responseObserver.onError(t);
			}

			@Override
			public void onCompleted() {
				if (inputs.isEmpty()) {
					responseObserver.onError(new IllegalArgumentException("Empty input stream"));
					return;
				}

				String finalSn = sn;
				edgeAdapterService.manualControlInput(inputs.stream())
						.thenAccept(result -> {
							var base = RequestBase.newBuilder()
									.setSn(finalSn)
									.setTid(java.util.UUID.randomUUID().toString())
									.setTimestamp(ProtobufHelpers.now())
									.build();
							responseObserver.onNext(toEdgeResponse(base, result));
							responseObserver.onCompleted();
						})
						.exceptionally(throwable -> {
							log.error("Manual control input failed for SN: {}", finalSn, throwable);
							var base = RequestBase.newBuilder()
									.setSn(finalSn)
									.setTid(java.util.UUID.randomUUID().toString())
									.setTimestamp(ProtobufHelpers.now())
									.build();
							responseObserver.onNext(toErrorResponse(base, throwable));
							responseObserver.onCompleted();
							return null;
						});
			}
		};
	}

	@Override
	public void lookAt(EdgeLookAtRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("LookAt for Edge SN: {}", request.getBase().getSn());
		var lookAtRequest = protoJsonMapper.map(request);
		edgeAdapterService.lookAt(lookAtRequest)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void enableGimbalTracking(EdgeEnableGimbalTrackingRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("EnableGimbalTracking for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.enableGimbalTracking(request.getBase().getSn(), request.getEnabled())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void openCover(EdgeOpenCoverRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("OpenCover for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.openCover(request.getBase().getSn())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void closeCover(EdgeCloseCoverRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("CloseCover for Edge SN: {}", request.getBase().getSn());
		Boolean force = request.hasForce() ? request.getForce() : null;
		edgeAdapterService.closeCover(request.getBase().getSn(), force)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void startCharging(EdgeStartChargingRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("StartCharging for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.startCharging(request.getBase().getSn())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void stopCharging(EdgeStopChargingRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("StopCharging for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.stopCharging(request.getBase().getSn())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void rebootAsset(EdgeRebootAssetRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("RebootAsset for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.rebootAsset(request.getBase().getSn())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void bootUpSubAsset(EdgeBootSubAssetRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("BootUpSubAsset for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.bootUpSubAsset(request.getBase().getSn())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void bootDownSubAsset(EdgeBootSubAssetRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("BootDownSubAsset for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.bootDownSubAsset(request.getBase().getSn())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void enterOrCloseRemoteDebugMode(EdgeRemoteDebugModeRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("EnterRemoteDebugMode for Edge SN: {}", request.getBase().getSn());
		if (request.getEnabled()) {
			edgeAdapterService.enterRemoteDebugMode(request.getBase().getSn())
					.thenAccept(result -> {
						responseObserver.onNext(toEdgeResponse(request.getBase(), result));
						responseObserver.onCompleted();
					})
					.exceptionally(throwable -> {
						responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
						responseObserver.onCompleted();
						return null;
					});
		} else {
			edgeAdapterService.closeRemoteDebugMode(request.getBase().getSn())
					.thenAccept(result -> {
						responseObserver.onNext(toEdgeResponse(request.getBase(), result));
						responseObserver.onCompleted();
					})
					.exceptionally(throwable -> {
						responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
						responseObserver.onCompleted();
						return null;
					});
		}
	}

	@Override
	public void startLiveStream(EdgeStartLiveStreamRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("StartLiveStream for Edge SN: {}", request.getBase().getSn());
		var liveStreamRequest = protoJsonMapper.map(request);
		edgeAdapterService.startLiveStream(liveStreamRequest)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void stopLiveStream(EdgeStopLiveStreamRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("StopLiveStream for Edge SN: {}", request.getBase().getSn());
		var stopRequest = new LiveStreamStopRequest(
			request.getBase().getSn(),
			generateUUID(),
			request.getRequest().getVideoId()
		);
		edgeAdapterService.stopLiveStream(stopRequest)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void changeLens(EdgeChangeCameraLensRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("ChangeLens for Edge SN: {}", request.getBase().getSn());
		var lensRequest = protoJsonMapper.map(request);
		edgeAdapterService.changeLens(lensRequest)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void changeZoom(EdgeChangeCameraZoomRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("ChangeZoom for Edge SN: {}", request.getBase().getSn());
		var zoomRequest = protoJsonMapper.map(request);
		edgeAdapterService.changeZoom(zoomRequest)
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void registerAsset(EdgeRegisterAssetRequest request, StreamObserver<EdgeResponse> responseObserver) {
		super.registerAsset(request, responseObserver);
	}

	@Override
	public void deRegisterAsset(EdgeDeRegisterAssetRequest request, StreamObserver<EdgeResponse> responseObserver) {
		super.deRegisterAsset(request, responseObserver);
	}

	@Override
	public void startTask(EdgeStartTaskRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.info("StartTask for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.startTask(request.getTaskId(), request.getBase().getTid())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
	}

	@Override
	public void stopTask(EdgeStopTaskRequest request, StreamObserver<EdgeResponse> responseObserver) {
		log.warn("StopTask for Edge SN: {}", request.getBase().getSn());
		edgeAdapterService.stopTask(request.getTaskId())
				.thenAccept(result -> {
					responseObserver.onNext(toEdgeResponse(request.getBase(), result));
					responseObserver.onCompleted();
				})
				.exceptionally(throwable -> {
					responseObserver.onNext(toErrorResponse(request.getBase(), throwable));
					responseObserver.onCompleted();
					return null;
				});
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
