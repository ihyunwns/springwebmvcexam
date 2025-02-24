package com.hyunwns.demoweb.chat.handler;

import jakarta.security.auth.message.AuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class SocketInterceptor implements HandshakeInterceptor {

    Logger logger = LoggerFactory.getLogger(SocketInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (request instanceof ServletServerHttpRequest servletRequest) {
            String uuid = servletRequest.getServletRequest().getParameter("uuid");

            System.out.println("Received UUID: " + uuid);

            if (uuid != null && !uuid.isEmpty()) {
                attributes.put("uuid", uuid);
            }

            if (auth != null && auth.isAuthenticated()) {
               attributes.put("username", auth.getName());
            } else {
                logger.error("인증되지 않은 세션입니다.");

                throw new AuthException("Not authenticated");
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
