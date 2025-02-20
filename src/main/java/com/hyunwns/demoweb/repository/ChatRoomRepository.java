package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.chat.ChatRoom;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;

public interface ChatRoomRepository {

    ChatRoom saveRoom(ChatRoom room);

    void deleteRoom(UUID roomId);

    Map<UUID, ChatRoom> getRooms();

    ChatRoom getRoom(UUID roomId);

}
