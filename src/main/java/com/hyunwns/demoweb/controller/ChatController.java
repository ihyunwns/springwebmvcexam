package com.hyunwns.demoweb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.chat.ChatRoom;
import com.hyunwns.demoweb.dto.ChatRoomDTO;
import com.hyunwns.demoweb.service.ChatRoomManager;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SecurityUtils securityUtils;
    private final ChatRoomManager chatRoomManager;
    private final MemberService memberService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_ROOMS_PER_USER = 1;

    @GetMapping("/waiting")
    public String waiting(Model model) {
        securityUtils.addAttributeUserInfo(model);

        List<ChatRoomDTO> roomList = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomManager.getRoomList().values()) {
            ChatRoomDTO roomDTO = new ChatRoomDTO();

            roomDTO.setCount(chatRoom.getCounts()); roomDTO.setUuid(chatRoom.getRoomId());
            roomDTO.setTitle(chatRoom.getRoomTitle()); roomDTO.setMember(chatRoom.getOwner());
            if (chatRoom.getOwner() == thisMember()) {
                roomDTO.setOwner(true);
            }

            roomList.add(roomDTO);
        }

        model.addAttribute("chatRoomDTO", new ChatRoomDTO());
        model.addAttribute("rooms", roomList);

        return "chat/waiting";
    }

    @PostMapping("/chat/create")
    public String create(@Valid @ModelAttribute("chatRoomDTO") ChatRoomDTO chatRoomDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "방 제목은 필수입니다.");
            return "redirect:/waiting";
        }

        if(MAX_ROOMS_PER_USER <= thisMember().getChatRooms().size()) {
            redirectAttributes.addFlashAttribute("errorMessage", "방 최대 개수를 초과했습니다. 최대 개수: " + MAX_ROOMS_PER_USER);
            return "redirect:/waiting";
        }

        chatRoomManager.createRoom(chatRoomDTO.getTitle(), thisMember());
        return "redirect:/waiting";
    }

    // TODO: 페이징 기능 구현
    @GetMapping("/chat/list")
    public ResponseEntity<?> list() {

        Map<UUID, ChatRoom> roomList = chatRoomManager.getRoomList();

        return ResponseEntity.ok().body(roomList);

    }

    @GetMapping("/chat/enter")
    public String enterRoom(@RequestParam("uuid") UUID roomId, Model model) {

        securityUtils.addAttributeUserInfo(model);

        ChatRoom room = chatRoomManager.getRoom(roomId);

        ChatRoomDTO roomDTO = new ChatRoomDTO();
        roomDTO.setTitle(room.getRoomTitle());
        roomDTO.setUuid(roomId);
        roomDTO.setMember(room.getOwner());

        model.addAttribute("chatRoomDTO", roomDTO);

        return "chat/chatRoom";
    }

    @PostMapping("/chat/delete")
    public ResponseEntity<?> deleteRoom(@RequestBody String uuid) throws JsonProcessingException {

        JsonNode jsonNode = objectMapper.readTree(uuid);
        String uuidString = jsonNode.get("uuid").asText();

        UUID roomId = UUID.fromString(uuidString);
        try {
            chatRoomManager.deleteRoom(roomId, thisMember());
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    private Member thisMember() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();

        return memberService.findMember(name);
    }

}
