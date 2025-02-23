package com.hyunwns.demoweb.dto.post;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostSearch {
    private String search;
    private int page = 1;
}
