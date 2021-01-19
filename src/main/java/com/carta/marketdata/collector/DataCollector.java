package com.carta.marketdata.collector;


public interface DataCollector<T, R> {
    void execute();

    R getData(T key);
}
