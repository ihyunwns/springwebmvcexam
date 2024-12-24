package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Comment;
import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryCommentRepository implements CommentRepository {

    private static Map<Long, Comment> comments = new ConcurrentHashMap<>();
    private Long id = 0L;

    @Override
    public synchronized void save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(++id);
        }
        comments.put(comment.getId(), comment);

        Post post = comment.getPost();
        post.addComment(comment);
    }

    @Override
    public void delete(Comment comment) {
        Long id = comment.getId();
        comments.remove(id);
    }

    // 모든 댓글 객체 가져오기
    @Override
    public List<Comment> findAll() {
        return new ArrayList<>(comments.values());
    }

    // 게시글 별 댓글 리스트 가져오기
    @Override
    public List<Comment> findByPost(Post post) {
        return post.getComments();
    }

    // 작성자 별 댓글 리스트 가져오기
    @Override
    public List<Comment> findByCommenter(Member commenter) {
        return commenter.getComments();
    }

    // 단일 댓글 가져오기
    @Override
    public Comment findById(Long id) {
        return comments.get(id);
    }

}
