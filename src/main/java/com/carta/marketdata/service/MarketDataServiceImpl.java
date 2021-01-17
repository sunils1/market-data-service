package com.carta.marketdata.service;


import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.repository.Repository;
import com.carta.marketdata.service.lib.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;


@GrpcService
public class MarketDataServiceImpl extends MarketDataServiceGrpc.MarketDataServiceImplBase {
    private static final DateTimeFormatter LONG_DATETIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
    private static final DateTimeFormatter ISO_DATETIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    private static final String HEARTBEAT_MESSAGE = "[%s] Heart is still beating.";

    final private Repository repository;

    public MarketDataServiceImpl(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatReply> responseObserver) {
        HeartbeatReply reply = HeartbeatReply.newBuilder()
                .setMessage(String.format(HEARTBEAT_MESSAGE,
                        LONG_DATETIME_FORMATTER.format(ZonedDateTime.now(ZoneId.of("UTC")))))
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void marketdata(MarketDataRequest request, StreamObserver<MarketDataReply> responseObserver) {
        String symbol = request.getSymbol();

        MarketData data = repository.findById(symbol);
        MarketDataReply reply;
        if (Optional.ofNullable(data).isPresent()) {
            reply = MarketDataReply.newBuilder()
                    .setSymbol(data.getSymbol())
                    .setPrice(data.getPrice().doubleValue())
                    .setDateTime(ISO_DATETIME_FORMAT.format(data.getDateTimeU()))
                    .setExchange(data.getExchange())
                    .build();
        } else {
            reply = MarketDataReply.newBuilder()
                    .setSymbol(symbol)
                    .setComment("Market data is not available.")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}