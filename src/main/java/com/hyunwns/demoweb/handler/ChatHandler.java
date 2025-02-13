package com.hyunwns.demoweb.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ChatHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String username = getUsername(session);

        logger.info("{} connected, {} ", session.getId(), username);
        session.sendMessage(new TextMessage(username + "님이 접속하였습니다."));
        broadcastMessage(username + "님이 접속하였습니다.");

        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        String username = getUsername(session);

        logger.info("{} received message: {}", session.getId(), message.getPayload());

        sendMessageOtherSession(username + ": " + message.getPayload(), session);

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        logger.error("WebSocket transport error 발생! 세션 ID: {}", session.getId(), exception);

        if (session.isOpen()) {
           session.close(CloseStatus.SERVER_ERROR); // 서버 에러 상태로 닫기
        }

        // 세션 목록에서 제거
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

        String username = getUsername(session);

        if (closeStatus == CloseStatus.SERVER_ERROR) {
            return;
        }
        // Session을 먼저 제거한 후 broadcast 해야함
        sessions.remove(session);
        broadcastMessage(username + "님이 퇴장하였습니다.");

        logger.info("{} closed: {}", session.getId(), username);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    logger.error("{} 메시지 전송 실패 {}", session.getId(), e.getMessage());
                }
            }
        }
    }

    private void sendMessageOtherSession(String message, WebSocketSession self) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    if(session != self) {
                        session.sendMessage(new TextMessage(message));
                    }
                } catch (IOException e) {
                    logger.error("{} 메시지 전송 실패 {}", session.getId(), e.getMessage());
                }
            }
        }
    }

    private String getUsername(WebSocketSession session) {
        return (String) session.getAttributes().get("username");
    }

}
