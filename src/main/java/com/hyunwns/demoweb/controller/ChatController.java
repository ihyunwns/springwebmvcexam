package com.hyunwns.demoweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwns.demoweb.domain.chat.ChatRoom;
import com.hyunwns.demoweb.dto.ChatRoomDTO;
import com.hyunwns.demoweb.service.ChatRoomManager;
import com.hyunwns.demoweb.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SecurityUtils securityUtils;
    private final ChatRoomManager chatRoomManager;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @GetMapping("/waiting")
    public String waiting(Model model) {
        securityUtils.addAttributeUserInfo(model);

        Map<UUID, ChatRoom> roomList = chatRoomManager.getRoomList();

        model.addAttribute("chatRoomDTO", new ChatRoomDTO());
        model.addAttribute("rooms", roomList);

        return "chat/waiting";
    }

    @PostMapping("/chat/create")
    public String create(@Valid @ModelAttribute("chatRoomDTO") ChatRoomDTO chatRoomDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "방 제목은 필수입니다");
            return "redirect:/waiting";
        }

        chatRoomManager.createRoom(chatRoomDTO.getTitle());
        return "redirect:/waiting";
    }

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

        model.addAttribute("chatRoomDTO", roomDTO);

        return "chat/chatRoom";
    }

}
