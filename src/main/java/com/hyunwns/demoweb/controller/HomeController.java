package com.hyunwns.demoweb.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {
        String username = (String) request.getAttribute("username");
        System.out.println("aaaa!!! " + username);
        return "home";
    }
}
