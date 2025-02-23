package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Page;
import com.hyunwns.demoweb.dto.post.PostSearch;
import com.hyunwns.demoweb.service.NoticeBoardService;
import com.hyunwns.demoweb.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MainController {

    private final SecurityUtils securityUtils;
    private final NoticeBoardService noticeBoardService;

    @Autowired
    public MainController(SecurityUtils securityUtils, NoticeBoardService noticeBoardService) {
        this.securityUtils = securityUtils;
        this.noticeBoardService = noticeBoardService;
    }

    @GetMapping("/main")
    public String mainPage(@ModelAttribute("postSearch") PostSearch postSearch, Model model) {

        securityUtils.addAttributeUserInfo(model);
        Page page = noticeBoardService.findPost(postSearch);

        model.addAttribute("page", page);

        return "main";
    }
}
