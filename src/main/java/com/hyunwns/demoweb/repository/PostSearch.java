package com.hyunwns.demoweb.repository;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostSearch {
    private String search;
    private int page = 1;
}
