package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.repository.MemoryNoticeBoardRepository;
import com.hyunwns.demoweb.repository.NoticeBoardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class NoticeBoardServiceImplTest {

    NoticeBoardService noticeBoardService;
    NoticeBoardRepository noticeBoardRepository;
    static String path = "C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\webapp\\WEB-INF\\spring\\appServlet\\servlet-context.xml";

    @BeforeEach
    void setUp() {
        ApplicationContext ac = new FileSystemXmlApplicationContext(path);
        noticeBoardService = ac.getBean(NoticeBoardServiceImpl.class);
        noticeBoardRepository = ac.getBean(MemoryNoticeBoardRepository.class);
    }


    @Test
    public void 게시물_게시() throws Exception {
        //given
        String content = "안녕하세요 테스트입니다.";
        Member member = new Member("test", "테스트", "test", 25);

        Post post = new Post(member, "테스트", content);

        //when
        noticeBoardService.post(post);

        //then
        Post findPost = noticeBoardRepository.find(1L);

        Assertions.assertEquals(post, findPost);

    }

}