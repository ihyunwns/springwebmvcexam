package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryNoticeBoardRepository implements NoticeBoardRepository {

    private static Map<Long, Post> store = new ConcurrentHashMap<>();

    @Override
    public void save(Post post) {
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
