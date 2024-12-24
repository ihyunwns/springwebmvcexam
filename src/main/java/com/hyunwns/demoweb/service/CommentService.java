package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Comment;
import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface CommentService {
    // 댓글 작성
    void write(Comment comment);

    // 댓글 삭제
    void delete(Comment comment);

    // 댓글 전체 조회
    List<Comment> getComments();

    // 특정 포스터 댓글 조회
    List<Comment> getCommentsByPost(Post post);

    // 특정 작성자 댓글 조회
    List<Comment> getCommentsByCommenter(Member Commenter);

    // ID 단일 조회
    Comment getCommentById(Long id);

}
