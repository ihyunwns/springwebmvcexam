package com.hyunwns.demoweb.chat.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hyunwns.demoweb.chat.domain.ChatCode;
import com.hyunwns.demoweb.chat.domain.ChatMessage;
import com.hyunwns.demoweb.chat.domain.ChatRoom;
import com.hyunwns.demoweb.chat.service.ChatRoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

@Component
public class ChatHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);
    //private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final ChatRoomManager chatRoomManager;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatHandler(ChatRoomManager chatRoomManager, ObjectMapper objectMapper) {
        this.chatRoomManager = chatRoomManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String username = getUsername(session);

        ChatRoom room = chatRoomManager.getRoom(getUUID(session));

        // 같은 ID로 로그인한 유저가 웹소켓 세션을 유지 중일 때
        if (room.getJoinUsers().containsKey(username)) {
            // 기존 세션 종료하고 새로운 접속자로 업데이트, 그러나 이건 SpringSecurity의 세션 관리 규약에 따라 바뀌어야함 (현재는 기존 세션을 만료하는 방식이라 이 방법 채택)
            room.getJoinUsers().get(username).close(CloseStatus.SESSION_NOT_RELIABLE);
        }

        logger.info("{} connected, {} ", session.getId(), username);

        String resJson = objectMapper.writeValueAsString(new ChatMessage(username, "", ChatCode.ENTER.getCode(), null));

        session.sendMessage(new TextMessage(resJson));
        broadcastMessage(resJson, room);

        room.addUsers(username, session);

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String username = getUsername(session);
        ChatRoom room = chatRoomManager.getRoom(getUUID(session));

        // Map.class 로 변환될 때 내부적으로 Map<Object, Object> 로 처리되기 때문에 타입안정성 X
        Map<String, Object> messageMap = objectMapper.readValue(message.getPayload().toString(), new TypeReference<>() {});
        String textMessage = messageMap.get("message").toString();
        // JSON 객체를 지정한 타입으로 변환
        // TypeReference로 제네릭 타입도 변환 가능
        List<String> images = objectMapper.convertValue(messageMap.get("image"), new TypeReference<>() {});

        String jsonResponse = objectMapper.writeValueAsString(new ChatMessage(username, textMessage, ChatCode.MESSAGE.getCode(), images)) ;

        logger.info("{} received message: {}", session.getId(), message.getPayload());
        sendMessageOtherSession(jsonResponse, session, room);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        String username = getUsername(session);
        ChatRoom room = chatRoomManager.getRoom(getUUID(session));

        logger.error("WebSocket transport error 발생! 세션 ID: {}", session.getId(), exception);

        if (session.isOpen()) {
           session.close(CloseStatus.SERVER_ERROR); // 서버 에러 상태로 닫기
        }

        // 세션 목록에서 제거
        room.removeUsers(username);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

        String username = getUsername(session);
        ChatRoom room = chatRoomManager.getRoom(getUUID(session));

        if (closeStatus == CloseStatus.SERVER_ERROR) {
            return;
        }
        // Session을 먼저 제거한 후 broadcast 해야함
        room.removeUsers(username);

        String resJson = objectMapper.writeValueAsString(new ChatMessage(username, "", ChatCode.EXIT.getCode(), null));

        broadcastMessage(resJson, room);

        logger.info("{} closed: {}", session.getId(), username);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void broadcastMessage(String message, ChatRoom room) {
        for (WebSocketSession session : room.getJoinUsers().values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    logger.error("{} 메시지 전송 실패 {}", session.getId(), e.getMessage());
                }
            }
        }

        logger.info("send message to all users - message: {}, room: {}, count: {}", message, room.getRoomId(), room.getJoinUsers().size() );
    }

    private void sendMessageOtherSession(String jsonData, WebSocketSession self, ChatRoom room) {
        for (WebSocketSession session : room.getJoinUsers().values()) {
            if (session.isOpen()) {
                try {
                    if(session != self) {
                        session.sendMessage(new TextMessage(jsonData));
                    }
                } catch (IOException e) {
                    logger.error("{} 메시지 전송 실패 {}", session.getId(), e.getMessage());
                }
            }
        }

        logger.info("send message to other user - message: {}, room: {}, count: {}", jsonData, room.getRoomId(), room.getJoinUsers().size() );
    }

    private String getUsername(WebSocketSession session) {
        return (String) session.getAttributes().get("username");
    }

    private UUID getUUID(WebSocketSession session) {
        String uuid = (String) session.getAttributes().get("uuid");

        return UUID.fromString(uuid);
    }

}
