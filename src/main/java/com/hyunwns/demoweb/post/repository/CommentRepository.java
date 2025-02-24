package com.hyunwns.demoweb.post.repository;

import com.hyunwns.demoweb.post.domain.Comment;
import com.hyunwns.demoweb.common.domain.Member;
import com.hyunwns.demoweb.post.domain.Post;

import java.util.List;

public interface CommentRepository {

    void save(Comment comment);

    void delete(Comment comment);

    // 댓글 전체 조회
    List<Comment> findAll();

    // 특정 포스터 댓글 조회
    List<Comment> findByPost(Post post);

    // 특정 작성자 댓글 조회
    List<Comment> findByCommenter(Member Commenter);

    // ID 단일 조회
    Comment findById(Long id);

}
