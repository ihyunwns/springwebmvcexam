package com.hyunwns.demoweb.animal.service;

import com.hyunwns.demoweb.animal.TestConfig;
import com.hyunwns.demoweb.animal.config.AnimalDatabaseConfig;
import com.hyunwns.demoweb.animal.domain.AnimalType;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.repository.CrawlAnimalRepository;
import com.hyunwns.demoweb.animal.repository.H2nJDBCCrawlAnimalRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static com.hyunwns.demoweb.animal.service.WebCrawlingService.BASE_CRAWLING_URL;


@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Component
class WebCrawlingServiceTests {

    private static final int MAX_THREAD_POOL = 2;
    ChromeOptions chromeOptions = new ChromeOptions();

    @Autowired
    private CrawlAnimalRepository animalRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setupClass() {
        WebDriverManager.chromedriver().setup();

        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-popup-blocking");
        chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
//        chromeOptions.addArguments("--headless");

        chromeOptions.setPageLoadTimeout(Duration.ofMinutes(5)); // 페이지 로드 타임아웃 5분
        chromeOptions.setScriptTimeout(Duration.ofSeconds(60));  // 스크립트 타임아웃 60초

    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    void syncAnimalData() throws SQLException {

        String keyword = "강아지";
        String category = AnimalType.fromKeyword(keyword).name();
        WebDriver driver = new ChromeDriver(chromeOptions);
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_POOL);

        Optional<CrawlAnimal> latestAnimal = Optional.ofNullable(animalRepository.findLatestAnimal(keyword));
        log.info("카테고리 {}의 최신 데이터: {}", category, latestAnimal);

        // 가장 마지막 페이지 가져오기
        String url = BASE_CRAWLING_URL + keyword + "&page=1";
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(url);
        List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[@src='../images/arrow-bb.gif']/..")));
        int LAST_PAGE = getLastPage(elements);

        log.info("LAST PAGE: {}", LAST_PAGE);

        String sql = "SELECT last_page FROM crawl_status WHERE category = ?";
        Optional<Integer> lastPage;
        int lastPageValue = 0;
        int diff_page = 0;
        try {
            lastPage = Optional.ofNullable(jdbcTemplate.queryForObject(sql, Integer.class, category));
            lastPageValue = lastPage.orElse(LAST_PAGE);

            diff_page = LAST_PAGE - lastPageValue;

        } catch (EmptyResultDataAccessException e) {
            log.info("INSERT 시도: category={}, last_page={}", category, LAST_PAGE);

            sql = "INSERT INTO crawl_status (category, last_page) VALUES (?, ?)";
            jdbcTemplate.update(sql, category, LAST_PAGE);

            lastPageValue = LAST_PAGE;
            diff_page = LAST_PAGE - 1;

        } finally {
            log.info("카테고리 {}의 마지막 페이지: {}, 페이지 차이: {}", category, lastPageValue, diff_page);

            try {
                BlockingQueue<int[]> taskQueue = createTaskQueue(diff_page);
                log.info(Arrays.deepToString(taskQueue.toArray()));

                for (int i = 0; i < MAX_THREAD_POOL; i++) {
                    executor.submit(new CrawlerThread(animalRepository, chromeOptions, taskQueue, keyword));
                }

                executor.shutdown();
                while (!executor.isTerminated()) {
                    Thread.sleep(2000); // 2초마다 체크
                    log.info("키워드 '{}' 작업 진행 중... 남은 큐 크기: {}", keyword, taskQueue.size());
                }

                log.info("키워드 '{}' 크롤링 완료", keyword);

            } catch (Exception e) {
                log.error("{}", e.getMessage());
            } finally {
                driver.quit();
            }
        }
    }

    @Test
    public void taskQueueTest() throws Exception {
        //given
        BlockingQueue<int[]> taskQueue = createTaskQueue(0);

        log.info(Arrays.deepToString(taskQueue.toArray()));
        int i = 0;
        while(!taskQueue.isEmpty()) {
            int[] task = taskQueue.poll();
            for (int j = task[0]; j <= task[1]; j++) {
                log.info("{}번째 값 {}", i, j);
            }
            i++;
        }


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
        }

        return LAST_PAGE;
    }

    private BlockingQueue<int[]> createTaskQueue(int diff_page) throws InterruptedException {
        BlockingQueue<int[]> taskQueue = new LinkedBlockingQueue<>();

        int PAGE_GROUP_SIZE = 10;
        int tasks = diff_page / PAGE_GROUP_SIZE + 1;

        for(int i = 0; i < tasks; i++) {
            int startPage = i * PAGE_GROUP_SIZE + 1;
            int endPage = Math.min(startPage + PAGE_GROUP_SIZE - 1, diff_page + 1);

            int[] task = new int[]{startPage, endPage};

            taskQueue.put(task);
        }
        return taskQueue;
    }

}