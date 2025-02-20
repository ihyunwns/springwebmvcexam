package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.chat.ChatRoom;
import com.hyunwns.demoweb.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomManager {

    private final ChatRoomRepository chatRoomRepository;

    public void createRoom(String title) {

        ChatRoom chatRoom = new ChatRoom(title);
        chatRoomRepository.saveRoom(chatRoom);
    }

    public Map<UUID, ChatRoom> getRoomList() {
        return chatRoomRepository.getRooms();
    }

    public ChatRoom getRoom(UUID id) {
        return chatRoomRepository.getRoom(id);
    }


}
