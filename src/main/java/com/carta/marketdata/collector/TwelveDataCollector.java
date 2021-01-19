package com.carta.marketdata.collector;

import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.repository.Repository;

import java.util.Set;

public class TwelveDataCollector extends MarketDataCollector {
    protected TwelveDataCollector(Repository repository) {
        super(repository);
    }

    @Override
    public Set<String> getSymbols() {
        return null;
    }

    @Override
    public MarketData getData(String key) {
        return null;
    }
}
