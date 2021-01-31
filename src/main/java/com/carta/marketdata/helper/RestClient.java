package com.carta.marketdata.helper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Slf4j
@Component
public class RestClient<T> {
    Client client = ClientBuilder.newClient();

    @SneakyThrows
    public T get(final String base, final String endpoint, Map<String, Object> queryParams, Class<T> clazz) {
        WebTarget target = client.target(base).path(endpoint);
        for(String key : queryParams.keySet())
            target = target.queryParam(key, queryParams.get(key));

        return target.request(MediaType.APPLICATION_JSON).get(clazz);
    }
}
