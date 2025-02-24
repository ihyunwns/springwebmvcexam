package com.hyunwns.demoweb.chat.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyunwns.demoweb.chat.dto.ChatRoomDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ChatRoom {

    private final UUID roomId;

    private final String id;
    private final String nickname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime creationTime;

    @Setter
    private String roomTitle;

    /* 채팅방 참가자 */
    private final Map<String, WebSocketSession> joinUsers = new ConcurrentHashMap<>();

    /* 채팅방 대화 내용 */
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    public ChatRoom(String roomTitle, String id, String nickname) {
        this.roomId = UUID.randomUUID();
        this.roomTitle = roomTitle;
        this.id = id;
        this.nickname = nickname;

        this.creationTime = LocalDateTime.now();
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

    public ChatRoomDTO convertToDTO(){
        ChatRoomDTO roomDTO = new ChatRoomDTO();

        roomDTO.setId(id); roomDTO.setNickname(nickname);
        roomDTO.setTitle(roomTitle); roomDTO.setUuid(roomId);
        roomDTO.setCount(getCounts()); roomDTO.setCreatedAt(getCreationTime());

        return roomDTO;
    }


}
