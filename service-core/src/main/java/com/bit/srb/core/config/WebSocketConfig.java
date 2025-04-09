package com.bit.srb.core.config;

import com.bit.srb.core.handler.OrderWebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

public class WebSocketConfig implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new OrderWebSocketHandler(), "/ws/orders").setAllowedOrigins("*");
    }

}
