package com.hyunwns.demoweb.chat.repository;

import com.hyunwns.demoweb.common.domain.Member;
import com.hyunwns.demoweb.chat.domain.ChatRoom;

import java.util.Map;
import java.util.UUID;

public interface ChatRoomRepository {

    void saveRoom(ChatRoom room, Member member);

    void deleteRoom(UUID roomId, Member member);

    Map<UUID, ChatRoom> getRooms();

    ChatRoom getRoom(UUID roomId);

}
