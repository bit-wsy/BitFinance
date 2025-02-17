package com.bit.srb.gateway.filter;

import com.bit.srb.gateway.util.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtHeaderGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从请求头获取 Token
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (token == null) {
            return chain.filter(exchange);
        }

        try {
            // 解析 Token 获取用户信息
            Long userId = JwtUtils.getUserId(token);
            String userName = JwtUtils.getUserName(token);

            // 添加新请求头
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-User-Name", userName)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            // 处理无效 Token
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1; // 设置执行顺序（值越小优先级越高）
    }
}
