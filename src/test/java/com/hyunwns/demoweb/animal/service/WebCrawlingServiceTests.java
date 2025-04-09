package com.hyunwns.demoweb.animal.service;

import com.google.gson.*;
import com.hyunwns.demoweb.animal.TestConfig;
import com.hyunwns.demoweb.animal.config.KakaoMapConfig;
import com.hyunwns.demoweb.animal.domain.AnimalType;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.domain.CrawlStatus;
import com.hyunwns.demoweb.animal.domain.LocationInfo;
import com.hyunwns.demoweb.animal.exception.CrawlingException;
import com.hyunwns.demoweb.animal.repository.CrawlAnimalRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import static com.hyunwns.demoweb.animal.service.WebCrawlingService.BASE_CRAWLING_URL;


@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class WebCrawlingServiceTests {

    private static final int MAX_THREAD_POOL = 3;
    ChromeOptions chromeOptions = new ChromeOptions();

    @Autowired
    private CrawlAnimalRepository animalRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private KakaoMapService kakaoMapService;
    @Autowired
    private CrawlAnimalRepository crawlAnimalRepository;

    @BeforeEach
    void setupClass() {
        WebDriverManager.chromedriver().setup();

        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-popup-blocking");
        chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        chromeOptions.addArguments("--headless");

        chromeOptions.setPageLoadTimeout(Duration.ofMinutes(5)); // 페이지 로드 타임아웃 5분
        chromeOptions.setScriptTimeout(Duration.ofSeconds(60));  // 스크립트 타임아웃 60초

    }

    @Test
    public void crawlStatusTest() throws Exception{

        String category = AnimalType.fromKeyword("강아지").name();

        Optional<CrawlStatus> crawlStatus = animalRepository.getCrawlStatus(category);
        if (crawlStatus.isPresent()) {
            int last_page = crawlStatus.get().getLast_page();

            log.info("데이터가 있으므로 해당 키워드의 마지막 페이지: {}", last_page);
        } else {
            log.info("데이터가 없으므로 초기값 업데이트");
            animalRepository.updateCrawlStatus(category, 10);
        }

        log.info("마지막으로 진행 한 페이지 및 포스트를 업데이트 진행");

        animalRepository.updateCrawlStatus(category, 12);

    }

    @Test
    public void taskQueueTest() throws Exception {
        //given
        BlockingQueue<int[]> taskQueue = createTaskQueue(11);

        int i = 0;
        while(!taskQueue.isEmpty()) {
            int[] task = taskQueue.poll();
            for (int j = task[0]; j <= task[1]; j++) {
                log.info("{}번째 값 {}", i, j);
            }
            i++;
        }


    }

    @Test
    public void 특정_게시물_크롤링_테스트() throws Exception{
        String page = "&page=43";
        int post = 20;
        String keyword = "강아지";

        //given
        WebDriver driver = new ChromeDriver(chromeOptions);

        String url = BASE_CRAWLING_URL + keyword + page;
        driver.get(url);
        String originalWindow = driver.getWindowHandle();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<WebElement> table = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table[@background='../images/board/main-search-img-frame-01.gif']//tr[2]/td//font[normalize-space(text())]")));

        table.get(post - 1).click();

        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        Set<String> windowHandles = driver.getWindowHandles();
        windowHandles.remove(originalWindow);
        if (!windowHandles.isEmpty()) {
            String newWindowHandle = windowHandles.iterator().next();
            driver.switchTo().window(newWindowHandle);
        } else {
            throw new CrawlingException();
        }

        //List<WebElement> imgElement = webDriver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));

        List<WebElement> imgElement = new ArrayList<>();
        try {
            imgElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[contains(@src, '/pet_care/photo/')]")));
        } catch (TimeoutException e) {
            e.getMessage();
        }

        List<WebElement> infoElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//b")));
        Map<String, String> crawlingData = getStringMap(infoElement, imgElement);

        System.out.println(infoElement.size());
        for(String key : crawlingData.keySet()) {
            System.out.println(key + ": " + crawlingData.get(key));
        }
    }

    @Test
    public void kakao_map_api_test() throws Exception{

        
        LocationInfo locationInfo = kakaoMapService.getLocationInfo("더");

        System.out.println(locationInfo);
        
        
    }

    @Test
    public void crawlingTest() throws Exception{
        //given
        CrawlAnimal crawlAnimal = new CrawlAnimal();

        crawlAnimal.setTitle("Test"); crawlAnimal.setGender("Test"); crawlAnimal.setLost_place("제주 본인 집"); crawlAnimal.setGratuity("TEST"); crawlAnimal.setImgURL("TEST");
        crawlAnimal.setLost_date("TEST"); crawlAnimal.setDetails("TEST"); crawlAnimal.setPhoneNumber("TEST");

        LocationInfo locationInfo = kakaoMapService.getLocationInfo(crawlAnimal.getLost_place());
        crawlAnimal.setLatitude(locationInfo.getX());
        crawlAnimal.setLongitude(locationInfo.getY());
        crawlAnimal.setAddress(locationInfo.getAddress_name());

        crawlAnimalRepository.insertCrawlAnimal("dog", crawlAnimal);

        //then
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

        log.info("tasks: {}", tasks);
        for(int i = 0; i < tasks; i++) {
            int startPage = i * PAGE_GROUP_SIZE + 1;
            int endPage = Math.min(startPage + PAGE_GROUP_SIZE - 1, diff_page + 1);

            int[] task = {startPage, endPage};

            taskQueue.put(task);
        }
        return taskQueue;
    }

    private Map<String, String> getStringMap(List<WebElement> infoElement, List<WebElement> imgElement) {
        Map<String, String> crawlingData = new HashMap<>();

        if (!imgElement.isEmpty()) {
            crawlingData.put("imgURL", imgElement.get(0).getDomAttribute("src"));
            if (infoElement.size() == 7) {
                crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
                crawlingData.put("address", infoElement.get(1).getText());
                crawlingData.put("date", infoElement.get(2).getText());
                crawlingData.put("title", infoElement.get(3).getText());
                crawlingData.put("gender", infoElement.get(5).getText());
                crawlingData.put("details", infoElement.get(6).getText());
            } else if (infoElement.size() == 8) {
                crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
                crawlingData.put("gratuity", infoElement.get(1).getText().split(":")[1].trim());
                crawlingData.put("address", infoElement.get(2).getText());
                crawlingData.put("date", infoElement.get(3).getText());
                crawlingData.put("title", infoElement.get(4).getText());
                crawlingData.put("gender", infoElement.get(6).getText());
                crawlingData.put("details", infoElement.get(7).getText());
            }
        } else {
            crawlingData.put("imgURL", "Not Found");
            if (infoElement.size() == 8) {
                crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
                crawlingData.put("address", infoElement.get(2).getText());
                crawlingData.put("date", infoElement.get(3).getText());
                crawlingData.put("title", infoElement.get(4).getText());
                crawlingData.put("gender", infoElement.get(6).getText());
                crawlingData.put("details", infoElement.get(7).getText());
            } else if (infoElement.size() == 9) {
                crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
                crawlingData.put("gratuity", infoElement.get(2).getText().split(":")[1].trim());
                crawlingData.put("address", infoElement.get(3).getText());
                crawlingData.put("date", infoElement.get(4).getText());
                crawlingData.put("title", infoElement.get(5).getText());
                crawlingData.put("gender", infoElement.get(7).getText());
                crawlingData.put("details", infoElement.get(8).getText());
            }
        }

        return crawlingData;

        }
}