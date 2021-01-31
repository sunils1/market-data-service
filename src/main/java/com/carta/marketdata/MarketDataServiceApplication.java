package com.carta.marketdata;

import com.carta.marketdata.helper.RestClient;
import com.carta.marketdata.model.MarketData;
import com.redislabs.redistimeseries.RedisTimeSeries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import redis.clients.jedis.JedisPoolConfig;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MarketDataServiceApplication {
    @Bean
    JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(new JedisPoolConfig());
    }

    @Bean
    RedisTemplate<String, MarketData> redisTemplate() {
        RedisTemplate<String, MarketData> redisTemplate = new RedisTemplate<>();
        JedisConnectionFactory connectionFactory = connectionFactory();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean
    RedisTimeSeries redisTimeSeries() {
        JedisConnectionFactory connectionFactory = connectionFactory();
        log.info("Registering RedisTimeSeries with {}, {}", connectionFactory.getHostName(), connectionFactory.getPort());
        return new RedisTimeSeries(connectionFactory.getHostName(), connectionFactory.getPort());
    }

    public static void main(String[] args) {
        SpringApplication.run(MarketDataServiceApplication.class, args);
    }
}
