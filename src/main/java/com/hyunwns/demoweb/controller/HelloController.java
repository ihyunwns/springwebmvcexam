package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class HelloController {

    private final MemberService memberService;

    @GetMapping("/hello")
    public String hello() {
        return "jsp/hello";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") LoginForm loginForm, Model model) {

        String id = loginForm.getId();
        Member member = memberService.findMember(id); // < 예외 던져서 thymeleaf 에 던져줘야함
        int age = member.getAge();
        String password = (loginForm.getPassword().hashCode() + age) + id;

        System.out.println(id);
        System.out.println(password);

        if (password.equals(member.getPassword())) {
            System.out.println("Same password");
            return "redirect:/hello"; //view 에다가 넘겨주는게 아닌 URL 요청
        }

        model.addAttribute("loginErr", "PE");

        return "home";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignUpDTO());
        return "user/signupForm";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute("signupRequest") SignUpDTO signUpDTO) {

        String id = signUpDTO.getId();
        String nickname = signUpDTO.getNickname();
        int age = signUpDTO.getAge();
        String password = ( signUpDTO.getPassword().hashCode() + age ) + id;

        Member member = new Member(id, nickname, password, age);
        memberService.join(member);

        return "redirect:/";
    }

}
