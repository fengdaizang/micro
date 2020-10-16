package com.fdzang.micro.gateway.filter;

import com.fdzang.micro.common.constant.GatewayConstant;
import io.netty.buffer.ByteBufAllocator;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PutRequestBodyFilter implements GlobalFilter,Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                Object requestBody = exchange.getAttributeOrDefault(GatewayConstant.CACHE_REQUEST_BODY, null);

                if(requestBody!=null) {
                    byte[] bodyBytes = (byte[])requestBody;
                    DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
                    DataBuffer dataBuffer = dataBufferFactory.allocateBuffer();
                    dataBuffer.write(bodyBytes);

                    return Flux.just(dataBuffer);
                }else {
                    return super.getBody();
                }
            }
        };

        return chain.filter(exchange.mutate().request(decorator).build());

    }

    @Override
    public int getOrder() {
        return GatewayConstant.Order.PUT_BODY_ORDER;
    }

}
