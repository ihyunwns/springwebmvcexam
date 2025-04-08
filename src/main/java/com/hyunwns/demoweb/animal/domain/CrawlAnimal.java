package com.hyunwns.demoweb.animal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CrawlAnimal {

    /* Auto Increment */
    private Long id;

    private String type;

    private String title;

    private String details;

    private String imgURL;

    private String gender;

    private String gratuity;

    private String lost_place;

    private String phoneNumber;

    private String lost_date;

    private Double latitude;

    private Double longitude;

    private String address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrawlAnimal that = (CrawlAnimal) o;
        return Double.compare(latitude, that.latitude) == 0 && Double.compare(longitude, that.longitude) == 0 && Objects.equals(title, that.title) && Objects.equals(details, that.details) && Objects.equals(imgURL, that.imgURL) && Objects.equals(gender, that.gender) && Objects.equals(gratuity, that.gratuity) && Objects.equals(lost_place, that.lost_place) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(lost_date, that.lost_date) && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, details, imgURL, gender, gratuity, lost_place, phoneNumber, lost_date, latitude, longitude, address);
    }
}
