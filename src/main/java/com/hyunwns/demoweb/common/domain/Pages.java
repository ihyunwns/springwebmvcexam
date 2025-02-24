package com.hyunwns.demoweb.common.domain;

import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
public class Pages<T> {

    private final List<T> pagedList;
    private final int page;
    private final int size;
    private final int totalElements;
    private final int totalPages;

    private Pages(Builder<T> builder) {
        this.page = builder.page;
        this.size = builder.size;
        this.totalElements = builder.pagedList.size();
        this.totalPages = (int) Math.ceil((double) totalElements / size);

        // 실제 페이징 적용 (start ~ end 범위의 데이터를 가져옴)
        int start = Math.max(0, (page - 1) * size);
        int end = Math.min(start + size, totalElements);
        this.pagedList = builder.pagedList.subList(start, end);
    }

    public static <T> Builder<T> setPagesConfigure(List<T> objects) {
        return new Builder<>(objects);
    }

    public static class Builder<T>{

        private final List<T> pagedList;
        private int page = 1;
        private int size = 9;
        private Comparator<T> comparator;

        Builder(List<T> objects) {
            this.pagedList = objects;
        }

        public Builder<T> setPage(int page) {
            this.page = Math.max(1, page);
            return this;
        }

        public Builder<T> setSize(int size){
            this.size = Math.max(1, size);
            return this;
        }

        public Builder<T> sortBy(Comparator<T> comparator) {
            this.comparator = comparator;
            return this;
        }

        public Pages<T> build(){
            if(comparator != null){
                pagedList.sort(comparator);
            }
            return new Pages<>(this);
        }
    }
}
