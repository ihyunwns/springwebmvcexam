package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Comment;
import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.dto.CommentDTO;
import com.hyunwns.demoweb.service.CommentService;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.service.NoticeBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final NoticeBoardService noticeBoardService;
    private final CommentService commentService;

    // AJAX 비동기
    @PostMapping(value = "/comment")
    public ResponseEntity<?> comment(@RequestBody CommentDTO commentDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberId = auth.getName();

        Member member = memberService.findMember(memberId);

        String content = commentDTO.getContent();
        Long postId = commentDTO.getPostId();

        Post post = noticeBoardService.findPost(postId);

        Comment comment = new Comment(member, post, null, content);


        return null;
    }

    // 비동기 댓글 로딩
    @GetMapping(value="/comment/{postId}/load", produces = "application/json")
    @ResponseBody
    public List<CommentDTO> display(@PathVariable("postId") Long postId) {

        Post post = noticeBoardService.findPost(postId);

        List<CommentDTO> commentDTO = new ArrayList<>();
        for(Comment comment : commentService.getCommentsByPost(post)) {
            commentDTO.add(new CommentDTO(comment.getCommenter().getId(), comment.getContent(), postId));
        }

        return commentDTO;

    }

}
