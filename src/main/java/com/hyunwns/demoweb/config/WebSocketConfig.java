package com.hyunwns.demoweb.config;

import com.hyunwns.demoweb.handler.ChatHandler;
import com.hyunwns.demoweb.handler.SocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatHandler(), "/chats")
                .addInterceptors(new SocketInterceptor())
                .setAllowedOrigins("*");
    }
}
