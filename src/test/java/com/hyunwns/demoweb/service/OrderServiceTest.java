package com.hyunwns.demoweb.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

class OrderServiceTest {

    MemberService memberService;

    static String path = "C:\\Users\\ihyun\\Desktop\\demoweb\\src\\main\\webapp\\WEB-INF\\spring\\appServlet\\servlet-context.xml";
    @BeforeEach
    void setUp() {
        ApplicationContext ac = new FileSystemXmlApplicationContext(path);

        memberService = ac.getBean(MemberServiceImpl.class);
    }


}