package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.domain.CrawlStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CrawlAnimalRepository {

    void insertCrawlAnimal(String type, CrawlAnimal crawlAnimal) throws SQLException;

    Optional<CrawlStatus> getCrawlStatus(String type) throws SQLException;

    void updateCrawlStatus(String type, int last_page) throws SQLException;

    void updateLastUpdatedDate() throws SQLException;

    LocalDateTime getLastUpdatedDate() throws SQLException;

    List<CrawlAnimal> getCrawlAnimals(String type, int count) throws SQLException;

    CrawlAnimal getCrawlAnimal(int id) throws SQLException;

}
