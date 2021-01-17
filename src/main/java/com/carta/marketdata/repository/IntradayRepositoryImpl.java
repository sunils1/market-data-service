package com.carta.marketdata.repository;

import com.carta.marketdata.model.MarketData;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

@org.springframework.stereotype.Repository
public class IntradayRepositoryImpl implements Repository {
    private static final String REPO_KEY = "INTRA_DAY_PRICE";

    private RedisTemplate<String, MarketData> redisTemplate;
    final private HashOperations<String, String, MarketData> hashOperations;

    public IntradayRepositoryImpl(RedisTemplate<String, MarketData> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(MarketData marketData) {
        hashOperations.put(REPO_KEY, marketData.getSymbol(), marketData);
    }

    @Override
    public Map<String, MarketData> findAll() {
        return hashOperations.entries(REPO_KEY);
    }

    @Override
    public MarketData findById(String id) {
        return hashOperations.get(REPO_KEY, id);
    }

    @Override
    public void update(MarketData marketData) {
        save(marketData);
    }

    @Override
    public void delete(String id) {
        hashOperations.delete(REPO_KEY, id);
    }
}
