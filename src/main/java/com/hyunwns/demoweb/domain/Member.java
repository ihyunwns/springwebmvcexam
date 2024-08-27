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

    private String role;

    public Member(String id, String nickname, String password, int age) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.age = age;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", role='" + role + '\'' +
                '}';
    }
}
