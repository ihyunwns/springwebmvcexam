package com.hyunwns.demoweb.domain.chat;

import lombok.Getter;

@Getter
public class ChatMessage {

    private final String id;
    private final String message;
    private final int code;

    public ChatMessage(String id, String message, int code) {
        this.id = id;
        this.message = message;
        this.code = code;
    }
}
