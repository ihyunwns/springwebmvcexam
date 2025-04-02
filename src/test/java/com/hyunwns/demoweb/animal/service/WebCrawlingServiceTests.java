package com.hyunwns.demoweb.animal.service;

import com.hyunwns.demoweb.animal.TestConfig;
import com.hyunwns.demoweb.animal.domain.AnimalType;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.domain.CrawlStatus;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import static com.hyunwns.demoweb.animal.service.WebCrawlingService.BASE_CRAWLING_URL;
import static org.springframework.util.ClassUtils.isPresent;

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
    void syncAnimalData() throws SQLException {
        String keyword = "강아지";
        String category = AnimalType.fromKeyword(keyword).name();

        WebDriver driver = new ChromeDriver(chromeOptions);
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_POOL);
        List<Future<Map<Integer, List<CrawlAnimal>>>> futures = new ArrayList<>();

        Optional<CrawlAnimal> latestAnimal = Optional.ofNullable(animalRepository.findLatestAnimal(keyword));
        log.info("카테고리 {}의 최신 데이터: {}", category, latestAnimal);

        transactionTemplate.execute(status -> {
            try {
                String url = BASE_CRAWLING_URL + keyword + "&page=1";
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                driver.get(url);
                List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[@src='../images/arrow-bb.gif']/..")));
                int LAST_PAGE = getLastPage(elements);

                int last_page;
                int diff_page;
                Optional<CrawlStatus> crawlStatus = animalRepository.getCrawlStatus(keyword);
                if (crawlStatus.isPresent()) {
                    last_page = crawlStatus.get().getLast_page();
                    diff_page = LAST_PAGE - last_page;
                } else {
                    log.info("INSERT 시도: category={}, last_page={}", category, LAST_PAGE);
                    animalRepository.updateCrawlStatus(keyword, LAST_PAGE);
                    diff_page = LAST_PAGE - 1;
                }

                BlockingQueue<int[]> taskQueue = createTaskQueue(diff_page);
                log.info("Task Queue: {}", Arrays.deepToString(taskQueue.toArray()));
                for (int i = 0; i < MAX_THREAD_POOL; i++) {
                    futures.add(executor.submit(new CrawlerThread(chromeOptions, taskQueue, keyword, latestAnimal.orElse(null))));
                }

                // 실시간 모니터링 및 결과 수집
                Map<Integer, List<CrawlAnimal>> allCrawledAnimals = new TreeMap<>(Comparator.reverseOrder());
                while (!futures.isEmpty()) {
                    Iterator<Future<Map<Integer, List<CrawlAnimal>>>> iterator = futures.iterator();
                    while (iterator.hasNext()) {
                        Future<Map<Integer, List<CrawlAnimal>>> future = iterator.next();
                        if (future.isDone()) {
                            try {
                                allCrawledAnimals.putAll(future.get());
                                iterator.remove(); // 완료된 작업 제거
                            } catch (InterruptedException | ExecutionException e) {
                                log.error("크롤링 작업 중 오류 발생: {}", e.getMessage());
                                executor.shutdownNow(); // 즉시 중단
                                throw new RuntimeException("크롤링 실패로 작업 중단", e);
                            }
                        }
                    }
                    Thread.sleep(2000); // 2초마다 체크
                    log.info("진행 중... 남은 작업: {}, 큐 크기: {}", futures.size(), taskQueue.size());
                }

                // DB 저장
                int crawledSize = 0;
                for (List<CrawlAnimal> animals : allCrawledAnimals.values()) {
                    crawledSize += animals.size();
                }
                log.info("크롤링된 데이터 개수: {}", crawledSize);

                for (List<CrawlAnimal> animals : allCrawledAnimals.values()) {
                    for(CrawlAnimal animal : animals) {
                        animalRepository.insertCrawlAnimal(category, animal);
                    }
                }

                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.MINUTES); // 정리 대기
            } catch (Exception e) {
                log.error("트랜잭션 내 오류 발생: {}", e.getMessage());
                executor.shutdownNow();
                throw new RuntimeException("크롤링 중단 및 롤백", e);
            } finally {
                driver.quit();
            }
            return null;
        });

        log.info("키워드 {} 동기화 완료", keyword);
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

    //43page 5번째와 같은 경우 어떻게 가져올 지 사진이 없는 경우 b 태그가 밀리는 것 같음
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

        for(String key : crawlingData.keySet()) {
            System.out.println(key + ": " + crawlingData.get(key));
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
        } else {
            crawlingData.put("imgURL", "Not Found");
        }

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
        } else if (infoElement.size() == 9) {
            crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
            crawlingData.put("gratuity", infoElement.get(2).getText().split(":")[1].trim());
            crawlingData.put("address", infoElement.get(3).getText());
            crawlingData.put("date", infoElement.get(4).getText());
            crawlingData.put("title", infoElement.get(5).getText());
            crawlingData.put("gender", infoElement.get(7).getText());
            crawlingData.put("details", infoElement.get(8).getText());
        }
        return crawlingData;
    }

}