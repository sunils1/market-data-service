package com.carta.marketdata.service;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import com.carta.marketdata.service.lib.MarketDataServiceGrpc;
import com.carta.marketdata.service.lib.HeartbeatRequest;
import com.carta.marketdata.service.lib.HeartbeatReply;



@GrpcService
public class MarketDataServiceImpl extends MarketDataServiceGrpc.MarketDataServiceImplBase {
    private static final DateTimeFormatter LONG_DATETIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
    private static final String HEARTBEAT_MESSAGE = "[%s] Heart is still beating.";

    @Override
    public void heartbeat( HeartbeatRequest request, StreamObserver<HeartbeatReply> responseObserver) {
        HeartbeatReply reply = HeartbeatReply.newBuilder()
                .setMessage(String.format(HEARTBEAT_MESSAGE,
                        LONG_DATETIME_FORMATTER.format(ZonedDateTime.now(ZoneId.of("UTC")))))
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }


}