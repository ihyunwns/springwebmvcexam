/*
package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.domain.AnimalType;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.domain.CrawlStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.SQLException;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
@Slf4j
public class H2nJDBCCrawlAnimalRepository implements CrawlAnimalRepository {

    //private final JdbcTemplate jdbcTemplate;

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

        jdbcTemplate.update(sql,

                crawlAnimal.getTitle(), crawlAnimal.getDetails(), crawlAnimal.getImgURL(), crawlAnimal.getGender(),
                crawlAnimal.getGratuity(), crawlAnimal.getAddress(), crawlAnimal.getPhoneNumber(), crawlAnimal.getDate());

    }

    @Override
    public Optional<CrawlStatus> getCrawlStatus(String category) throws SQLException, EmptyResultDataAccessException {

        String sql = "SELECT * FROM crawl_status WHERE category = ?";
        try{
            CrawlStatus crawlStatus = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(CrawlStatus.class), category);
            return Optional.ofNullable(crawlStatus);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public void updateCrawlStatus(String category, int last_page) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM crawl_status WHERE CATEGORY = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, category);

        if (count != null && count > 0) {
            String updateSql = "UPDATE crawl_status SET LAST_PAGE = ? WHERE CATEGORY = ?";
            jdbcTemplate.update(updateSql, last_page, category);
        } else {
            String insertSql = "INSERT INTO crawl_status (CATEGORY, LAST_PAGE) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, category, last_page);
        }
    }
}
*/
