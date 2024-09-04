package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.dto.SignUpDTO;
import com.hyunwns.demoweb.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        validateDuplicateMember(member);// 중복 회원 검사
        member.setRole("ROLE_USER");

        memberRepository.save(member);
}

    @Override
    public Member findMember(String memberId) {
        Member findMember = memberRepository.findOne(memberId);
        if(findMember == null) {
            throw new UsernameNotFoundException("Not found member with id " + memberId);
        }
        return findMember;
    }

    @Override
    public boolean isMemberExist(String memberId) {
        Member findMember = memberRepository.findOne(memberId);
        return findMember != null;
    }

    private void validateDuplicateMember(Member member) {
        List<Member> members = memberRepository.findById(member.getId());

        // 중복 검사 해서 중복나오면 에러 throw
        if (!members.isEmpty()) {
            throw new IllegalStateException("Duplicate member");
        }
    }


}
