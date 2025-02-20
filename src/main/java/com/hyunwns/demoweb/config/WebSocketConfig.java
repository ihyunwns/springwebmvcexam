package com.hyunwns.demoweb.config;

import com.hyunwns.demoweb.handler.ChatHandler;
import com.hyunwns.demoweb.handler.SocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Component
// @Configuration 으로 설정 시 자동 주입이 사용되지 않음 따라서 Component로 일반적인 스프링 빈으로 등록
// 혹은 @Configuration 으로 하되 필요한 빈들을 수동으로 등록
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    private final ChatHandler chatHandler;

    @Autowired
    public WebSocketConfig(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/chats")
                .addInterceptors(new SocketInterceptor())
                .setAllowedOrigins("*");
    }


}
