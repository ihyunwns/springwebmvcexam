package com.hyunwns.demoweb.animal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CrawlStatus {

    // PRIMARY KEY
    private String category;

    private int last_page;

    private LocalDateTime updated_at;

}
