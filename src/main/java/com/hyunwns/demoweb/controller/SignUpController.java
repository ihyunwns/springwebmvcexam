package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.dto.SignUpDTO;
import com.hyunwns.demoweb.service.InputVerificationService;
import com.hyunwns.demoweb.service.MemberService;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final InputVerificationService inputVerificationService;

    @GetMapping("/hello")
    public String hello() {
        return "jsp/hello";
    }


    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignUpDTO());
        return "user/signupForm";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute("signupRequest") SignUpDTO signUpDTO, Model model) throws ServletException, IOException {
        SignUpDTO afterVerification;
        try {
            afterVerification = inputVerificationService.verification(signUpDTO);

            String id = afterVerification.getId();
            String password = afterVerification.getPassword();
            String nickname = afterVerification.getNickname();
            int age = signUpDTO.getAge();

            String bCryptPassword = bCryptPasswordEncoder.encode(password);
            Member member = new Member(id, nickname, bCryptPassword, age);
            memberService.join(member);

            return "redirect:/";

        } catch (RuntimeException e) {
            // 모델에 담아서 signupFail에 넘겨줌
            model.addAttribute("error", e.getMessage());

            return "/info/signupFail";
        }



    }

    @GetMapping(value = "/checkID", produces = "application/json")
    @ResponseBody
    public Map<String, Boolean> checkID(@RequestParam(name = "userID") String userId) {
        Map<String, Boolean> response = new HashMap<>();
        String id = userId.toLowerCase();

        try {
            memberService.findMember(id);
        } catch (UsernameNotFoundException e) { // 멤버 찾지 못한 경우
            response.put("Available", true);
            return response;
        }
        response.put("Available", false);
        return response;
    }
}
