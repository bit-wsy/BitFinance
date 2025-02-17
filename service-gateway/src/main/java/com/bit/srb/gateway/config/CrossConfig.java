package com.bit.srb.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


@Configuration
public class CrossConfig {

        @Bean
        public CorsWebFilter corsFilter() {
            CorsConfiguration config = new CorsConfiguration();

            // 允许的域名（开发环境可放宽，生产环境应指定具体域名）
            config.addAllowedOriginPattern("*"); // 支持 localhost 所有端口

            // 允许的请求头
            config.addAllowedHeader("*");
            // 允许的请求方法
            config.addAllowedMethod("*");
            // 允许携带凭证（如 token）
            config.setAllowCredentials(true);
            // 暴露自定义响应头（前端可读取的头部）
            config.addExposedHeader("token");
            config.addExposedHeader("Set-Cookie"); // 如果需要 Cookie

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);

            return new CorsWebFilter(source);
        }



}
