package com.hyunwns.demoweb.animal.repository;

import com.hyunwns.demoweb.animal.TestConfig;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.domain.CrawlStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class H2nJDBCCrawlAnimalRepositoryTest {

    @Autowired
    private CrawlAnimalRepository crawlAnimalRepository;


    @Test
    void findAnimal() throws SQLException {
        List<CrawlAnimal> latestDog = crawlAnimalRepository.getCrawlAnimals("DOG", 1);

        log.info("{}", latestDog.get(0));

        List<CrawlAnimal> dogs = crawlAnimalRepository.getCrawlAnimals("DOG", 10);
        for(CrawlAnimal dog : dogs) {
            log.info("{}", dog);
        }

    }

    @Test
    public void insertCrawlAnimal() throws Exception{
        CrawlAnimal crawlAnimal = new CrawlAnimal();

        crawlAnimal.setTitle("TEST"); crawlAnimal.setAddress("파담로 113");
        crawlAnimalRepository.insertCrawlAnimal("dog", crawlAnimal);
    }

    @Test
    public void crawlStatusTest() throws Exception{
        //given
        crawlAnimalRepository.updateCrawlStatus("dog", 300);
        crawlAnimalRepository.updateCrawlStatus("dog", 400);
        crawlAnimalRepository.updateCrawlStatus("cat", 500);
        crawlAnimalRepository.updateCrawlStatus("etc", 600);

        Optional<CrawlStatus> dogCrawlStatus = crawlAnimalRepository.getCrawlStatus("dog");
        Optional<CrawlStatus> catCrawlStatus = crawlAnimalRepository.getCrawlStatus("cat");

        Assertions.assertEquals(400, dogCrawlStatus.get().getLast_page());
        Assertions.assertEquals(500, catCrawlStatus.get().getLast_page());

    }

    @Test
    public void updateStatusTest() throws Exception{
        //given
        crawlAnimalRepository.updateLastUpdatedDate();
        LocalDateTime prevTime = crawlAnimalRepository.getLastUpdatedDate();

        Thread.sleep(300);
        crawlAnimalRepository.updateLastUpdatedDate();
        LocalDateTime currentTime = crawlAnimalRepository.getLastUpdatedDate();

        Duration diff = Duration.between(prevTime, currentTime);

        Assertions.assertTrue(diff.toMillis() > 300);

    }


}