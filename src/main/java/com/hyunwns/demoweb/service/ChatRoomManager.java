package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
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

    public void createRoom(String title, Member member) {
        ChatRoom chatRoom = new ChatRoom(title, member.getId(), member.getNickname());
        chatRoomRepository.saveRoom(chatRoom, member);
    }

    public void deleteRoom(UUID roomId, Member member) {

        ChatRoom room = chatRoomRepository.getRoom(roomId);

        if (room.getId().equals(member.getId())) {
            chatRoomRepository.deleteRoom(roomId, member);
        } else {
            throw new IllegalArgumentException("Room does not belong to the member");
        }
    }

    public Map<UUID, ChatRoom> getRoomList() {
        return chatRoomRepository.getRooms();
    }

    public ChatRoom getRoom(UUID id) {
        return chatRoomRepository.getRoom(id);
    }



}
