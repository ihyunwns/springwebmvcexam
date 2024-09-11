package com.hyunwns.demoweb.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter @Setter
public class Post {

    private final Long id;
    private final Member author;

    private String title;
    private String[] content;
    private boolean isDeleted;

    private List<Comment> comments;

    public Post(Long id, Member author, String title, String[] content) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;

        isDeleted = false;
        comments = new ArrayList<>();
    }

}
