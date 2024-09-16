package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.JstlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
    public List<Post> findAll(PostSearch postSearch) {
        String searchString = postSearch.getSearch();
        if (!(searchString == null)) {

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
            return posts;
        } else {
            return new ArrayList<>(store.values());
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

}
