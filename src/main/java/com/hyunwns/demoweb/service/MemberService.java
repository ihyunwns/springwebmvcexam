package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.dto.SignUpDTO;

public interface MemberService {

    void join(Member member);
    Member findMember(String memberId);
    boolean isLogin(String memberId, String password);

}
