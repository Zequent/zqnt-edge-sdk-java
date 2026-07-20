package com.zqnt.sdk.edge.livedata.application.impl;

import com.zqnt.utils.common.proto.DetectionBatch;
import com.zqnt.utils.livedata.proto.LiveDataResponse;
import com.zqnt.utils.livedata.proto.LiveDataServiceGrpc;
import com.zqnt.utils.livedata.proto.ProduceNotificationRequest;
import com.zqnt.utils.livedata.proto.ProduceTelemetryRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LiveDataServiceImplTest {

	private Server server;
	private ManagedChannel channel;
	private LiveDataServiceImpl service;

	@AfterEach
	void tearDown() throws InterruptedException {
		if (service != null) {
			service.shutdown();
		}
		if (channel != null) {
			channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		}
		if (server != null) {
			server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		}
	}

	@Test
	void reconnectsAllProducerStreamsAfterServerRestart() throws Exception {
		CountingLiveDataServer initialService = new CountingLiveDataServer();
		server = startServer(0, initialService);
		int port = server.getPort();
		channel = ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
		service = new LiveDataServiceImpl(null, null, null, LiveDataServiceGrpc.newStub(channel),
				Executors.newSingleThreadScheduledExecutor(), 1, 2);

		produceOneOfEach();
		await(Duration.ofSeconds(5), initialService::receivedOneOfEach);

		server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		/* Keep the endpoint unavailable long enough to exercise at least one failed reconnect. */
		Thread.sleep(1_500);

		CountingLiveDataServer restartedService = new CountingLiveDataServer();
		server = startServer(port, restartedService);
		await(Duration.ofSeconds(8), restartedService::openedAllStreams);

		produceOneOfEach();
		await(Duration.ofSeconds(5), restartedService::receivedOneOfEach);
	}

	private void produceOneOfEach() {
		service.produceTelemetry("device-1", ProduceTelemetryRequest.getDefaultInstance());
		service.produceDetection("device-1", DetectionBatch.getDefaultInstance());
		service.produceNotification("device-1", ProduceNotificationRequest.getDefaultInstance());
	}

	private Server startServer(int port, CountingLiveDataServer implementation) throws IOException {
		return ServerBuilder.forPort(port).addService(implementation).build().start();
	}

	private void await(Duration timeout, BooleanSupplier condition) throws InterruptedException {
		long deadline = System.nanoTime() + timeout.toNanos();
		while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
			Thread.sleep(25);
		}
		assertTrue(condition.getAsBoolean(), "condition was not met within " + timeout);
	}

	private static final class CountingLiveDataServer extends LiveDataServiceGrpc.LiveDataServiceImplBase {
		private final AtomicInteger telemetryStreams = new AtomicInteger();
		private final AtomicInteger detectionStreams = new AtomicInteger();
		private final AtomicInteger notificationStreams = new AtomicInteger();
		private final AtomicInteger telemetryMessages = new AtomicInteger();
		private final AtomicInteger detectionMessages = new AtomicInteger();
		private final AtomicInteger notificationMessages = new AtomicInteger();

		@Override
		public StreamObserver<ProduceTelemetryRequest> produceTelemetry(StreamObserver<LiveDataResponse> responseObserver) {
			telemetryStreams.incrementAndGet();
			return countingObserver(telemetryMessages);
		}

		@Override
		public StreamObserver<DetectionBatch> produceDetection(StreamObserver<LiveDataResponse> responseObserver) {
			detectionStreams.incrementAndGet();
			return countingObserver(detectionMessages);
		}

		@Override
		public StreamObserver<ProduceNotificationRequest> produceNotification(StreamObserver<LiveDataResponse> responseObserver) {
			notificationStreams.incrementAndGet();
			return countingObserver(notificationMessages);
		}

		private boolean openedAllStreams() {
			return telemetryStreams.get() > 0 && detectionStreams.get() > 0 && notificationStreams.get() > 0;
		}

		private boolean receivedOneOfEach() {
			return telemetryMessages.get() > 0 && detectionMessages.get() > 0 && notificationMessages.get() > 0;
		}

		private static <T> StreamObserver<T> countingObserver(AtomicInteger counter) {
			return new StreamObserver<>() {
				@Override
				public void onNext(T value) {
					counter.incrementAndGet();
				}

				@Override
				public void onError(Throwable throwable) {
				}

				@Override
				public void onCompleted() {
				}
			};
		}
	}
}
