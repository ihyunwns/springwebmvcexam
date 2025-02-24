package com.hyunwns.demoweb.post.controller;

import com.hyunwns.demoweb.common.domain.Page;
import com.hyunwns.demoweb.post.dto.PostSearch;
import com.hyunwns.demoweb.post.service.NoticeBoardService;
import com.hyunwns.demoweb.common.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MainController {

    private final SecurityUtils securityUtils;
    private final NoticeBoardService noticeBoardService;

    private static Logger logger = LoggerFactory.getLogger(MainController.class);

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

        logger.info("current Page: {}, last page: {}", page.getCurrent_page(), page.getLast_page());
        logger.info("page List: {}", page.getPageList().toString());

        return "main";
    }
}
