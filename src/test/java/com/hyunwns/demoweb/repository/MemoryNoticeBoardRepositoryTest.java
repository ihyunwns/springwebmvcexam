package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryNoticeBoardRepositoryTest {


    @Test
    public void 게시물_저장() throws Exception{
        //given
        MemoryNoticeBoardRepository repository = new MemoryNoticeBoardRepository();
        Member member = new Member("test", "테스트", "test", 25);

        String[] content = {"안녕하세요", "테스트입니다."};


        Post post = new Post(1L, member, "테스트", content);

        //when
        repository.save(post);

        //then
        Assertions.assertEquals(1, repository.size());

    }

    @Test
    public void 게시물_저장후_제목확인() throws Exception{
        //given
        NoticeBoardRepository repository = new MemoryNoticeBoardRepository();
        Member member = new Member("test", "테스트", "test", 25);

        String[] content = {"안녕하세요", "테스트입니다."};
        Post post = new Post(1L, member, "테스트", content);

        //when
        repository.save(post);
        String title = repository.find(1L).getTitle();

        //then
        Assertions.assertEquals("테스트", title);
    }
}