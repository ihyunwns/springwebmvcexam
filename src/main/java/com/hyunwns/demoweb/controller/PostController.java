package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class PostController {

    private final SecurityUtils securityUtils;

    @Autowired
    public PostController(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @GetMapping("postForm")
    public String postForm(Model model) {
        securityUtils.addAttributeUserInfo(model);

        return "board/postForm";
    }
}
