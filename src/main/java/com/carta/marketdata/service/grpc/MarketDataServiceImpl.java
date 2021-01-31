package com.carta.marketdata.service.grpc;


import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.repository.Repository;
import com.carta.marketdata.service.lib.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@GrpcService
public class MarketDataServiceImpl extends MarketDataServiceGrpc.MarketDataServiceImplBase {
    private static final DateTimeFormatter LONG_DATETIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
    private static final DateTimeFormatter ISO_DATETIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    private static final String HEARTBEAT_MESSAGE = "[%s] Heart is still beating.";
    private static final int ROW_COUNT_DEFAULT = 20;

    final private Repository<MarketData> repository;

    public MarketDataServiceImpl(Repository<MarketData> repository) {
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
    public void spot(SpotRequest request, StreamObserver<MarketDataReply> responseObserver) {
        String symbol = request.getSymbol();

        MarketData data = repository.get(symbol);
        MarketDataReply reply;
        if (Optional.ofNullable(data).isPresent()) {
            reply = MarketDataReply.newBuilder()
                    .setSymbol(data.getSymbol())
                    .setPrice(data.getPrice().doubleValue())
                    .setDateTime(ISO_DATETIME_FORMAT.format(data.getDateTimeU()))
                    .setExchange(data.getExchange())
                    .setVolume(data.getVolume().doubleValue())
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

    public MarketDataReply getReply(MarketData data) {
        return MarketDataReply.newBuilder()
                .setSymbol(data.getSymbol())
                .setPrice(data.getPrice().doubleValue())
                .setDateTime(ISO_DATETIME_FORMAT.format(data.getDateTimeU()))
                .setExchange(data.getExchange())
                .setVolume(data.getVolume().doubleValue())
                .build();
    }

    @Override
    public void timeseries(TimeSeriesRequest request, StreamObserver<TimeSeriesMarketDataReply> responseObserver) {
        String symbol = request.getSymbol();
        int rowCount = request.getRowCount();

        if (rowCount == 0) {
            rowCount = ROW_COUNT_DEFAULT;
        }

        List<MarketData> all = repository.get(symbol, rowCount);
        TimeSeriesMarketDataReply.Builder builder = TimeSeriesMarketDataReply.newBuilder();

        if (Optional.ofNullable(all).isPresent()) {
            List<MarketDataReply> replies = all.stream()
                    .map(this::getReply)
                    .collect(Collectors.toList());
            builder.addAllReply(replies);
        } else {
            builder.addReply(MarketDataReply.newBuilder()
                    .setSymbol(symbol)
                    .setComment("Market data is not available.")
                    .build());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}