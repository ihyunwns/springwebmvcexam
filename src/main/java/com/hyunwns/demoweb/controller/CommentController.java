package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Comment;
import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.dto.CommentDTO;
import com.hyunwns.demoweb.service.CommentService;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.service.NoticeBoardService;
import lombok.RequiredArgsConstructor;
import org.eclipse.tags.shaded.org.apache.regexp.RE;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final NoticeBoardService noticeBoardService;
    private final CommentService commentService;

    /*
     * html의 form 태그에서 버튼을 submit 타입으로 해두면 버튼 클릭할 때 form 형식으로 요청이 외서 @RequestBody의 json 타입과 타입이 맞지 않아 에러가 발생한다
     * 이를 해결하기 위해서는 form 태그안에 버튼의 타입을 button으로 두면 된다. form의 action 또한 그냥 "#"으로 두었음
     * */
    @PostMapping(value = "/comment")
    public ResponseEntity<?> comment(@RequestBody CommentDTO commentDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberId = auth.getName();

        Member member = memberService.findMember(memberId);
        String content = commentDTO.getContent();
        Long postId = commentDTO.getPostId();

        Post post = noticeBoardService.findPost(postId);

        Comment comment = new Comment(member, post, null, content);
        commentService.write(comment);

        return ResponseEntity.ok().build();
    }

    // 비동기 댓글 로딩
    @GetMapping(value = "/comment/{postId}/load", produces = "application/json")
    public List<CommentDTO> display(@PathVariable("postId") Long postId) {

        Post post = noticeBoardService.findPost(postId);

        List<CommentDTO> commentDTO = new ArrayList<>();
        for (Comment comment : commentService.getCommentsByPost(post)) {
            if (comment.getParent() != null) {
                continue;
            }

            CommentDTO dto = new CommentDTO();
            if (!comment.getChildren().isEmpty()) {
                dto.setChildExist(comment.getChildren().size());
            }

            dto.setCommenterId(comment.getCommenter().getId());
            dto.setContent(comment.getContent());
            dto.setPostId(postId);
            dto.setId(comment.getId());

            commentDTO.add(dto);
        }

        return commentDTO;

    }

    @GetMapping("/icon/{userId}")
    public ResponseEntity<Resource> icon(@PathVariable("userId") String userId) {

        // 원래는 이렇게 member를 가져와서 개별 설정한 icon을 가져와야 하지만 그 기능은 아직 미제작으로 일단 기본 test 파일을 건내주는 것으로 대체
//        Member member = memberService.findMember(userId);

//        String iconURL = member.getIconURL();
//        Path path = Paths.get("파일경로" + iconURL);

        try {
            Path path = Paths.get("C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\resources\\userIcon\\test.png");
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                        .filename(resource.getFilename(), StandardCharsets.UTF_8)
                        .build();
                // 이미지 파일을 브라우저에 보여줌
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();

        }
    }

    // 브라우저에서 임의로 댓글을 삭제할 수 있으므로 댓글 삭제자가 해당 포스터의 소유자인지 확인해야함
    // 이런식으로 컨트롤러단에서 검증을 거치는 건 프로젝트 규모가 커질수록 재사용성이 떨어진다.

    // 따라서 이런 검증 로직을 캡슐화해서 넘겨준 에러를 컨트롤러단에서는 캐치해서 ResponseEntity 반환하는 방법
    // edit 단은 그렇게 해보자 !
    @GetMapping("/comment/{commentID}/delete")
    public ResponseEntity<?> delete(@PathVariable("commentID") Long commentID) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Member requester = memberService.findMember(name);

        Comment comment = commentService.getCommentById(commentID);

        Member commenter = comment.getCommenter();

        if (!requester.getId().equals(commenter.getId())) {
             return ResponseEntity.badRequest().build();
        }

        commentService.delete(comment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/comment/{commentID}/edit")
    public ResponseEntity<?> edit(@PathVariable("commentID") Long commentID) {

        Comment comment = commentService.getCommentById(commentID);
        CommentDTO commentDTO = setCommentDTO(comment);

        Map<String, Object> response = new HashMap<>();

        response.put("content", commentDTO.getContent());
        response.put("postId", commentDTO.getPostId());
        response.put("commenterId", commentDTO.getCommenterId());
        response.put("id", commentDTO.getId());

        return ResponseEntity.ok(response);

    }

    @PostMapping("/comment/edit")
    public ResponseEntity<?> edit(@RequestBody CommentDTO commentDTO) {

        Long id = commentDTO.getId();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Member member = memberService.findMember(name);

        try{
            commentService.edit(id, commentDTO.getContent(), member);
        }catch (AccessDeniedException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", "You don't have permission to edit this comment");

            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().build();

    }

    @PostMapping("/comment/reply")
    public ResponseEntity<?> reply(@RequestBody CommentDTO commentDTO) {

        Member member = thisMember();

        Post post = noticeBoardService.findPost(commentDTO.getPostId());
        Comment parent = commentService.getCommentById(commentDTO.getId());

        Comment comment = new Comment(member, post, parent, commentDTO.getContent());
        commentService.write(comment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/comment/{commentId}/replies")
    public List<CommentDTO> replyLoad(@PathVariable("commentId") Long commentId, CommentDTO commentDTO) {

        List<CommentDTO> comments = new ArrayList<>();

        // 자식 댓글 가져오기
        Comment comment = commentService.getCommentById(commentId);
        List<Comment> children = comment.getChildren();

        for(Comment child : children) {
            CommentDTO dto = new CommentDTO();

            dto.setId(child.getId());
            dto.setContent(child.getContent());
            dto.setPostId(child.getPost().getId());
            dto.setCommenterId(child.getCommenter().getId());

            comments.add(dto);
        }

        return comments;
    }

    private CommentDTO setCommentDTO(Comment comment) {

        CommentDTO dto = new CommentDTO();
        dto.setCommenterId(comment.getCommenter().getId());
        dto.setContent(comment.getContent());
        dto.setPostId(comment.getPost().getId());
        dto.setId(comment.getId());

        return dto;
    }

    private Member thisMember() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();

        return memberService.findMember(name);
    }

}
