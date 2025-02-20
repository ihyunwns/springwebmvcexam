package com.hyunwns.demoweb.domain.chat;

import lombok.Getter;

@Getter
public enum ChatCode {
    ENTER(100, "입장"),
    EXIT(101, "퇴장"),
    MESSAGE(200, "전송");

    private final int code;
    private final String description;

    ChatCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ChatCode fromCode(int code) {
        for (ChatCode chatCode : ChatCode.values()) {
            if (chatCode.code == code) {
                return chatCode;
            }
        }
        throw new IllegalArgumentException("Invalid ChatCode: " + code);
    }

}
