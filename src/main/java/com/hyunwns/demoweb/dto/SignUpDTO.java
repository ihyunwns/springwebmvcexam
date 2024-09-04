package com.hyunwns.demoweb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignUpDTO {

    private String id;
    private String nickname;
    private String password;
    private String password_C;
    private int age;

}
