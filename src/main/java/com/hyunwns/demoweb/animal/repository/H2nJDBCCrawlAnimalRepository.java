package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class H2nJDBCCrawlAnimalRepository implements CrawlAnimalRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public CrawlAnimal findLatestAnimal(String keyword) throws SQLException {

        // 임베디드 모드라서 애플리케이션이 실행중일 때만 DB 확인 가능


        return null;
    }
}
