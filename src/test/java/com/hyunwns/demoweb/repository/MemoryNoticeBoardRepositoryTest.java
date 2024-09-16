package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MemoryNoticeBoardRepositoryTest {


    @Test
    public void 게시물_저장() throws Exception{
        //given
        MemoryNoticeBoardRepository repository = new MemoryNoticeBoardRepository();
        Member member = new Member("test", "테스트", "test", 25);

        String content = "안녕하세요 테스트입니다.";


        Post post = new Post(member, "테스트", content);
        Post post2 = new Post(member, "테스트2", content);

        //when
        repository.save(post);
        repository.save(post2);

        String title = repository.find(2L).getTitle();
        //then
        Assertions.assertEquals("테스트2", title);

    }

    @Test
    public void 게시물_저장후_제목확인() throws Exception{
        //given
        NoticeBoardRepository repository = new MemoryNoticeBoardRepository();
        Member member = new Member("test", "테스트", "test", 25);

        String content = "안녕하세요 테스트입니다.";
        Post post = new Post(member, "테스트", content);

        //when
        repository.save(post);
        String title = repository.find(1L).getTitle();

        //then
        Assertions.assertEquals("테스트", title);
    }

    @Test
    public void 페이지_테스트() throws Exception{
        //given
        int size = 100;

        int page = 1;
        int lastPage = ((size -1) /9) + 1;

        Map<Long, Integer> store = new HashMap<>();
        store.put(1L, 1);
        store.put(2L, 2);
        store.put(3L, 3);
        store.put(4L, 4);
        store.put(5L, 5);
        store.put(6L, 6);
        store.put(7L, 7);
        store.put(8L, 8);
        store.put(9L, 9);

        long test = (( page - 1 ) * 9 + 1);


        List<Integer> search_posts = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            System.out.println(store.get(test + i));

        }


        //when
        System.out.println("현재 페이지: " + page + ", 마지막 페이지: " + lastPage);
        System.out.println("현재 페이지 POST 인덱스 시작값: " + (( page - 1 ) * 9 + 1) );



        //then
    }

    @Test
    public void 페이지_리스트() throws Exception{
        //given
        int current = 10;

        int last = 9;

        //when
        int start = ((current - 1) / 9) * 9 + 1;
        System.out.println("start " + start);

        for (int i = 0; i < 9; i++) {
            if (start > last) {
                break;
            }
            System.out.println(start++);

        }
    }
}