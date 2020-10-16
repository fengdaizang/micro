package com.fdzang.micro.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.fdzang.micro.common.constant.GatewayConstant;
import com.fdzang.micro.common.entity.ApiResult;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @author tanghu
 * @Date: 2019/11/7 8:58
 */
@Component
public class ModifyResponseFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = exchange.getAttribute(GatewayConstant.REQUEST_TRACE_ID);
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        //释放掉内存
                        DataBufferUtils.release(dataBuffer);

                        //获取原始响应
                        String originalbody = new String(content, Charset.forName("UTF-8"));
                        ApiResult apiResult = JSON.parseObject(originalbody, ApiResult.class);

                        //修改响应结果
                        apiResult.setMsg("ModifyResponse:" + apiResult.getMsg());

                        //回填响应数据
                        return bufferFactory.wrap(JSON.toJSONString(apiResult).getBytes());
                    }));
                }

                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return GatewayConstant.Order.MODIFY_RESPONSE_ORDER;
    }
}
