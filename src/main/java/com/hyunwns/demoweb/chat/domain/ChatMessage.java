package com.hyunwns.demoweb.chat.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ChatMessage {

    private final String id;
    private final String message;
    private final int code;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime dateTime; // 메시지 생성 시간, objectMapper로 변환 시 에러 발생

    @Setter
    private List<String> images; //Base64로 인코딩된 이미지 리스트

    public ChatMessage(String id, String message, int code, List<String> images ) {
        this.id = id;
        this.message = message;
        this.code = code;
        this.dateTime = LocalDateTime.now();
        this.images = images;
    }
}
