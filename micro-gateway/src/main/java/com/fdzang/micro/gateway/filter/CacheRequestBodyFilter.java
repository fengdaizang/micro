package com.fdzang.micro.gateway.filter;

import com.fdzang.micro.common.constant.GatewayConstant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CacheRequestBodyFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Object requestBody = exchange.getAttributeOrDefault(GatewayConstant.CACHE_REQUEST_BODY, null);

        if (requestBody != null) {
            return chain.filter(exchange);
        }

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return bytes;
                }).defaultIfEmpty(new byte[0])
                .doOnNext(bytes -> exchange.getAttributes().put(GatewayConstant.CACHE_REQUEST_BODY, bytes))
                .then(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return GatewayConstant.Order.CACHE_BODY_ORDER;
    }

}
