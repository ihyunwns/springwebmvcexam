package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SecurityUtils securityUtils;

    @GetMapping("/chat")
    public String chat(Model model) {

        securityUtils.addAttributeUserInfo(model);

        return "chat/waiting";
    }
}
