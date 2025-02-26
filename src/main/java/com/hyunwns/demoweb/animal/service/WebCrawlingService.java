package com.hyunwns.demoweb.animal.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebCrawlingService {

    private final WebDriver driver;
    private final Logger logger = LoggerFactory.getLogger(WebCrawlingService.class);
    public static String BASE_CRAWLING_URL = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=";

    public List<String> crawlAbandonedAnimal(String keyword) {
        String url = "https://www.zooseyo.or.kr/Yu_board/petfind.html";
        try {
            driver.get(url);

            List<WebElement> table = driver.findElements(By.xpath("//table[@background=\"../images/board/main-search-img-frame-01.gif\"]//tr[2]/td//font[1]"));

            for(WebElement we : table) {
                logger.info("font text: {}", we.getText());
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            driver.quit();
        }

        return new ArrayList<>();
    }
}
