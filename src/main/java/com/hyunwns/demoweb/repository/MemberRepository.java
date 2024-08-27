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

    // JPA를 사용하게 되면 '메서드 이름 쿼리 생성' 기능으로 메서드 이름을 분석하여 자동으로 SQL 쿼리문을 작성해준다. 개사기...
    List<Member> findById(String id);


}

