package com.hyunwns.demoweb.domain;

import lombok.Getter;

@Getter
public class ChatMessage {

    private final Member member;
    private final String message;

    public ChatMessage(Member member, String message) {
        this.member = member;
        this.message = message;
    }
}
