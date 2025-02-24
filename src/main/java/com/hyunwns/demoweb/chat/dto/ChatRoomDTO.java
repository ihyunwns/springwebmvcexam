package com.hyunwns.demoweb.chat.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter @Setter
public class ChatRoomDTO {
/* NotNull로 하면 공백도 문자로 포함되서 에러 감지가 되지 않음 ! */
    @NotBlank(message = "방제목은 필수입니다.")
    private String title;

    private UUID uuid;

    // 방 소유자 정보
    private String id;
    private String nickname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private int count;

    private boolean isOwner = false;
}
