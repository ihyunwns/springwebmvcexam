package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Comment;
import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void edit(Long id, String content) {
        Comment comment = commentRepository.findById(id);
        comment.setContent(content);
    }

    @Override
    public void write(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }


    @Override
    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    @Override
    public List<Comment> getCommentsByCommenter(Member Commenter) {
        return commentRepository.findByCommenter(Commenter);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id);
    }

}
