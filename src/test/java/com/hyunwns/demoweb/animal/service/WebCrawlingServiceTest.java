package com.hyunwns.demoweb.animal.service;

import com.hyunwns.demoweb.animal.TestConfig;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.exception.CrawlingException;
import com.hyunwns.demoweb.animal.repository.CrawlAnimalRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

import static com.hyunwns.demoweb.animal.service.WebCrawlingService.BASE_CRAWLING_URL;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class WebCrawlingServiceTest {

    @Autowired
    private CrawlAnimalRepository animalRepository;

    private final Logger logger = LoggerFactory.getLogger(WebCrawlingServiceTest.class);
    private static final int MAX_THREAD_POOL = 2;

    ChromeOptions options = new ChromeOptions();

    @BeforeEach
    void setupClass() {
        WebDriverManager.chromedriver().setup();

        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--headless");

        options.setPageLoadTimeout(Duration.ofMinutes(5)); // 페이지 로드 타임아웃 5분
        options.setScriptTimeout(Duration.ofSeconds(60));  // 스크립트 타임아웃 60초

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
        BlockingQueue<int[]> taskQueue = new LinkedBlockingQueue<>();

        List<String> keywords = List.of(
                "강아지", "고양이", "기타 반려동물"
        );

        for(String keyword : keywords) {

            WebDriver driver = new ChromeDriver(options);
            String beginUrl = BASE_CRAWLING_URL + keyword + "&page=1";
            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_POOL);

            try{
                driver.get(beginUrl);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[@src='../images/arrow-bb.gif']/..")));
                int LAST_PAGE = getLastPage(elements);

                taskQueue.clear();
                taskQueue = createTaskQueue(LAST_PAGE, PAGE_GROUP_SIZE);

                // 키워드별 taskQueue가 독립적으로 존재해야 서로 다른 키워드를 작업중인 스레드가 영향을 끼치지 않는다. 근데 나는 그걸 기대하고 만든 게 아닌데..
                // 키워드별로 순차적으로 진행하되 이 키워드 별 크롤링을 스레드를 이용해서 여러 페이지를 동시에 크롤링 하고자 한것.

                for(int i = 0; i < MAX_THREAD_POOL; i++) {
                    executor.submit(new CrawlerThread(options, taskQueue, keyword, null));
                }

                executor.shutdown();
                // 모든 스레드가 끝날 때까지 기다리기
                while (!executor.isTerminated()) {
                    Thread.sleep(2000); // 2초마다 체크
                    logger.info("키워드 '{}' 작업 진행 중... 남은 큐 크기: {}", keyword, taskQueue.size());
                }
                
                logger.info("키워드 '{}' 크롤링 완료", keyword);

            }catch (Exception e) {
                logger.error("키워드 {} 처리 중 오류 발생: {}", keyword, e.getMessage());
            }finally {
                driver.quit();
            }
        }

        logger.info("모든 키워드 크롤링 완료");
    }

    @Test
    public void 특정_페이지_포스터_요소_테스트() throws InterruptedException, CrawlingException {
        String url = BASE_CRAWLING_URL + "강아지" + "&page=1";
        int post = 2; // 1 ~ 30

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        List<WebElement> table = driver.findElements(By.xpath("//table[@background=\"../images/board/main-search-img-frame-01.gif\"]//tr[2]/td//font[normalize-space(text())]"));
        // normalize-space(text()) : 태그의 텍스트를 가져와 공백을 제거한 후 비어있지 않은 경우만 선택

        String originalWindow = driver.getWindowHandle();

        WebElement webElement = table.get(post-1);
        Thread.sleep(400);

        webElement.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        Set<String> windowHandles = driver.getWindowHandles(); //현재 열려있는 창
        windowHandles.remove(originalWindow);

        if(!windowHandles.isEmpty()) {
            String newWindowHandle = windowHandles.iterator().next();
            driver.switchTo().window(newWindowHandle);
        }

        //List<WebElement> imgElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[contains(@src, '/pet_care/photo/')]")));
        List<WebElement> imgElement = driver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));
        if (imgElement.isEmpty()) {
            logger.info("이미지를 찾지 못함, 게시물: {}, URL: {}", post, driver.getCurrentUrl());
        }
        List<WebElement> infoElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//b")));
        logger.info("b태그 크기: {}", infoElement.size());
        for(WebElement we : infoElement) {
            logger.info("b: {}", we.getText());
        }

        Map<String, String> crawlingData = getStringMap(infoElement, imgElement);
        logger.info("crawlingData: {}", crawlingData);

        driver.close();
        driver.switchTo().window(originalWindow);


        driver.quit();
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
        System.out.println(imgElement.size());

        if (!imgElement.isEmpty()) {
            crawlingData.put("src", imgElement.get(0).getDomAttribute("src"));
        } else {
            crawlingData.put("src", "Not Found");
        }

        if (infoElement.size() == 7) {
            crawlingData.put("phone", infoElement.get(0).getText().substring(5).replace(" ", ""));
            crawlingData.put("address", infoElement.get(1).getText());
            crawlingData.put("date", infoElement.get(2).getText());
            crawlingData.put("title", infoElement.get(3).getText());
            crawlingData.put("gender", infoElement.get(5).getText());
            crawlingData.put("details", infoElement.get(6).getText());
        } else {
            crawlingData.put("phone", infoElement.get(0).getText().substring(5).replace(" ", ""));
            crawlingData.put("gratuity", infoElement.get(1).getText());
            crawlingData.put("address", infoElement.get(2).getText());
            crawlingData.put("date", infoElement.get(3).getText());
            crawlingData.put("title", infoElement.get(4).getText());
            crawlingData.put("gender", infoElement.get(6).getText());
            crawlingData.put("details", infoElement.get(7).getText());
        }
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