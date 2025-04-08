package com.hyunwns.demoweb.animal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CrawlStatus {

    // PRIMARY KEY
    private String category;

    private int last_page;

}
