package com.carta.marketdata.collector;

import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.model.MarketDataIfc;
import com.carta.marketdata.repository.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public abstract class MarketDataCollector implements DataCollector<String, MarketDataIfc> {
    abstract public Set<String> getSymbols();

    private static long FIXED_RATE;

    final private Repository<MarketDataIfc> repository;

    protected MarketDataCollector(Repository<MarketDataIfc> repository) {
        this.repository = repository;
    }

    private void updateRepo(String symbol) {
        MarketDataIfc data = getData(symbol);
        log.info("Updating {}, with {}", symbol, data);
        repository.add(data);
    }

    @Override
    @Scheduled(fixedRateString ="${data.collector.rate}", initialDelay=1000)
    public void execute() {
        getSymbols().parallelStream()
                .forEach(this::updateRepo);
    }

    @Override
    abstract public MarketData getData(String key);
}
