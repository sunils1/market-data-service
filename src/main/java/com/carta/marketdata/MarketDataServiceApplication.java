package com.carta.marketdata;

import com.carta.marketdata.model.MarketData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import redis.clients.jedis.JedisPoolConfig;

@SpringBootApplication
@EnableScheduling
public class MarketDataServiceApplication {
    @Bean
    JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(new JedisPoolConfig());
    }

    @Bean
    RedisTemplate<String, MarketData> redisTemplate() {
        RedisTemplate<String, MarketData> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory());
        return redisTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(MarketDataServiceApplication.class, args);
    }
	// test
}
