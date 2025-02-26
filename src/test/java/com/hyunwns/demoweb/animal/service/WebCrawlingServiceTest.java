package com.hyunwns.demoweb.animal.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

import static com.hyunwns.demoweb.animal.service.WebCrawlingService.BASE_CRAWLING_URL;

@Slf4j
class WebCrawlingServiceTest {
    private final Logger logger = LoggerFactory.getLogger(WebCrawlingServiceTest.class);
    private static final int MAX_THREAD_POOL = 1;

    ChromeOptions options = new ChromeOptions();

    @BeforeEach
    void setupClass() {
        WebDriverManager.chromedriver().setup();

        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        //options.addArguments("--headless");

    }

    @Test
    void crawlAbandonedAnimalDogTest() {
        WebDriver driver = new ChromeDriver(options);
        String url = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=강아지&page=1";

        try {
            driver.get(url);

            List<WebElement> table = driver.findElements(By.xpath("//table[@background=\"../images/board/main-search-img-frame-01.gif\"]//tr[2]/td//font[normalize-space(text())]"));
            // normalize-space(text()) : 태그의 텍스트를 가져와 공백을 제거한 후 비어있지 않은 경우만 선택

            String originalWindow = driver.getWindowHandle();
            logger.info("original window: {}", originalWindow);

            Assertions.assertEquals(30, table.size());
            for(WebElement we : table) {
                String text = we.getText().trim();
                if (text.isEmpty() || we.getText().contains("찾았어요")) {
                    continue;
                }

                we.click();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.numberOfWindowsToBe(2));

                Thread.sleep(400);
                Set<String> windowHandles = driver.getWindowHandles(); //현재 열려있는 창
                windowHandles.remove(originalWindow);

                if(!windowHandles.isEmpty()) {
                    String newWindowHandle = windowHandles.iterator().next();
                    driver.switchTo().window(newWindowHandle);
                }

                List<WebElement> imgElement = driver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));
                List<WebElement> infoElement = driver.findElements(By.xpath("//b"));

                Map<String, String> crawlingData = getStringMap(infoElement, imgElement);
                logger.info("crawlingData: {}", crawlingData);
                logger.info("-----------------");


