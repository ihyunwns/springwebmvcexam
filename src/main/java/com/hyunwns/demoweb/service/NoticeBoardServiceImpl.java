package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.repository.NoticeBoardRepository;
import com.hyunwns.demoweb.repository.PostSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeBoardServiceImpl implements NoticeBoardService {

    private final NoticeBoardRepository noticeBoardRepository;

    @Autowired
    public NoticeBoardServiceImpl(NoticeBoardRepository noticeBoardRepository) {
        this.noticeBoardRepository = noticeBoardRepository;
    }

    @Override
    public void post(Post post) {
        noticeBoardRepository.save(post);
    }

    @Override
    public void delete(Long id) {
        noticeBoardRepository.delete(id);
    }

    @Override
    public List<Post> findPost(PostSearch postSearch) {
        return noticeBoardRepository.findAll(postSearch);
    }

    @Override
    public Post findPost(Long id) {
        return noticeBoardRepository.find(id);
    }
}
