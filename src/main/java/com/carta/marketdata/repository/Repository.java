package com.carta.marketdata.repository;

import com.carta.marketdata.model.MarketData;

import java.util.Map;

public interface Repository {
    void save(MarketData marketData);

    Map<String, MarketData> findAll();

    MarketData findById(String symbol);

    void update(MarketData marketData);

    void delete(String symbol);
}
