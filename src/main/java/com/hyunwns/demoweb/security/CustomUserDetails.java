package com.hyunwns.demoweb.security;

import com.hyunwns.demoweb.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// 인증에 성공하여 생성된 UserDetails 객체는 Authentication 객체를 구현한 UsernamePasswordAuthenticationToken을 생성하기 위해 사용
public class CustomUserDetails implements UserDetails {

    private Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    // 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 목록에 어떤 Collection 을 넘겨 줘야 하는지 ?
        // List< GrantedAuthority 를 상속받는 클래스 >
        return List.of();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getId();
    }



}
