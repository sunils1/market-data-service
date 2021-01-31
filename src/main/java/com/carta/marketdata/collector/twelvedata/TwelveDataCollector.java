package com.carta.marketdata.collector.twelvedata;

import com.carta.marketdata.collector.MarketDataCollector;
import com.carta.marketdata.helper.RestClient;
import com.carta.marketdata.helper.Util;
import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.model.MarketDataIfc;
import com.carta.marketdata.model.MarketDataSource;
import com.carta.marketdata.repository.Repository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "twelvedata.collector", name = "enable", havingValue = "true")
public class TwelveDataCollector extends MarketDataCollector {
    protected TwelveDataCollector(Repository<MarketDataIfc> repository,
                                  RestClient<TwelveDataResponse> client) {
        super(repository);
        this.client = client;
    }
    private static final String SYMBOL = "symbol";
    private static final String APIKEY = "apikey";
    private static final String INTERVAL = "interval";


    @Value("${twelvedata.api.key}")
    protected String apiKey;

    @Value("${twelvedata.base.url}")
    protected String baseUrl;

    @Value("${twelvedata.endpoint.quote}")
    protected String quote;

    @Value("${twelvedata.endpoint.timeseries}")
    protected String timeseries;

    @Override
    public Set<String> getSymbols() {
        return new HashSet<>(
                Arrays.asList( "AAPL", "TSLA", "IBM", "GS", "GME", "MSFT", "AMZN" )
        );
    }

    private final RestClient<TwelveDataResponse> client;



    @SneakyThrows
    @Override
    public MarketData getData(String key) {
        Map<String, Object> params = new HashMap<>();

        params.put(SYMBOL, key);
        params.put(APIKEY, this.apiKey);
        params.put(INTERVAL, "1min");

        TwelveDataResponse response = client.get(this.baseUrl, this.timeseries, params, TwelveDataResponse.class);
        TimeSeriesValue value = response.getValues()[0];
        String datetime = value.getDatetime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(response.getMeta().getExchange_timezone()));
        Date date = sdf.parse(datetime);

        // TODO:: move to adapter
        return new MarketData(
                response.getMeta().getSymbol(),
                Util.getUTCTime(date.getTime()),
                new BigDecimal(value.getClose()),
                response.getMeta().exchange,
                MarketDataSource.TWELVE_DATA,
                new BigDecimal(value.getVolume())
        );
    }
}

