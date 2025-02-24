package com.hyunwns.demoweb.post.service;

import com.hyunwns.demoweb.common.domain.Member;
import com.hyunwns.demoweb.post.domain.Post;
import com.hyunwns.demoweb.post.repository.NoticeBoardRepository;
import com.hyunwns.demoweb.common.domain.Page;
import com.hyunwns.demoweb.post.dto.PostSearch;
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
    public Page findPost(PostSearch postSearch) {
        return noticeBoardRepository.findAll(postSearch);
    }

    @Override
    public Post findPost(Long id) {
        return noticeBoardRepository.find(id);
    }

    @Override
    public List<Post> findPostByMember(Member member) {
        return noticeBoardRepository.findByMember(member);
    }
}
