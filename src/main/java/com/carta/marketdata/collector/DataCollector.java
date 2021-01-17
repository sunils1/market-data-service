package com.carta.marketdata.collector;


import com.carta.marketdata.model.MarketData;

public interface DataCollector<T> {
    void execute();

    MarketData getData(T key);
}
