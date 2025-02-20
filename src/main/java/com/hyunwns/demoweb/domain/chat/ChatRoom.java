package com.hyunwns.demoweb.domain.chat;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ChatRoom {

    private final UUID roomId;

    @Setter
    private String roomTitle;

    /* 채팅방 참가자 */
    private final Map<String, WebSocketSession> joinUsers = new ConcurrentHashMap<>();

    /* 채팅방 대화 내용 */
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    public ChatRoom(String roomTitle) {
        this.roomId = UUID.randomUUID();
        this.roomTitle = roomTitle;
    }

    public void addUsers(String username, WebSocketSession session) {
        joinUsers.put(username, session);
    }

    public void removeUsers(String username) {
        joinUsers.remove(username);
    }

    public int getCounts() {
        return joinUsers.size();
    }

}
