package com.fdzang.micro.gateway.filter;

import java.util.List;

import com.fdzang.micro.common.constant.GatewayConstant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.base.Splitter;

import reactor.core.publisher.Mono;

/**
 * @author tanghu
 * @Date: 2019/11/6 15:39
 */
@Component
public class ModifyRequestFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder requestBuilder = request.mutate();

        /***** 解析请求，根据URL获取服务名及对应方法 *****/
        // url = /{version}/{service}/{path} = /v1/message/message/sendMsg
        String url = request.getURI().getPath();

        List<String> strs = Splitter.on("/").omitEmptyStrings()
                .trimResults().limit(3)
                .splitToList(url);

        // serviceName = pdbc.blockchain.micro.{service}.{version} = pdbc.blockchain.micro.message.v1
        String serviceName = GatewayConstant.SEREIVCE_NAME_PREFIX + strs.get(1) + "." + strs.get(0);
        String path = strs.get(2);

        /***** 重写路由规则 通过服务名负载请求 *****/
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        Route newRoute = Route.async()
                .asyncPredicate(route.getPredicate())
                .filters(route.getFilters())
                .id(route.getId())
                .order(route.getOrder())
                .uri(GatewayConstant.URI.LOAD_BALANCE+serviceName).build();

        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR,newRoute);

        ServerHttpRequest newRequest = requestBuilder.path(path)
                .header("Micro-Service",serviceName)
                .header("Micro-Endpoint",path)
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    @Override
    public int getOrder() {
        return GatewayConstant.Order.MODIFY_REQUEST_ORDER;
    }
}
