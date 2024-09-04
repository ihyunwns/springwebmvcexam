package com.hyunwns.demoweb.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class InputVerificationServiceTest {

    @Test
    public void 아이디_검증_테스트() throws Exception{
        //given
        String id = "Asadㅇ";
        String[] words = id.split("");

        //when
        for(String word : words){
            char uniChar = word.charAt(0);
            if ( !((uniChar >= 97 && uniChar <= 122) || (uniChar >= 65 && uniChar <= 90) || (uniChar >= 48 && uniChar <= 57)) ) {
                System.out.println(uniChar + "X");
                return;
            }
        }

        System.out.println("검증 완료");
        

    }

    @Test
    public void 한글_자음_모음_나누기() throws Exception{
        // 한글 유니코드 = ( 초성 * 21 + 중성 + 28 + 종성 + 0xAC00 )
        String[] initialConsonants = {"ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ",
                                        "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ",
                                        "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};
        String[] medialConsonants = {"ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ",
                                    "ㅖ", "ㅗ", "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ",
                                    "ㅝ", "ㅞ", "ㅟ", "ㅠ", "ㅡ", "ㅢ", "ㅣ"};
        String[] finalConsonants = {"", "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ",
                                    "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ", "ㄾ",
                                    "ㄿ", "ㅀ", "ㅁ", "ㅂ", "ㅄ", "ㅅ", "ㅆ",
                                    "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};

        // 초성 = ( 유니코드 - 0xAC00 / 28 / 21 )

        //given

        String input = "안";
        char index = input.charAt(0);

        if (index >= 0xAC00) {
            char f = (char) ((index - 0xAC00) / 28 / 21);
            char s = (char) ((index - 0xAC00) / 28 % 21);
            char t = (char) ((index - 0xAC00) % 28);

            //when
            System.out.println(initialConsonants[f]);
            System.out.println(medialConsonants[s]);
            System.out.println(finalConsonants[t]);

            Assertions.assertEquals("ㅇ", initialConsonants[f]);
            Assertions.assertEquals("ㅏ", medialConsonants[s]);
            Assertions.assertEquals("ㄴ", finalConsonants[t]);

        } else {
            Assertions.assertEquals('ㄴ', index);
        }


        //then
    }

    @Test
    public void 한글_감지() throws Exception{
        //given
        String text = "aㄴ안@aㅁ2";

        char unicode = 0x3133; // 0xAC00: 가   0xD7A3: 힣
                                // 0x1100: ㄱ 0x11FF: ㄴㄴ
                                // 0x3130


        for(char c : text.toCharArray()){
            if ( (c >= 97 && c <= 122) || (c >= 65 && c <= 90) || (c >= 48 && c <= 57) ) {
                System.out.println(c + " English or Number");
            } else if ((c >= 0xAC00 && c <= 0xD7A3) || (c >= 0x1100 && c <= 0x11FF) || (c >= 0x3130 && c <= 0x318F)) {
                System.out.println(c + " Korean");
            } else {
                System.out.println(c + " Special Character");
            }
        }

    }

    @Test
    public void 비밀번호_검증() throws Exception{
        
        //given
        char[] specialChar = {33, 64, 35, 36, 37, 94, 126, 38, 42};
        String input = "123456";

        int specialCount = 0;
        int numberCount = 0;
        for(char uniChar : input.toCharArray()){
            // 영어 / 숫자가 아닌데
            int count = 0;
            if (!((uniChar >= 97 && uniChar <= 122) || (uniChar >= 65 && uniChar <= 90) || (uniChar >= 48 && uniChar <= 57))) {
                // 특정 특수문자가 아닌 경우
                for (char c : specialChar) {
                    if (uniChar == c) {
                        specialCount++;
                        break;
                    }
                    count++;
                }
                //끝까지 발견을 못한 경우
                if (count == 9) {
                    System.out.println("검증 실패");
                    return;
                }
            } else {
                if (Character.isDigit(uniChar)) {
                    numberCount++;
                }
            }
        }

        if (specialCount >= 1) {
            if (numberCount >= 2) {
                System.out.println("검증 성공");
                return;
            }
        }

        System.out.println("검증 실패");

    }

}

