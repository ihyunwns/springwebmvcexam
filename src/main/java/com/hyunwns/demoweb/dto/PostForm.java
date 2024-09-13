package com.hyunwns.demoweb.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Getter @Setter
public class PostForm {

    private MultipartFile imgFile;

    private String title;
    private String content;
//    private String author;

}
