package com.hyunwns.demoweb.dto;


import com.hyunwns.demoweb.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter @Setter
public class ChatRoomDTO {
/* NotNull로 하면 공백도 문자로 포함되서 에러 감지가 되지 않음 ! */
    @NotBlank(message = "방제목은 필수입니다.")
    private String title;

    private UUID uuid;

    private Member member;

    private int count;

    private boolean isOwner = false;
}
