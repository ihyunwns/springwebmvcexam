package com.hyunwns.demoweb.animal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class LocationInfo {

    private final float x;
    private final float y;

    private final String address_name;

    private String region_depth_1_name;
    private String region_depth_2_name;
    private String region_depth_3_name;

    public LocationInfo(float x, float y, String address_name) {
        this.x = x;
        this.y = y;
        this.address_name = address_name;
    }

    // 임시
    public void setRegionDepth() {
        this.region_depth_1_name = address_name;
        this.region_depth_2_name = region_depth_1_name;
        this.region_depth_3_name = region_depth_2_name;
    }

}
