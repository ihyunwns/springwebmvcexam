package com.hyunwns.demoweb.service;

import org.springframework.stereotype.Service;


@Service
public class InputVerificationService {

    //ID 문자열 정규식 검증, 영어, 숫자만 가능
    public boolean IdVerification(String id) {

        // 영어 유니코드 a-z (97-122) / A-Z (65-90) / 0-9 (48-57)
        if(id.length() <= 4 || id.length() > 10) {
            return false;
        }

        String[] words = id.split("");
        for(String word : words) {
            char uniChar = word.charAt(0);
            if ( !((uniChar >= 97 && uniChar <= 122) || (uniChar >= 65 && uniChar <= 90) || (uniChar >= 48 && uniChar <= 57)) ) {
                return false;
            }
        }
        return true;
    }

    // 영어, 한글, 숫자 가능 ( 한글은 완성되어야 함 )
    //닉네임 문자열 정규식 검증
    public boolean NicknameVerification(String nickname) {
        if(nickname.length() <= 1 || nickname.length() > 8) {
            return false;
        }

        String[] words = nickname.split("");
        for(String word : words) {
            char uniChar = word.charAt(0);
            if (isWhatChar(uniChar).equals("SPECIAL")) {
                return false;
            } else if (isWhatChar(uniChar).equals("KOREAN")) {
                if (uniChar < 0xAC00) {
                    // 자음/모음만 들어온 경우
                    return false;
                }
            }
        }

        return true;
    }

    //비밀번호 문자열 정규식 검증
    /*  ! @ # $ % ^ ~ & *    */
    public boolean PasswordVerification(String password) {
        return true;
    }

    //비밀번호 확인 검증
    public boolean Password2Verfication(String password2) {
        return true;
    }

    //나이 범위 검증
    public boolean AgeVerification(String age) {
        return true;
    }

    private String isWhatChar(char t) {
        if ( (t >= 97 && t <= 122) || (t >= 65 && t <= 90) || (t >= 48 && t <= 57) ) {
            return "ENGLISH/NUMBER";
        } else if ((t >= 0xAC00 && t <= 0xD7A3) || (t >= 0x1100 && t <= 0x11FF) || (t >= 0x3130 && t <= 0x318F)) {
            return "KOREAN";
        } else {
            return "SPECIAL";
        }
    }
}
