package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Post;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.JstlUtils;

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
        return List.of();
    }


    public int size() {
        return store.size();
    }

}
