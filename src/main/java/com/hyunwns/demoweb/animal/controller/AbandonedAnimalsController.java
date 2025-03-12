package com.hyunwns.demoweb.animal.controller;

import com.hyunwns.demoweb.animal.service.WebCrawlingService;
import com.hyunwns.demoweb.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/animal")
public class AbandonedAnimalsController {

    private final SecurityUtils securityUtils;
    private final WebCrawlingService webCrawlingService;

    @GetMapping("/home")
    public String animal(@RequestParam(name = "keyword", required = false) String keyword, Model model) throws SQLException {
        securityUtils.addAttributeUserInfo(model);

        return "animal/home";
    }

    @GetMapping("/manage")
    public String manage(Model model) throws SQLException {
        securityUtils.addAttributeUserInfo(model);



        return "animal/manage";
    }
}
