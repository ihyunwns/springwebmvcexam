package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.dto.SignUpDTO;
import com.hyunwns.demoweb.repository.MemberRepository;
import com.hyunwns.demoweb.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final MemberRepository memberRepository;

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

        // 컨트롤러에서 인코딩해서 넘겨준다? 아니면 문자열 받고 서비스 단에서 인코딩한다? 아니면 프론트 쪽에서 인코딩한다?
        String id = signUpDTO.getId();
        String password = bCryptPasswordEncoder.encode(signUpDTO.getPassword());
        System.out.println(password);
        String nickname = signUpDTO.getNickname();
        int age = signUpDTO.getAge();

        Member member = new Member(id, nickname, password, age);
        memberService.join(member);

        return "redirect:/";

    }

}
