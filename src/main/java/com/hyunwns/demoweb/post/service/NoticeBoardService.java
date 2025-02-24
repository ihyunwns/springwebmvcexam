package com.hyunwns.demoweb.post.service;

import com.hyunwns.demoweb.common.domain.Member;
import com.hyunwns.demoweb.post.domain.Post;
import com.hyunwns.demoweb.common.domain.Page;
import com.hyunwns.demoweb.post.dto.PostSearch;

import java.util.List;

public interface NoticeBoardService {

    // 게시하기
    void post(Post post);

    void delete(Long id);

    //검색하기
    Page findPost(PostSearch postSearch);

    Post findPost(Long id);

    //특정 멤버의 게시물 조회
    List<Post> findPostByMember(Member member);

}
