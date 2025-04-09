package com.hyunwns.demoweb.animal.repository;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
@Slf4j
public class H2nJDBCCrawlAnimalRepository implements CrawlAnimalRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insertCrawlAnimal(String type, CrawlAnimal crawlAnimal) throws SQLException {

        String sql = "INSERT INTO MISSING_ANIMAL (TYPE, TITLE, DETAILS, IMGURL, GENDER, GRATUITY, LOST_PLACE, PHONENUMBER, LOST_DATE, LATITUDE, LONGITUDE, ADDRESS) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

        jdbcTemplate.update(sql,

                type, crawlAnimal.getTitle(), crawlAnimal.getDetails(), crawlAnimal.getImgURL(), crawlAnimal.getGender(),
                crawlAnimal.getGratuity(), crawlAnimal.getLost_place(), crawlAnimal.getPhoneNumber(), crawlAnimal.getLost_date(),
                crawlAnimal.getLatitude(), crawlAnimal.getLongitude(), crawlAnimal.getAddress());

    }

    @Override
    public Optional<CrawlStatus> getCrawlStatus(String category) throws SQLException, EmptyResultDataAccessException {

        String sql = "SELECT * FROM CRAWL_STATUS WHERE TYPE = ?";
        try{
            CrawlStatus crawlStatus = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(CrawlStatus.class), category);
            return Optional.ofNullable(crawlStatus);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public void updateCrawlStatus(String type, int last_page) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM crawl_status WHERE TYPE = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, type);

        if (count != null && count > 0) {
            String updateSql = "UPDATE crawl_status SET LAST_PAGE = ? WHERE TYPE = ?";

            jdbcTemplate.update(updateSql, last_page, type);
        } else {
            String insertSql = "INSERT INTO crawl_status (TYPE, LAST_PAGE) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, type, last_page);
        }
    }

    @Override
    public void updateLastUpdatedDate() throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM UPDATE_STATUS";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);

        if (count != null && count > 0) {
            String updateSql = "UPDATE UPDATE_STATUS SET LAST_UPDATED = NOW() WHERE ID = 1";

            jdbcTemplate.update(updateSql);
        } else {
            String insertSql = "INSERT INTO UPDATE_STATUS (ID, LAST_UPDATED) VALUES (1, NOW())";

            jdbcTemplate.update(insertSql);
        }
    }

    @Override
    public LocalDateTime getLastUpdatedDate() throws SQLException {
        String sql = "SELECT LAST_UPDATED from UPDATE_STATUS ORDER BY LAST_UPDATED DESC LIMIT 1";

        return jdbcTemplate.queryForObject(sql, LocalDateTime.class);
    }

    @Override
    public List<CrawlAnimal> getCrawlAnimals(String type, int count) throws SQLException {

        String sql;
        if (count == 0) {
            sql = "SELECT * FROM MISSING_ANIMAL WHERE TYPE = '" + type + "' ORDER BY id";
        } else {
            sql = "SELECT * FROM PUBLIC.MISSING_ANIMAL WHERE TYPE = '" + type + "' ORDER BY id DESC LIMIT " + count;
        }
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CrawlAnimal.class));
    }

    @Override
    public CrawlAnimal getCrawlAnimal(int id) throws SQLException {
        String sql = "SELECT * FROM MISSING_ANIMAL WHERE ID = " + id;

        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(CrawlAnimal.class));
    }
}
