package com.bit.srb.gateway.config;

import com.bit.common.exception.BusinessException;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.gateway.encoder.CustomPasswordEncoder;
import com.bit.srb.gateway.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

import java.util.Collections;

@Slf4j
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers( "/api/core/userInfo/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(customJwtFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    private WebFilter customJwtFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            // 放行公开接口和 OPTIONS 请求
            if (path.startsWith("/api/core/userInfo/login") ||
                    request.getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            // 直接从 "token" 头获取
            String token = exchange.getRequest().getHeaders().getFirst("token");
            log.info("token" + token);

            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                // 校验 Token
                if (!JwtUtils.checkToken(token)) {
                    throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
                }

                // 构建认证信息
                Long userId = JwtUtils.getUserId(token);
                String userName = JwtUtils.getUserName(token);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        userName,
                        Collections.emptyList() // 角色/权限留空或按需添加
                );

                // 注入安全上下文
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }

}
