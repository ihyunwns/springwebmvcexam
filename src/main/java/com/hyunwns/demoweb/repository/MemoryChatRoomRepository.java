package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.chat.ChatRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryChatRoomRepository implements ChatRoomRepository {

    private static final Map<UUID, ChatRoom> rooms = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MemoryChatRoomRepository.class);

    @Override
    public void saveRoom(ChatRoom room, Member member) {

        member.addChatRoom(room);
        rooms.put(room.getRoomId(), room);

        logger.info("saved room {} - {}", room.getRoomId(), this );

    }

    @Override
    public void deleteRoom(UUID roomId, Member member) {

        ChatRoom chatRoom = rooms.get(roomId);
        rooms.remove(roomId);
        member.removeChatRoom(chatRoom);

        logger.info("deleted room {} - {}", roomId, this );
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
