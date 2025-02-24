package com.hyunwns.demoweb.post.service;

import com.hyunwns.demoweb.post.domain.Comment;
import com.hyunwns.demoweb.common.domain.Member;
import com.hyunwns.demoweb.post.domain.Post;

import java.util.List;

public interface CommentService {
    // 댓글 작성
    void write(Comment comment);

    void edit(Long id, String content, Member requester);

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
