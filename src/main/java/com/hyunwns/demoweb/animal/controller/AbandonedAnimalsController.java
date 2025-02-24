package com.hyunwns.demoweb.animal.controller;

import com.hyunwns.demoweb.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AbandonedAnimalsController {

    private final SecurityUtils securityUtils;

    @GetMapping("/animal")
    public String animal(Model model) {
        securityUtils.addAttributeUserInfo(model);

        return "animal/animals";
    }

}
