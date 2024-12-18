package com.hyunwns.demoweb.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Comment {

    /* 댓글
    *    ㄴ 답글
    *    ㄴ 태그 답글 ( 답글의 답글 )
    * */

    // 작성자
    Member Commenter;
    // 댓글의 게시글
    Post post;

    // 루트 게시글인 경우 null
    Comment parent;

    // 댓글의 자식 댓글
    List<Comment> children;

}
