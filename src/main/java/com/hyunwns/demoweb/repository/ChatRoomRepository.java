package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.chat.ChatRoom;

import java.util.Map;
import java.util.UUID;

public interface ChatRoomRepository {

    void saveRoom(ChatRoom room, Member member);

    void deleteRoom(UUID roomId, Member member);

    Map<UUID, ChatRoom> getRooms();

    ChatRoom getRoom(UUID roomId);

}
