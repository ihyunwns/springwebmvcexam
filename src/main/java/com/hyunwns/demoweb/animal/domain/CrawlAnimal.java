package com.hyunwns.demoweb.animal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrawlAnimal that = (CrawlAnimal) o;
        return Objects.equals(title, that.title) && Objects.equals(details, that.details) && Objects.equals(imgURL, that.imgURL) && Objects.equals(gender, that.gender) && Objects.equals(gratuity, that.gratuity) && Objects.equals(address, that.address) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, details, imgURL, gender, gratuity, address, phoneNumber, date);
    }
}
