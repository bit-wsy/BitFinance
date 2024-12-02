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
//            config.setAllowCredentials(true); //是否允许携带cookie
//            config.addAllowedOrigin("*"); //可接受的域，是一个具体域名或者*（代表任意域名）
//            config.addAllowedHeader("*"); //允许携带的头
//            config.addAllowedMethod("*"); //允许访问的方式

            // 这里仅为了说明问题，配置为放行所有域名，生产环境请对此进行修改
            config.addAllowedOriginPattern("*");
            // 放行的请求头
            config.addAllowedHeader("*");
            // 放行的请求方式，主要有：GET, POST, PUT, DELETE, OPTIONS
            config.addAllowedMethod("*");
            // 暴露头部信息
//            config.addExposedHeader("*");
            // 是否发送cookie
            config.setAllowCredentials(true);


            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);

            return new CorsWebFilter(source);
        }



}
