package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Post;
import lombok.Getter;

import java.util.List;

@Getter
public class Page {

    private List<Post> posts;
    private int current_page;
    private int last_page;
    private List<Integer> pageList;

    private Page(PageBuilder builder) {
        this.posts = builder.posts;
        this.current_page = builder.current_page;
        this.last_page = builder.last_page;
        this.pageList = builder.pageList;
    }

    public static class PageBuilder {
        private List<Post> posts;
        private int current_page;
        private int last_page;
        private List<Integer> pageList;

        // Builder 메서드를 통해 각 필드를 설정
        public PageBuilder posts(List<Post> posts) {
            this.posts = posts;
            return this;
        }

        public PageBuilder currentPage(int current_page) {
            this.current_page = current_page;
            return this;
        }

        public PageBuilder lastPage(int last_page) {
            this.last_page = last_page;
            return this;
        }

        public PageBuilder pageList(List<Integer> pageList) {
            this.pageList = pageList;
            return this;
        }

        public Page build() {
            return new Page(this);  // Page 객체 생성
        }
    }


}
