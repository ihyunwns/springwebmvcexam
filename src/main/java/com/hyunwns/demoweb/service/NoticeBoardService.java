package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.repository.PostSearch;

import java.util.List;

public interface NoticeBoardService {

    // 게시하기
    void post(Post post);

    void delete(Long id);

    //검색하기
    List<Post> findPost(PostSearch postSearch);

    Post findPost(Long id);

}
