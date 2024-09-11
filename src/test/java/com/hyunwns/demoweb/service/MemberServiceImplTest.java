package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceImplTest {

    MemberService memberService;
    static String path = "C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\webapp\\WEB-INF\\spring\\appServlet\\servlet-context.xml";

    @BeforeEach
    void setUp() {
        ApplicationContext ac = new FileSystemXmlApplicationContext(path);
        memberService = ac.getBean(MemberServiceImpl.class);
    }

    @Test
    public void 멤버_찾기_예외() throws Exception {

        //given
        Member member = new Member("ihyunwns", "임현준", "1234", 1);

        //when
        memberService.join(member);

        //then
        Assertions.assertThrows(UsernameNotFoundException.class, () -> memberService.findMember("test"));
    }

}