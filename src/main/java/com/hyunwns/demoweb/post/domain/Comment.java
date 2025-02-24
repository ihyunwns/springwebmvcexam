package com.hyunwns.demoweb.post.domain;

import com.hyunwns.demoweb.common.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Comment {

    private Long id;

    /* 댓글
    *    ㄴ 답글
    *    ㄴ 태그 답글 ( 답글의 답글 )
    * */

    private final Member Commenter;
    private final Post post;
    private final Comment parent;

    private List<Comment> children;

    private String content;

    public Comment(Member commenter, Post post, Comment parent, String content) {
        Commenter = commenter;
        this.post = post;
        this.parent = parent;
        this.content = content;

        if (parent == null) {
            children = new ArrayList<>();
        }
    }

}
