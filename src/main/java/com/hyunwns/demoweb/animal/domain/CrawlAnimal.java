package com.hyunwns.demoweb.animal.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CrawlAnimal {

    /* Auto Increment */
    private Long id;

    private String title;

    private String details;

    private String imgURL;

    private String gender;

    private String gratuity;

    private String address;

    private String phoneNumber;

}
