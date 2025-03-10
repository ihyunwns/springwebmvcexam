package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.config.AnimalDatabaseConfig;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AnimalDatabaseConfig.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class H2nJDBCCrawlAnimalRepositoryTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void findLatestAnimal() {
        String sql = "SELECT * FROM dog ORDER BY id DESC LIMIT 1";

        List<CrawlAnimal> query = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CrawlAnimal.class));

        for(CrawlAnimal animal : query) {
            System.out.println(animal.toString());
        }
    }

    @Test
    public void insertCrawlAnimal() throws Exception{
        //given
        String sql = "INSERT INTO dog (title, details, imgurl, gender, gratuity, address, phonenumber) VALUES (?, ?, ?, ?, ?, ?, ?)";

        CrawlAnimal crawlAnimal = new CrawlAnimal();

        crawlAnimal.setTitle("TEST");

        jdbcTemplate.update(sql, crawlAnimal.getTitle(), crawlAnimal.getDetails(), crawlAnimal.getImgURL(), crawlAnimal.getGender()
        , crawlAnimal.getGratuity(), crawlAnimal.getAddress(), crawlAnimal.getPhoneNumber()
        );

        Assertions.assertEquals(2, jdbcTemplate.queryForObject("select count(*) from dog", Integer.class));
    }

    @Test
    public void deleteCrawlAnimal() throws Exception{
        //given
        String sql = "DELETE FROM dog WHERE id = ?";
        String latest = "SELECT * FROM dog ORDER BY id DESC LIMIT 1";
        Integer id = jdbcTemplate.queryForObject(latest, new BeanPropertyRowMapper<>(CrawlAnimal.class)).getId().intValue();

        jdbcTemplate.update(sql, id);

    }


}