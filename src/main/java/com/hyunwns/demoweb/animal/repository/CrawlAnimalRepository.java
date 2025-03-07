package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.domain.CrawlAnimal;

import java.sql.SQLException;

public interface CrawlAnimalRepository {

    CrawlAnimal findLatestAnimal(String keyword) throws SQLException;
}
