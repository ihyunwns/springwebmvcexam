package com.hyunwns.demoweb.config;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.repository.MemberRepository;
import com.hyunwns.demoweb.repository.NoticeBoardRepository;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.service.NoticeBoardService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements InitializingBean {

    @Autowired private MemberService memberService;
    @Autowired private NoticeBoardService noticeBoardService;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void afterPropertiesSet() throws Exception {

        String encode = bCryptPasswordEncoder.encode("1234");
        Member member = new Member("tester", "tester", encode, 25);
        memberService.join(member);

        Member member2 = new Member("tester2", "tester2", encode, 25);
        memberService.join(member2);

//        for (int i = 0; i < 1000; i++) {
//            noticeBoardService.post(new Post(member, "TEST"+i, "HI"));
//        }

    }
}
