package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryNoticeBoardRepository implements NoticeBoardRepository {

    private static Map<Long, Post> store = new ConcurrentHashMap<>();
    private long nextId = 1L;

    @Override
    public synchronized void save(Post post) {
        if (post.getId() == null) {
            post.setId(nextId++);
        }
        store.put(post.getId(), post);
    }

    @Override
    public Post find(Long id) {
        return store.get(id);
    }

    @Override
    public void delete(Long id) {
        Post post = store.get(id);
        post.setDeleted(true);
    }

    @Override
    public Page findAll(PostSearch postSearch) {
        String searchString = postSearch.getSearch();
        if ( searchString != null && !searchString.isEmpty() ) {

            List<Post> posts = new ArrayList<>();

            for(Post post : store.values()) {
                String title = post.getTitle();

                StringBuilder text = new StringBuilder();
                StringBuilder initialText = new StringBuilder();
                for (char c : title.toCharArray()) {

                    if (isKorean(c)) {
                        initialText.append(initialConsonants(c));
                    } else { initialText.append(c); }
                    text.append(c);

                    if (text.toString().equals(searchString)) {
                        posts.add(post);
                        break;
                    } else if(initialText.toString().equals(searchString)) {
                        posts.add(post);
                        break;
                    }
                }
            }

            int count_posts = posts.size();

            int current_page = postSearch.getPage();
            int currentPage_post = (current_page - 1) * 9;
            List<Post> pagingPosts = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if(currentPage_post+i > count_posts-1) {
                    break;
                }
                pagingPosts.add( posts.get(currentPage_post + i) );
            }

            int last_page = ((count_posts -1) /9) + 1;

            return new Page.PageBuilder()
                    .posts(pagingPosts)
                    .currentPage(current_page)
                    .lastPage(last_page)
                    .pageList(getPageList(current_page, last_page))
                    .build();

        } else {
            int count_posts = store.size();
            int current_page = postSearch.getPage();
            long currentPage_post = ((current_page - 1) * 9L + 1);
            int last_page = ((count_posts -1) /9) + 1;

            List<Post> pagingPosts = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                if(currentPage_post+i > count_posts) {
                    break;
                }
                pagingPosts.add(store.get(currentPage_post + i));
            }

            return new Page.PageBuilder()
                    .posts(pagingPosts)
                    .currentPage(current_page)
                    .lastPage(last_page)
                    .pageList(getPageList(current_page, last_page))
                    .build();
        }
    }


    public int size() {
        return store.size();
    }

    private boolean isKorean(char t) {
        return (t >= 0xAC00 && t <= 0xD7A3) || (t >= 0x1100 && t <= 0x11FF) || (t >= 0x3130 && t <= 0x318F);
    }

    private char initialConsonants(char c) {

        char[] initialConsonants = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ',
                'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ',
                'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

        if (c >= 0xAC00) {
            char f = (char) ((c - 0xAC00) / 28 / 21);

            return initialConsonants[f];
        } else {
            return c;
        }
    }

    private List<Integer> getPageList(int currentPage, int lastPage) {
        List<Integer> pageList = new ArrayList<>();

        int start = ((currentPage - 1) / 9) * 9 + 1;
        for (int i = 0; i < 9; i++) {
            if (start > lastPage) {
                break;
            }
            pageList.add(start++);
        }
        return pageList;
    }

}
