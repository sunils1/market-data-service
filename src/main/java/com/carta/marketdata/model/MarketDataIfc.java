package com.carta.marketdata.model;

import com.carta.marketdata.constants.MarketDataSource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public interface MarketDataIfc {
    String getSymbol();
    ZonedDateTime getDateTimeU();
    BigDecimal getPrice();
    String getExchange();
    MarketDataSource getMarketDataSource();
    BigDecimal getVolume();

    void setSymbol(String symbol);
    void setDateTimeU(ZonedDateTime dateTimeU);
    void setPrice(BigDecimal price);
    void setExchange(String exchange);
    void setMarketDataSource(MarketDataSource source);
    void setVolume(BigDecimal volume);
}
