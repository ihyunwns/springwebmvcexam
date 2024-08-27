package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.dto.SignUpDTO;

public interface MemberService {

    // 더 나은 회원가입 구현을 위한 설명

    // 프론트단에서 회원가입 아이디 중복 로직 추가
    // httpXMLRequest 메소드를 통해 백엔드에 미리 구현해둔 API 에 이미 존재하는 아이디인지 검증하는 로직을 추가해야 한다.

    // 가입 불가 문자 정규식 처리
    // 아이디의 자리수, 아이디의 특수문자 포함 불가, admin과 같은 아이디 사용 불가, 비밀번호 자릿수, 비밀번호 특수문자 포함 필수 등

    void join(Member member);
    Member findMember(String memberId);

}
