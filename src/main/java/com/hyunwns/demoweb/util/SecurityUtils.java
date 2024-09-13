package com.hyunwns.demoweb.util;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final MemberService memberService;

    public void addAttributeUserInfo(Model model) {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Not Found UserDetails in SecurityContextHolder");
            throw new RuntimeException("유저 정보를 찾지 못했습니다");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String id = auth.getName();
        Member member = memberService.findMember(id);

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        GrantedAuthority authority = authorities.iterator().next();
        String role = authority.getAuthority();

        model.addAttribute("id", id);
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("role", role);

    }
}
