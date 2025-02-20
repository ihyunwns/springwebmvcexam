package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.chat.ChatRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryChatRoomRepository implements ChatRoomRepository {

    private static Map<UUID, ChatRoom> rooms = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MemoryChatRoomRepository.class);

    @Override
    public ChatRoom saveRoom(ChatRoom room) {
        rooms.put(room.getRoomId(), room);

        logger.info("saved room {} - {}", room.getRoomId(), this );

        return room;
    }

    @Override
    public void deleteRoom(UUID roomId) {
        rooms.remove(roomId);
    }

    @Override
    public Map<UUID, ChatRoom> getRooms() {
        return rooms;
    }

    @Override
    public ChatRoom getRoom(UUID roomId) {
        return rooms.get(roomId);
    }
}
