package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.dto.CustomUserDetails;
import com.hyunwns.demoweb.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private final MemberService memberService;

    @Autowired
    public HomeController(MemberService memberService) {
        this.memberService = memberService;
    }

    @RequestMapping("/")
    public String home(HttpServletRequest req, Model model) {
        HttpSession session = req.getSession();
        Object SPRING_SECURITY_CONTEXT = session.getAttribute("SPRING_SECURITY_CONTEXT");


        if(SPRING_SECURITY_CONTEXT != null) {
            SecurityContext securityContext = (SecurityContext) SPRING_SECURITY_CONTEXT;
            Authentication authentication = securityContext.getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            if (customUserDetails.isAccountNonExpired()) {
                String username = customUserDetails.getUsername();
                Member member = memberService.findMember(username);
                String nickname = member.getNickname();

                model.addAttribute("username", username);
                model.addAttribute("nickname", nickname);
                return "info/non-expired";
            }
        }

        return "home";
    }
}
