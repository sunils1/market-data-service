package com.carta.marketdata.repository;

import com.carta.marketdata.helper.Util;
import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.model.MarketDataIfc;
import com.carta.marketdata.constants.MarketDataSource;
import com.redislabs.redistimeseries.*;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.carta.marketdata.constants.MarketDataConstants.*;

@Slf4j
@org.springframework.stereotype.Repository
public class RedisRepositoryImpl implements Repository<MarketDataIfc> {
    private static final String KEY_SEPARATOR = ":";
    private static final int HOURS_LIMIT = 25;

    RedisTimeSeries redisTimeSeries;

    public RedisRepositoryImpl(RedisTimeSeries redisTimeSeries) {
        this.redisTimeSeries = redisTimeSeries;
    }

    /**
     * Add entry to the time series
     *
     * @param marketData market data to be added
     */
    @Override
    public void add(MarketDataIfc marketData) {
        Map<String, String> labels = new HashMap<>();
        labels.put(EXCHANGE, marketData.getExchange());
        labels.put(SYMBOL, marketData.getSymbol());

        long timestamp = marketData.getDateTimeU().toInstant().toEpochMilli();

        try {
            this.redisTimeSeries.add(
                    getTimeSeriesKey(marketData.getMarketDataSource().name(), marketData.getSymbol(), PRICE),
                    timestamp,
                    marketData.getPrice().doubleValue(), labels);
            this.redisTimeSeries.add(
                    getTimeSeriesKey(marketData.getMarketDataSource().name(), marketData.getSymbol(), VOLUME),
                    timestamp,
                    marketData.getVolume().doubleValue(), labels);
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
    public MarketDataIfc get(String symbol) {
        try {
            Range[] tsData = this.redisTimeSeries.mget(true, filterSymbol(symbol), filterSource());
            TreeMap<Long, MarketDataIfc> marketData = transform(symbol, tsData);
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
    public List<MarketDataIfc> get(String symbol, int rowCount) {
        try {
            long to = System.currentTimeMillis();
            long from = to - TimeUnit.HOURS.toMillis(HOURS_LIMIT);
            String[] filters = new String[]{filterSymbol(symbol), filterSource()};

            Range[] tsData = redisTimeSeries.mrevrange(from, to, null, 0L, true, rowCount, filters);

            TreeMap<Long, MarketDataIfc> marketData = transform(symbol, tsData);
            return new ArrayList<>(marketData.values());
        } catch (JedisException e) {
            log.error("Exception while trying to getting range of data for {}, error: {}", symbol, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Helper function to create the ts key based on the source and symbol
     *
     * @param source    Source of the data
     * @param symbol    Symbol
     * @param type      type - price/volume etc
     * @return          ts_market_data:source:symbol
     */
    private String getTimeSeriesKey(String source, String symbol, String type) {
        return "ts_market_data" + KEY_SEPARATOR + source + KEY_SEPARATOR + symbol + KEY_SEPARATOR + type;
    }


    /**
     * Helper function to transform redis TS data to MarketData
     * @param symbol    symbol queried upon
     * @param tsData    data to transform
     * @return  TreeMap with the time -> Data mapping
     */
    private TreeMap<Long, MarketDataIfc> transform(String symbol, Range[] tsData) {
        TreeMap<Long, MarketDataIfc> marketData = new TreeMap<>();
        for (Range entry : tsData) {
            String key = entry.getKey();
            Value[] values = entry.getValues();
            Map<String, String> labels = entry.getLabels();
            for (Value value : values) {
                Long time = value.getTime();

                MarketDataIfc data = marketData.getOrDefault(time, new MarketData());
                if (key.contains(PRICE)) {
                    data.setExchange(labels.getOrDefault(EXCHANGE, "-"));
                    data.setSymbol(symbol);
                    data.setDateTimeU(Util.getUTCTime(value.getTime()));
                    data.setPrice(BigDecimal.valueOf(value.getValue()));
                    data.setMarketDataSource(MarketDataSource.valueOf(
                            labels.getOrDefault(SOURCE, MarketDataSource.UNKNOWN.name())));
                }

                if (key.contains(VOLUME)) {
                    data.setVolume(BigDecimal.valueOf(value.getValue()));
                }

                marketData.put(time, data);
            }
        }
        return marketData;
    }

    /**
     * Returns source filters we want to apply
     * @return  String with the formatted source filter
     */
    private String filterSource() {
        return SOURCE + "!=[" + MarketDataSource.TEST + "]";
    }

    /**
     * Adding symbol label filters
     * @param symbol    symbol to query on
     * @return          String with the symbol filter applied
     */
    private String filterSymbol(String symbol) {
        return SYMBOL + "=" + symbol;
    }

    /**
     * Helper function to get UTC time from long (ms)
     * @param value epoch time value in ms
     * @return      ZonedDateTime
     */
    private ZonedDateTime getUTCTime(Value value) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getTime()), ZoneId.of("UTC"));
    }
}
