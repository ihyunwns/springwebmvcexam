package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검사
        memberRepository.save(member);
    }

    @Override
    public Member findMember(String memberId) {
        Member findMember = memberRepository.findOne(memberId);
        if(findMember == null) {
            throw new IllegalStateException("Not found member with id " + memberId);
        }
        return findMember;
    }

    private void validateDuplicateMember(Member member) {
        List<Member> members = memberRepository.findById(member.getId());

        // 중복 검사 해서 중복나오면 에러 throw
        if (!members.isEmpty()) {
            throw new IllegalStateException("Duplicate member");
        }
    }


}
