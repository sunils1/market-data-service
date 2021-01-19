package com.carta.marketdata.repository;

import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.model.SourceType;
import com.redislabs.redistimeseries.*;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@org.springframework.stereotype.Repository
public class TimeSeriesRepositoryImpl implements Repository {
    private static final String EXCHANGE = "exchange";
    private static final String SOURCE = "source";
    private static final String SYMBOL = "symbol";
    private static final String KEY_SEPARATOR = ":";
    private static final String PRICE = "price";
    private static final String VOLUME = "volume";
    private static final int HOURS_LIMIT = 25;

    RedisTimeSeries redisTimeSeries;

    public TimeSeriesRepositoryImpl(RedisTimeSeries redisTimeSeries) {
        this.redisTimeSeries = redisTimeSeries;
    }

    private String getTimeSeriesKey(String source, String symbol, String type) {
        return "ts_market_data" + KEY_SEPARATOR + source + KEY_SEPARATOR + symbol + KEY_SEPARATOR + type;
    }

    @Override
    public void add(MarketData marketData) {
        Map<String, String> labels = new HashMap<>();
        labels.put(EXCHANGE, marketData.getExchange());
        labels.put(SYMBOL, marketData.getSymbol());

        long timestamp = marketData.getDateTimeU().toInstant().toEpochMilli();

        try {
            this.redisTimeSeries.add(
                    getTimeSeriesKey(marketData.getSource().name(), marketData.getSymbol(), PRICE),
                    timestamp,
                    marketData.getPrice(), labels);
            this.redisTimeSeries.add(
                    getTimeSeriesKey(marketData.getSource().name(), marketData.getSymbol(), VOLUME),
                    timestamp,
                    marketData.getVolume(), labels);
            log.info("Added to ts with timestamp: {} ({}), price: {}, volume: {}, labels : {}",
                    timestamp, marketData.getDateTimeU(), marketData.getPrice(),
                    marketData.getVolume(), labels);
        } catch (JedisException e) {
            log.error("Exception while trying to add TS for {}, error : {}", marketData, e.getMessage());
        }

    }

    /**
     * Get latest date for single
     *
     * @param symbol symbol to search for
     * @return market data information
     */
    @Override
    public MarketData get(String symbol) {
        try {
            Range[] tsData = this.redisTimeSeries.mget(true, filterSymbol(symbol), filterSource());
            TreeMap<Long, MarketData> marketData = getMarketData(symbol, tsData);
            return Optional.ofNullable(marketData.lastEntry()).isPresent() ? marketData.lastEntry().getValue() : null;
        } catch (JedisConnectionException e) {
            log.error("Exception while trying to get data for {}, error : {}", symbol, e.getMessage());
            return null;
        }
    }

    /**
     * Get timeseries marketdata
     *
     * @param symbol   symbol for query
     * @param rowCount row count for return
     * @return List of MarketData
     */
    @Override
    public List<MarketData> get(String symbol, int rowCount) {
        try {
            long to = System.currentTimeMillis();
            long from = to - TimeUnit.HOURS.toMillis(HOURS_LIMIT);
            String[] filters = new String[]{filterSymbol(symbol), filterSource()};

            Range[] tsData = redisTimeSeries.mrevrange(from, to, null, 0L, true, rowCount, filters);

            TreeMap<Long, MarketData> marketData = getMarketData(symbol, tsData);
            return new ArrayList<>(marketData.values());
        } catch (JedisException e) {
            log.error("Exception while trying to getting range of data for {}, error: {}", symbol, e.getMessage());
            return new ArrayList<>();
        }
    }

    private TreeMap<Long, MarketData> getMarketData(String symbol, Range[] tsData) {
        TreeMap<Long, MarketData> marketData = new TreeMap<>();
        for (Range entry : tsData) {
            String key = entry.getKey();
            Value[] values = entry.getValues();
            Map<String, String> labels = entry.getLabels();
            for (Value value : values) {
                Long time = value.getTime();

                MarketData data = marketData.getOrDefault(time, new MarketData());
                if (key.contains(PRICE)) {
                    data.setExchange(labels.getOrDefault(EXCHANGE, "-"));
                    data.setSymbol(symbol);
                    data.setDateTimeU(getUTCTime(value));
                    data.setPrice(value.getValue());
                }

                if (key.contains(VOLUME)) {
                    data.setVolume(value.getValue());
                }

                marketData.put(time, data);
            }
        }
        return marketData;
    }


    private String filterSource() {
        return SOURCE + "!=[" + SourceType.TEST + ", " + SourceType.MANUAL + "]";
    }

    private String filterSymbol(String symbol) {
        return SYMBOL + "=" + symbol;
    }

    private ZonedDateTime getUTCTime(Value value) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getTime()), ZoneId.of("UTC"));
    }
}
