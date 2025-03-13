package com.hyunwns.demoweb.animal.service;

import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.repository.CrawlAnimalRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebCrawlingService {

    private final ChromeOptions chromeOptions;
    private final Logger logger = LoggerFactory.getLogger(WebCrawlingService.class);
    public static String BASE_CRAWLING_URL = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=";

    private static final int PAGE_GROUP_SIZE = 10;
    private static final int MAX_THREAD_POOL = 3;

    private final CrawlAnimalRepository animalRepository;

    public void syncAnimalData() throws SQLException {

        String[] keywords = { "강아지", "고양이", "기타 반려동물" };

        // main 스레드가 아닌 새로운 스레드로 크롤링 서비스 실행 > 그 안에서 다시 크롤링을 위한 스레드가 생성 되는 것

        int i = 0;
        try {
            while (true) {
                if (i == 20) {
                    break;
                }

                log.info(Arrays.toString(keywords));
                Thread.sleep(1000);
                i++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*new Thread(() -> {
            log.info(Arrays.toString(keywords));
        }).start();*/

        // 문제점은 갱신 시도마다 스레드가 새롭게 생성됨, 오버헤드 발생 가능성 있음

        // 1. 스프링의 Async 어노테이션 활용
        // 2. ExecutorService를 활용해서 스레드 풀 관리

    }

    private BlockingQueue<int[]> createTaskQueue(int diff_page) throws InterruptedException {
        BlockingQueue<int[]> taskQueue = new LinkedBlockingQueue<>();

        int tasks = diff_page / PAGE_GROUP_SIZE + 1;

        for(int i = 0; i < tasks; i++) {
            int startPage = i * PAGE_GROUP_SIZE + 1;
            int endPage = Math.min(startPage + PAGE_GROUP_SIZE - 1, diff_page + 1);

            int[] task = new int[]{startPage, endPage};

            taskQueue.put(task);
        }
        return taskQueue;
    }

    private int getLastPage(List<WebElement> elements) {

        int LAST_PAGE = 0;
        if (!elements.isEmpty()) {
            WebElement parent = elements.get(0);
            String tagName = parent.getTagName();

            if (tagName.equals("a")) {
                String href = parent.getDomAttribute("href");
                if (href != null && href.contains("page=")) {
                    LAST_PAGE = Integer.parseInt(href.split("page=")[1]);
                }
            } else if (tagName.equals("td")) {
                List<WebElement> last = parent.findElements(By.xpath("./a[last()]"));
                String href = last.get(0).getDomAttribute("href");
                if (href != null && href.contains("page=")) {
                    LAST_PAGE = Integer.parseInt(href.split("page=")[1]);
                }
            }
            logger.info("LAST_PAGE: {}", LAST_PAGE);
        }

        return LAST_PAGE;
    }
}
