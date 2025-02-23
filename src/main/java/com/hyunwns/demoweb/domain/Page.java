package com.hyunwns.demoweb.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class Page {

    // TODO: 한 화면에 보여줄 Object에 따른 페이지 설정 추가하기

    private final List<?> objects;
    private final int current_page;
    private final int last_page;
    private final List<Integer> pageList;

    private Page(PageBuilder builder) {
        this.objects = builder.objects;
        this.current_page = builder.current_page;
        this.last_page = builder.last_page;
        this.pageList = builder.pageList;
    }

    public static class PageBuilder {
        private List<?> objects;
        private int current_page;
        private int last_page;
        private List<Integer> pageList;

        // Builder 메서드를 통해 각 필드를 설정
        public PageBuilder posts(List<?> objects) {
            this.objects = objects;
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
