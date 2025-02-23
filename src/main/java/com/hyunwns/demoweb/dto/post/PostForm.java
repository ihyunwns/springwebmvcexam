package com.hyunwns.demoweb.dto.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class PostForm {

    private MultipartFile imgFile;

    private String title;
    private String content;
    private Long postId;
//    private String author;

}
