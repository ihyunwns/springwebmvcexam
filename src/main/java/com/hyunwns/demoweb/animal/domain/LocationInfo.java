package com.hyunwns.demoweb.animal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class LocationInfo {

    private final double x;
    private final double y;

    private final String address_name;

    public LocationInfo(double x, double y, String address_name) {
        this.x = x;
        this.y = y;
        this.address_name = address_name;
    }

}
