package com.hyunwns.demoweb.animal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwns.demoweb.animal.domain.AnimalType;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.domain.CrawlStatus;
import com.hyunwns.demoweb.animal.domain.LocationInfo;
import com.hyunwns.demoweb.animal.repository.CrawlAnimalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebCrawlingService {

    public static String BASE_CRAWLING_URL = "https://www.zooseyo.or.kr/Yu_board/petfind.html?area=&animal=";

    private static final int PAGE_GROUP_SIZE = 10;
    private static final int MAX_THREAD_POOL = 3;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final CrawlAnimalRepository animalRepository;
    private final TransactionTemplate transactionTemplate;
    private final ChromeOptions chromeOptions;
    private final SimpMessagingTemplate messagingTemplate;
    private final KakaoMapService kakaoMapService;

    private final String[] keywords = { "강아지", "고양이", "기타 반려동물" };
    private final ObjectMapper objectMapper;

    public boolean syncAnimalData() throws SQLException {
        // 크롤링이 진행중이지 않을 때, 그 후 true로 변경함
        if (running.compareAndSet(false, true)) {
            WebDriver driver = new ChromeDriver(chromeOptions);
            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_POOL);
            List<Future<Map<Integer, List<CrawlAnimal>>>> futures = new ArrayList<>();
            try {

                transactionTemplate.execute(status -> {

                    try {
                        for (String keyword : keywords) {
                            String type = AnimalType.fromKeyword(keyword).name();

                            List<CrawlAnimal> lastAnimal = animalRepository.getCrawlAnimals(type, 1);
                            Optional<CrawlAnimal> latestAnimal = lastAnimal.isEmpty() ? Optional.empty() : Optional.of(lastAnimal.get(0));
                            log.info("카테고리 {}의 최신 데이터: {}", type, latestAnimal);

                            String url = BASE_CRAWLING_URL + keyword + "&page=1";
                            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                            driver.get(url);
                            List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[@src='../images/arrow-bb.gif']/..")));
                            int LAST_PAGE = getLastPage(elements);

                            int last_page;
                            int diff_page;
                            Optional<CrawlStatus> crawlStatus = animalRepository.getCrawlStatus(type);
                            if (crawlStatus.isPresent()) {
                                last_page = crawlStatus.get().getLast_page();
                                diff_page = LAST_PAGE - last_page;
                            } else {
                                log.info("INSERT 시도: type={}, last_page={}", type, LAST_PAGE);
                                animalRepository.updateCrawlStatus(keyword, LAST_PAGE);
                                diff_page = LAST_PAGE - 1;
                            }

                            BlockingQueue<int[]> taskQueue = createTaskQueue(diff_page);
                            double size = taskQueue.size();

                            log.info("size: {}", size);
                            log.info("Task Queue: {}", Arrays.deepToString(taskQueue.toArray()));
                            for (int i = 0; i < MAX_THREAD_POOL; i++) {
                                futures.add(executor.submit(new CrawlerThread(chromeOptions, taskQueue, keyword, latestAnimal.orElse(null))));
                            }

                            // 실시간 모니터링 및 결과 수집
                            Map<Integer, List<CrawlAnimal>> crawledAnimals = new TreeMap<>(Comparator.reverseOrder());
                            while (!futures.isEmpty()) {
                                Iterator<Future<Map<Integer, List<CrawlAnimal>>>> iterator = futures.iterator();
                                while (iterator.hasNext()) {
                                    Future<Map<Integer, List<CrawlAnimal>>> future = iterator.next();
                                    if (future.isDone()) {
                                        try {
                                            crawledAnimals.putAll(future.get());
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
                                double progress = 1 - (taskQueue.size() / size);
                                Map<String, Object> progressData = new HashMap<>();
                                progressData.put("keyword", keyword);
                                progressData.put("progress", progress);
                                progressData.put("isCompleted", false);
                                String progressMessage = objectMapper.writeValueAsString(progressData);

                                messagingTemplate.convertAndSend("/topic/progress", progressMessage);
                            }

                            // DB 저장
                            int crawledSize = 0;
                            for (List<CrawlAnimal> animals : crawledAnimals.values()) {
                                crawledSize += animals.size();
                            }
                            log.info("크롤링된 데이터 개수: {}", crawledSize);

                            // TODO: KakaoMapService로 주소 기반 위도 경도 반환해서 DB에 반영

                            // 그 후 haversine formula 사용

                            for (List<CrawlAnimal> animals : crawledAnimals.values()) {
                                Collections.reverse(animals);
                                for(CrawlAnimal animal : animals) {
                                    LocationInfo locationInfo = kakaoMapService.getLocationInfo(animal.getLost_place());

                                    animal.setAddress(locationInfo.getAddress_name()); animal.setLongitude(locationInfo.getX()); animal.setLatitude(locationInfo.getY());

                                    animalRepository.insertCrawlAnimal(type, animal);
                                }
                            }

                            animalRepository.updateCrawlStatus(type, LAST_PAGE);
                            log.info("키워드 {} 동기화 완료", keyword);

                        }
                    } catch (Exception e) {
                        log.error("트랜잭션 내 오류 발생: {}", e.getMessage());
                        executor.shutdownNow();
                        throw new RuntimeException("크롤링 중단 및 롤백", e);
                    }

                    return null;
                });

                Map<String, Object> progressData = new HashMap<>();

                progressData.put("keyword", null);
                progressData.put("progress", null);
                progressData.put("isCompleted", true);

                String progressMessage = objectMapper.writeValueAsString(progressData);
                messagingTemplate.convertAndSend("/topic/progress", progressMessage);

                animalRepository.updateLastUpdatedDate();
                log.info("모든 키워드 동기화 완료");
                return true;

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                driver.quit();
                executor.shutdown();
                try {
                    executor.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error("Executor 종료 대기 중 오류: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
                running.set(false);
            }
        } else {
            log.info("이미 크롤링이 진행 중 입니다.");
            return false;
        }

    }

    public List<CrawlAnimal> getAnimalData(String type, int count) throws SQLException{
        return animalRepository.getCrawlAnimals(type, count);
    }

    public String getLastUpdatedDate() throws SQLException {

        return animalRepository.getLastUpdatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private BlockingQueue<int[]> createTaskQueue(int diff_page) throws InterruptedException {
        BlockingQueue<int[]> taskQueue = new LinkedBlockingQueue<>();

        int tasks = diff_page / PAGE_GROUP_SIZE + 1;

        for(int i = 0; i < tasks; i++) {
            int startPage = i * PAGE_GROUP_SIZE + 1;
            int endPage = Math.min(startPage + PAGE_GROUP_SIZE - 1, diff_page + 1);

            int[] task = {startPage, endPage};

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
            log.info("LAST_PAGE: {}", LAST_PAGE);
        }

        return LAST_PAGE;
    }

    public boolean getRunningState() {
        return running.get();
    }
}
