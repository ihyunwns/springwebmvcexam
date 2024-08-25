package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.service.MemberServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


class MemoryMemberRepositoryTest {

    MemberService memberService;
    static String path = "C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\webapp\\WEB-INF\\spring\\appServlet\\servlet-context.xml";

    @BeforeEach
    void setUp() {
        ApplicationContext ac = new FileSystemXmlApplicationContext(path);
        memberService = ac.getBean(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("메모리 저장소 저장")
    public void save() throws Exception{
        //given
        Member member = new Member("ihyunwns", "임현준", "1234", 25);
        //when
        memberService.join(member);
        //then
        Member findMember = memberService.findMember("ihyunwns");
        Assertions.assertEquals(findMember, member);
    }

    @Test
    public void 중복_회원_검출() throws Exception{
        //given
        Member member1 = new Member("ihyunwns", "임현준", "1234", 25);
        memberService.join(member1);

        //when
        Member member2 = new Member("ihyunwns", "임현준2", "12342", 252);

        //then
        Assertions.assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }


}