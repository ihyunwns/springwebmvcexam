package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Page;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.domain.PostSearch;

import java.util.List;

public interface NoticeBoardRepository {

    //저장하기
    void save(Post post);

    //찾기
    Post find(Long id);

    //삭제하기
    void delete(Long id);

    // save() 로 대체
    // void update(Post post);

    //검색하기
    Page findAll(PostSearch postSearch);

    // 멤버의 포스트 가져오기
    List<Post> findByMember(Member member);

}