                driver.close();
                driver.switchTo().window(originalWindow);
            }

            driver.quit();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void crawlAbandonedAnimalCatTest() throws Exception{
        String url = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=고양이&page=1";
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(url);

            List<WebElement> table = driver.findElements(By.xpath("//table[@background=\"../images/board/main-search-img-frame-01.gif\"]//tr[2]/td//font[normalize-space(text())]"));
            // normalize-space(text()) : 태그의 텍스트를 가져와 공백을 제거한 후 비어있지 않은 경우만 선택

            String originalWindow = driver.getWindowHandle();
            logger.info("original window: {}", originalWindow);

            Assertions.assertEquals(30, table.size());
            for(WebElement we : table) {
                String text = we.getText().trim();
                if (text.isEmpty() || we.getText().contains("찾았어요")) {
                    continue;
                }

                we.click();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.numberOfWindowsToBe(2));

                Thread.sleep(400);
                Set<String> windowHandles = driver.getWindowHandles(); //현재 열려있는 창
                windowHandles.remove(originalWindow);

                if(!windowHandles.isEmpty()) {
                    String newWindowHandle = windowHandles.iterator().next();
                    driver.switchTo().window(newWindowHandle);
                }

                List<WebElement> imgElement = driver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));
                List<WebElement> infoElement = driver.findElements(By.xpath("//b"));

                Map<String, String> crawlingData = getStringMap(infoElement, imgElement);
                logger.info("crawlingData: {}", crawlingData);

                logger.info("-----------------");


                driver.close();
                driver.switchTo().window(originalWindow);
            }

            driver.quit();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void crawlAbandonedEtcAnimalTest() throws Exception{
        String url = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=기타 반려동물&page=1";
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(url);

            List<WebElement> table = driver.findElements(By.xpath("//table[@background=\"../images/board/main-search-img-frame-01.gif\"]//tr[2]/td//font[normalize-space(text())]"));
            // normalize-space(text()) : 태그의 텍스트를 가져와 공백을 제거한 후 비어있지 않은 경우만 선택

            String originalWindow = driver.getWindowHandle();
            logger.info("original window: {}", originalWindow);

            Assertions.assertEquals(30, table.size());
            for(WebElement we : table) {
                String text = we.getText().trim();
                if (text.isEmpty() || we.getText().contains("찾았어요")) {
                    continue;
                }

                we.click();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.numberOfWindowsToBe(2));

                Thread.sleep(400);
                Set<String> windowHandles = driver.getWindowHandles(); //현재 열려있는 창
                windowHandles.remove(originalWindow);

                if(!windowHandles.isEmpty()) {
                    String newWindowHandle = windowHandles.iterator().next();
                    driver.switchTo().window(newWindowHandle);
                }

                List<WebElement> imgElement = driver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));
                List<WebElement> infoElement = driver.findElements(By.xpath("//b"));

                Map<String, String> crawlingData = getStringMap(infoElement, imgElement);
                logger.info("crawlingData: {}", crawlingData);

                logger.info("-----------------");


                driver.close();
                driver.switchTo().window(originalWindow);
            }

            driver.quit();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void 통합_크롤링_테스트() throws Exception{
        List<String> keyword = List.of(
                "강아지", "고양이", "기타 반려동물"
        );
        WebDriver driver = new ChromeDriver(options);

        for(String k : keyword) {

           String url = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=" + k + "&page=1";

            try {
                driver.get(url);

                List<WebElement> table = driver.findElements(By.xpath("//table[@background=\"../images/board/main-search-img-frame-01.gif\"]//tr[2]/td//font[normalize-space(text())]"));
                // normalize-space(text()) : 태그의 텍스트를 가져와 공백을 제거한 후 비어있지 않은 경우만 선택

                String originalWindow = driver.getWindowHandle();
                logger.info("original window: {}", originalWindow);

                Assertions.assertEquals(30, table.size());
                for(WebElement we : table) {
                    String text = we.getText().trim();
                    if (text.isEmpty() || we.getText().contains("찾았어요")) {
                        continue;
                    }

                    we.click();
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.numberOfWindowsToBe(2));

                    Thread.sleep(400);
                    Set<String> windowHandles = driver.getWindowHandles(); //현재 열려있는 창
                    windowHandles.remove(originalWindow);

                    if(!windowHandles.isEmpty()) {
                        String newWindowHandle = windowHandles.iterator().next();
                        driver.switchTo().window(newWindowHandle);
                    }

                    List<WebElement> imgElement = driver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));
                    List<WebElement> infoElement = driver.findElements(By.xpath("//b"));

                    Map<String, String> crawlingData = getStringMap(infoElement, imgElement);
                    logger.info("crawlingData: {}", crawlingData);

                    logger.info("-----------------");


                    driver.close();
                    driver.switchTo().window(originalWindow);
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        driver.quit();
    }

    @Test
    public void getLastPageTest() throws Exception{
        List<String> keyword = List.of(
                "강아지", "고양이", "기타 반려동물"
        );
        WebDriver driver = new ChromeDriver(options);
        for(String k : keyword) {

            String url = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=" + k + "&page=1";


            try {
                driver.get(url);

                int lastPage = 0;
                List<WebElement> elements = driver.findElements(By.xpath("//img[@src='../images/arrow-bb.gif']/.."));
                if (!elements.isEmpty()) {
                    WebElement parent = elements.get(0);
                    String tagName = parent.getTagName();

                    if (tagName.equals("a")) {
                        String href = parent.getDomAttribute("href");
                        if (href != null && href.contains("page=")) {
                            lastPage = Integer.parseInt(href.split("page=")[1]);
                        }
                    } else if (tagName.equals("td")) {
                        List<WebElement> last = parent.findElements(By.xpath("./a[last()]"));
                        String href = last.get(0).getDomAttribute("href");
                        if (href != null && href.contains("page=")) {
                            lastPage = Integer.parseInt(href.split("page=")[1]);
                        }
                    }
                    logger.info("lastPage: {}", lastPage);
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Test
    public void createTaskQueueTest() throws Exception{
        WebDriver driver = new ChromeDriver(options);
        int PAGE_GROUP_SIZE = 10;
        BlockingQueue<int[]> taskQueue;

        String BASE_CRAWLING_URL = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=";
        List<String> keywords = List.of(
                "강아지", "고양이", "기타 반려동물"
        );
        for(String keyword : keywords) {

            String url = BASE_CRAWLING_URL + keyword + "&page=1";

            try {
                driver.get(url);

                List<WebElement> elements = driver.findElements(By.xpath("//img[@src='../images/arrow-bb.gif']/.."));
                int LAST_PAGE = getLastPage(elements);

                taskQueue = createTaskQueue(LAST_PAGE, PAGE_GROUP_SIZE);
                logger.info("taskQueue: {}", Arrays.deepToString(taskQueue.toArray()));

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Test
    public void MultiThreadTest() throws Exception{
        int PAGE_GROUP_SIZE = 10;
        BlockingQueue<int[]> taskQueue;

        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_POOL);

        List<String> keywords = List.of(
                "강아지", "고양이", "기타 반려동물"
        );

        for(String keyword : keywords) {

            WebDriver driver = new ChromeDriver(options);
            String beginUrl = BASE_CRAWLING_URL + keyword + "&page=1";

            try{
                driver.get(beginUrl);

                List<WebElement> elements = driver.findElements(By.xpath("//img[@src='../images/arrow-bb.gif']/.."));
                int LAST_PAGE = getLastPage(elements);

                taskQueue = createTaskQueue(LAST_PAGE, PAGE_GROUP_SIZE);
                for(int i = 0; i < MAX_THREAD_POOL; i++) {
                    executor.submit(new CrawlerThread(options, taskQueue, keyword));
                }

            }catch (Exception e) {
                logger.error(e.getMessage());
            }finally {
                driver.quit();
            }

        }

        executor.shutdown();
        try {
            if(!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("일부 작업이 완료되지 않음. 강제 종료 시도");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("쓰레드 인터럽트 발생");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private BlockingQueue<int[]> createTaskQueue(int LAST_PAGE, int PAGE_GROUP_SIZE) throws InterruptedException {
        BlockingQueue<int[]> taskQueue = new LinkedBlockingQueue<>();
        int tasks = LAST_PAGE / PAGE_GROUP_SIZE + 1;

        for(int i = 0; i < tasks; i++) {
            int startPage = i * PAGE_GROUP_SIZE + 1;
            int endPage = Math.min(startPage + PAGE_GROUP_SIZE - 1, LAST_PAGE);

            int[] task = new int[]{startPage, endPage};

            taskQueue.put(task);
        }
        return taskQueue;
    }

    private Map<String, String> getStringMap(List<WebElement> infoElement, List<WebElement> imgElement) {
        Map<String, String> crawlingData = new HashMap<>();

        if (!imgElement.isEmpty()) {
            crawlingData.put("src", imgElement.get(0).getDomAttribute("src"));
        } else {
            crawlingData.put("src", "Not Found");
        }

        crawlingData.put("phone", infoElement.get(0).getText().substring(5).replace(" ", ""));
        crawlingData.put("address", infoElement.get(1).getText());
        crawlingData.put("date", infoElement.get(2).getText());
        crawlingData.put("title", infoElement.get(3).getText());
        crawlingData.put("gender", infoElement.get(5).getText());
        crawlingData.put("details", infoElement.get(6).getText());

        return crawlingData;
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