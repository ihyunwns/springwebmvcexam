package com.hyunwns.demoweb.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class Member {

    private final String id;
    private final String nickname;
    private final String password;
    private final int age;

    public Member(String id, String nickname, String password, int age) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.age = age;
    }

}
