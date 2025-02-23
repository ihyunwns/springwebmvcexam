package com.hyunwns.demoweb.config;

import com.hyunwns.demoweb.domain.post.Comment;
import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.post.Post;
import com.hyunwns.demoweb.service.CommentService;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.service.NoticeBoardService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements InitializingBean {

    @Autowired private MemberService memberService;
    @Autowired private NoticeBoardService noticeBoardService;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private CommentService commentService;

    @Override
    public void afterPropertiesSet() throws Exception {

        String encode = bCryptPasswordEncoder.encode("1234");
        Member member = new Member("tester", "tester", encode, 25);
        memberService.join(member);
        member.setRole("ROLE_ADMIN");

        Member member2 = new Member("tester2", "tester2", encode, 25);
        memberService.join(member2);

        Post post = new Post(member, "TEST", "HI");
        noticeBoardService.post(post);
        commentService.write(new Comment(member, post, null, "HELLO"));

//        for (int i = 0; i < 1000; i++) {
//            Post post = new Post(member, "TEST" + i, "HI");
//            noticeBoardService.post(post);
//            commentService.write(new Comment(member, post, null, "HELLO"));
//        }

    }
}
