package com.hyunwns.demoweb.post.service;

import com.hyunwns.demoweb.post.domain.Comment;
import com.hyunwns.demoweb.common.domain.Member;
import com.hyunwns.demoweb.post.domain.Post;
import com.hyunwns.demoweb.post.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
    public void edit(Long id, String content, Member requester){

        validateCommentOwner(id, requester);

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

    private void validateCommentOwner(Long commentId, Member requester) {

        Comment comment = getCommentById(commentId);
        String commenter = comment.getCommenter().getId();

        if (!commenter.equals(requester.getId())) {
            throw new AccessDeniedException("You don't access a this comment");
        }

    }

}
