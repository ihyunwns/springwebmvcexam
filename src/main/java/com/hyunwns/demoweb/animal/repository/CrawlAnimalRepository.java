package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.domain.CrawlStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CrawlAnimalRepository {

    CrawlAnimal findLatestAnimal(String category) throws SQLException;

    void insertCrawlAnimal(String keyword, CrawlAnimal crawlAnimal) throws SQLException;

    Optional<CrawlStatus> getCrawlStatus(String category) throws SQLException;

    void updateCrawlStatus(String category, int last_page) throws SQLException;

    LocalDateTime getLastUpdatedDate() throws SQLException;

    List<CrawlAnimal> getCrawlAnimals(String category, int count) throws SQLException;
}
