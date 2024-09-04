package com.hyunwns.demoweb.service;

import com.hyunwns.demoweb.dto.SignUpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InputVerificationService {

    private final MemberService memberService;

    @Autowired
    public InputVerificationService(MemberService memberService) {
        this.memberService = memberService;
    }

    public SignUpDTO verification(SignUpDTO signUpDTO) {

        String id = signUpDTO.getId().toLowerCase();
        if (memberService.isMemberExist(id)) {
            throw new RuntimeException("Member with id " + id + " already exists");
        }

        if (!IdVerification(id)) {
            throw new RuntimeException("Invalid id " + id);
        }

        String nickname = signUpDTO.getNickname();
        if(!NicknameVerification(nickname)) {
            throw new RuntimeException("Invalid nickname " + nickname);
        }

        String password = signUpDTO.getPassword();
        if(!PasswordVerification(password)) {
            throw new RuntimeException("Invalid password " + password);
        }

        String passwordC = signUpDTO.getPassword_C();
        if(!password.equals(passwordC)) {
            throw new RuntimeException("Invalid passwordC " + passwordC);
        }

        return signUpDTO;
    }

    //ID 문자열 정규식 검증, 영어, 숫자만 가능
    private boolean IdVerification(String id) {

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
    private boolean NicknameVerification(String nickname) {
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
    private boolean PasswordVerification(String password) {
        if(password.length() <= 7 || password.length() > 16) {
            return false;
        }

        char[] specialChar = {33, 64, 35, 36, 37, 94, 126, 38, 42};
        int specialCount = 0;
        int numberCount = 0;

        String[] words = password.split("");
        for(String word : words) {
            char uniChar = word.charAt(0);
            // 영어 / 숫자가 아닌데
            int count = 0;
            if (!((uniChar >= 97 && uniChar <= 122) || (uniChar >= 65 && uniChar <= 90) || (uniChar >= 48 && uniChar <= 57))) {
                for (char c : specialChar) {
                    if (uniChar == c) {
                        specialCount++;
                        break;
                    }
                    count++;
                }
                // 특정 특수문자를 발견을 못한 경우
                if (count == 9) {
                    return false;
                }
            } else {
                // 숫자 혹은 영어가 넘어왔는데 숫자인 경우
                if (Character.isDigit(uniChar)) {
                    numberCount++;
                }
            }
        }

        return specialCount >= 1 && numberCount >= 2;
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
