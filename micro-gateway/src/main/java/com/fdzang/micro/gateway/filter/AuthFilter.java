package com.fdzang.micro.gateway.filter;

import com.fdzang.micro.common.constant.GatewayConstant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 权限校验
 * @author tanghu
 * @Date: 2019/10/22 18:00
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //do nothing
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return GatewayConstant.Order.AUTH_ORDER;
    }
}