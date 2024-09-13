package com.hyunwns.demoweb.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter @Setter
public class Post {

    private Long id;
    private final Member author;
    private final LocalDateTime published;

    private String title;
    private String content;
    private boolean isDeleted;

    private File thumbnail;

    private List<Comment> comments;

    public Post(Member author, String title, String content) {

        this.author = author;
        this.title = title;
        this.content = content;

        published = LocalDateTime.now();
        isDeleted = false;
        comments = new ArrayList<>();

    }

}
