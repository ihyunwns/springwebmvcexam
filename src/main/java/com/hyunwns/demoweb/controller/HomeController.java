package com.hyunwns.demoweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("loginRequest", new LoginForm());
        return "home";
    }
}
