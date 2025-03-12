package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.domain.CrawlStatus;

import java.sql.SQLException;
import java.util.Optional;

public interface CrawlAnimalRepository {

    CrawlAnimal findLatestAnimal(String keyword) throws SQLException;

    void insertCrawlAnimal(String keyword, CrawlAnimal crawlAnimal) throws SQLException;

    Optional<CrawlStatus> getCrawlStatus(String category) throws SQLException;

    void updateCrawlStatus(String category, int last_page) throws SQLException;

}
