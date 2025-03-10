package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.domain.AnimalType;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Primary
@Repository
@RequiredArgsConstructor
@Slf4j
public class H2nJDBCCrawlAnimalRepository implements CrawlAnimalRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public CrawlAnimal findLatestAnimal(String keyword) throws SQLException {

        String sql = "SELECT * FROM " + AnimalType.fromKeyword(keyword) + " ORDER BY id DESC LIMIT 1";

        try{
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(CrawlAnimal.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void insertCrawlAnimal(String keyword, CrawlAnimal crawlAnimal) throws SQLException {

        String sql = "INSERT INTO " + AnimalType.fromKeyword(keyword) +
                     "(title, details, imgurl, gender, gratuity, address, phonenumber, date)" +
                     " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, crawlAnimal.getTitle(), crawlAnimal.getDetails(), crawlAnimal.getImgURL(), crawlAnimal.getGender()
        , crawlAnimal.getGratuity(), crawlAnimal.getAddress(), crawlAnimal.getPhoneNumber(), crawlAnimal.getDate()
        );

    }
}
