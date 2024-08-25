package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.repository.MemberRepository;
import com.hyunwns.demoweb.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findOne(userId);

        return new CustomUserDetails(member);

    }
}
