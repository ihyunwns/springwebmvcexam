package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.post.Comment;
import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.post.Post;

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
