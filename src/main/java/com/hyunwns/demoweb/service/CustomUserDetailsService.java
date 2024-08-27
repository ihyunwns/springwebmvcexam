package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.repository.MemberRepository;
import com.hyunwns.demoweb.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    // Spring Security 를 통해 인증을 진행하는 방법은 사용자가 Login 페이지를 통해 아이디, 비밀번호를 POST 요청 시, 데이터베이스에 저장된
    // 회원 정보를 조회 후 비밀번호를 검증하고 서버세션 저장소에 해당 아이디에 대한 세션을 저장한다.
    // 이를 검증하기 위해서는 UserDetailsService 와 UserDetails 를 구현해야 한다.

    private final MemberRepository memberRepository;


    // "/login" 경로로 POST 요청이 오면 스프링 시큐리티 내부적으로 UsernamePasswordAuthenticationFilter 가 동작을 하게되고
    // 이때 AuthenticationProvider(인터페이스) > DaoAuthenticationProvider(구현체) 에 의해 CustomUserDetailsService 의 loadUserByUsername 을 호출하여 DB에 있는 유저를 조회하게 됩니다.
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // loginForm 속성값을 잘못 쓰면 Spring Security가 userID 값을 가져오지 못하는 현상이 있음
        // 이것때문에 또 몇시간 날렸음 ..

        Member member = memberRepository.findOne(userId);

        if(member != null) {
            return new CustomUserDetails(member);
        }

        return null;

    }
}
