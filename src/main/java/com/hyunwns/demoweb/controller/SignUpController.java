package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.dto.SignUpDTO;
import com.hyunwns.demoweb.repository.MemberRepository;
import com.hyunwns.demoweb.service.InputVerificationService;
import com.hyunwns.demoweb.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String signUp(@ModelAttribute("signupRequest") SignUpDTO signUpDTO) {


        String id = signUpDTO.getId().toLowerCase();
        if (memberService.isMemberExist(id)) {
            System.out.println("동일 멤버 발견 가입 불가");
        }

        if (!inputVerificationService.IdVerification(id)) {
            System.out.println("아이디 정규식 에러");
        }

        String nickname = signUpDTO.getNickname();
        if(!inputVerificationService.NicknameVerification(nickname)) {
            System.out.println("닉네임 검증 에러");
        }

        String password = bCryptPasswordEncoder.encode(signUpDTO.getPassword());

        int age = signUpDTO.getAge();

        Member member = new Member(id, nickname, password, age);
        memberService.join(member);

        return "redirect:/";

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
