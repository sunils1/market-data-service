package com.carta.marketdata.repository;

import com.carta.marketdata.model.MarketData;

import java.util.List;
import java.util.Map;

public interface Repository {
    /**
     * Get the latest for the symbol
     * @param key   key to search on
     * @return      data returned
     */
    MarketData get(String key);

    /**
     * get time series style data of list of market data
     *
     * @param key       key to search on
     * @param rowCount  row count to return
     * @return          list of data returned
     */
    List<MarketData> get(String key, int rowCount);

    void add(MarketData marketData);
}
