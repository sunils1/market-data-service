package com.carta.marketdata.collector;

import com.carta.marketdata.model.MarketDataBase;
import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.repository.Repository;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public abstract class MarketDataCollector implements DataCollector<String, MarketData> {
    abstract public Set<String> getSymbols();

    final private Repository<MarketData> repository;

    protected MarketDataCollector(Repository<MarketData> repository) {
        this.repository = repository;
    }

    private void updateRepo(String symbol) {
        MarketData data = getData(symbol);
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
    abstract public MarketDataBase getData(String key);
}
