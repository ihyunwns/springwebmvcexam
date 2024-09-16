package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.repository.Page;
import com.hyunwns.demoweb.repository.PostSearch;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.service.NoticeBoardService;
import com.hyunwns.demoweb.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

        List<Post> findPost = page.getPosts();
        System.out.println(page.getCurrent_page());
        System.out.println(page.getLast_page());

        model.addAttribute("posts", findPost);

        return "main";
    }
}
