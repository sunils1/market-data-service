package com.carta.marketdata.collector;

import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;

@Slf4j
public abstract class AbstractDataCollector implements DataCollector<String, MarketData> {
    abstract public Set<String> getSymbols();

    private static final long FIXED_RATE = 300000; // 5 minutes
    final private Repository repository;

    protected AbstractDataCollector(Repository repository) {
        this.repository = repository;
    }

    private void updateRepo(String symbol) {
        MarketData data = getData(symbol);
        log.info("Updating {}, with {}", symbol, data);
        repository.add(data);
    }
    @Override
    @Scheduled(fixedRate = FIXED_RATE)
    public void execute() {
        getSymbols().parallelStream()
                .forEach(this::updateRepo);
    }

    @Override
    abstract public MarketData getData(String key);
}
