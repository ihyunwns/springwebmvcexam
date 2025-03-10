package com.hyunwns.demoweb.animal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
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

    private String date;

    @Override
    public String toString() {
        return "CrawlAnimal{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", imgURL='" + imgURL + '\'' +
                ", gender='" + gender + '\'' +
                ", gratuity='" + gratuity + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
