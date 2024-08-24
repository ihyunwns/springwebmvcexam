package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public interface MemberRepository {

    // 저장 기능
    void save(Member member);

    // 멤버 찾기 기능
    Member findOne(String id);

    List<Member> findAll();

    List<Member> findById(String id);


}

