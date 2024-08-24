package com.hyunwns.demoweb.repository;

import com.hyunwns.demoweb.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static Map<String, Member> members = new ConcurrentHashMap<>();

    @Override
    public void save(Member member) {
        members.put(member.getId(), member);
    }

    @Override
    public Member findOne(String id) {
        return members.get(id);
    }

    @Override
    public List<Member> findAll() {
        List<Member> memberList = new ArrayList<>();
        memberList.addAll(members.values());

        return memberList;
    }

    @Override
    public List<Member> findById(String id) {
        List<Member> memberList = new ArrayList<>();

        // 이름으로 멤버 찾기
        for(Member member : members.values()) {
            if(member.getId().equals(id)) {
                memberList.add(member);
            }
        }
        return memberList;
    }
}

