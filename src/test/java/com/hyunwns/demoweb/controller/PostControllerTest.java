package com.hyunwns.demoweb.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PostControllerTest {

    @Test
    public void 날짜_변환() throws Exception{
        //given
        LocalDateTime now = LocalDateTime.now();

        //when
        String date = now.toString().split("\\.")[0];
        String replace = date.replace("T", " ");

        //then
        System.out.println(replace);
    }

    @Test
    public void 본문_내용_테스트() throws Exception{
        //given
        String content = "abcdefg                @@@\n" +
                "\n" +
                "\n" +
                "\n" +
                "!!!!!!\n" +
                "\n" +
                "\n" +
                "ksnasad !@                  Aa";


        System.out.println(content);
        //when

        //then
    }

}