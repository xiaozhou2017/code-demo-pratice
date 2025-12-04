package com.example.gatewaydemo;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component
public class RemoveCookieGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求路径，可根据路径决定是否执行操作
        String path = exchange.getRequest().getURI().getPath();
        // 示例：仅当路径包含特定字符串时才移除Cookie
        // if (path.contains("/nacos-demo-service/")) {
        ServerHttpRequest newRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.remove("Cookie"))
                .build();
        return chain.filter(exchange.mutate().request(newRequest).build());
        // }
        // return chain.filter(exchange);
    }
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // 设置高优先级
    }
}
