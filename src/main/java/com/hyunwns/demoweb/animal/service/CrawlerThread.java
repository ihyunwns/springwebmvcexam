package com.hyunwns.demoweb.animal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.exception.CrawlingException;
import com.hyunwns.demoweb.animal.repository.CrawlAnimalRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.hyunwns.demoweb.animal.service.WebCrawlingService.BASE_CRAWLING_URL;

public class CrawlerThread implements Callable<Map<Integer, List<CrawlAnimal>>> {

    private final Logger logger = LoggerFactory.getLogger(CrawlerThread.class);

    private final ChromeOptions chromeOptions;
    private final BlockingQueue<int[]> taskQueue;
    private final String keyword;
    private final CrawlAnimal latestAnimal;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final int MAX_PAGE_LOAD_RETRY = 3;
    private static final int MAX_POST_LOAD_RETRY = 3;
    private static final long BASE_WAIT_TIME = 2000; /* 2000 ms */

    public CrawlerThread(ChromeOptions options, BlockingQueue<int[]> taskQueue, String keyword, CrawlAnimal latestAnimal) {
        this.chromeOptions = options;
        this.taskQueue = taskQueue;
        this.keyword = keyword;
        this.latestAnimal = latestAnimal;
    }

    @Override
    public Map<Integer, List<CrawlAnimal>> call() throws Exception{
        WebDriver webDriver = null;
        Map<Integer, List<CrawlAnimal>> animals = new TreeMap<>();

        try {
            webDriver = new ChromeDriver(chromeOptions);
            while (!taskQueue.isEmpty()) {
                if(Thread.currentThread().isInterrupted()) {
                    webDriver.quit();
                    logger.info("다른 스레드의 작업 오류로 인한 작업 종료, {}", Thread.currentThread().getName());
                    return Collections.emptyMap();
                }

                int[] pages = taskQueue.poll();
                if (pages == null) break;

                logger.info("키워드: {}, 큐 크기: {}, 크롤링 범위: {} ~ {}", keyword, taskQueue.size(), pages[0], pages[1]);

                for (int j = pages[0]; j <= pages[1]; j++) {
                    animals.put(j, new ArrayList<>());
                    String page = "&page=" + j;
                    String url = BASE_CRAWLING_URL + keyword + page;

                    int PAGE_RETRY_COUNT = 0;
                    while (PAGE_RETRY_COUNT < MAX_PAGE_LOAD_RETRY) {
                        try {
                            webDriver.get(url);

                            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
                            List<WebElement> table = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table[@background='../images/board/main-search-img-frame-01.gif']//tr[2]/td//font[normalize-space(text())]")));
                            if (!table.isEmpty()) {
                                String originalWindow = webDriver.getWindowHandle();
                                int post = 1;
                                for (WebElement we : table) {
                                    int POST_RETRY_COUNT = 0;
                                    while (POST_RETRY_COUNT < MAX_POST_LOAD_RETRY) {
                                        try {
                                            String text = we.getText().trim();
                                            if (text.isEmpty() || text.contains("찾았어요")) {
                                                break;
                                            }

                                            logger.info("키워드: {}, 페이지: {}, 포스터: {}, 제목: {}, 총 포스터: {}, 재시도 {}번 중 {}번",
                                                        keyword, j, post, text, table.size(), MAX_POST_LOAD_RETRY, POST_RETRY_COUNT);
                                            Thread.sleep(200);

                                            if (POST_RETRY_COUNT == 0) {
                                                wait.until(ExpectedConditions.elementToBeClickable(we));
                                                we.click();
                                            } else {
                                                List<WebElement> freshTable = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table[@background='../images/board/main-search-img-frame-01.gif']//tr[2]/td//font[normalize-space(text())]")));
                                                if (freshTable.size() < post) {
                                                    logger.warn("재시도 중 게시물 부족 - 찾은 개수: {}, 필요 포스트: {}", freshTable.size(), post);
                                                    throw new CrawlingException("게시물 목록 불일치");
                                                }
                                                WebElement freshElement = freshTable.get(post - 1);
                                                wait.until(ExpectedConditions.elementToBeClickable(freshElement));
                                                freshElement.click();
                                            }

                                            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
                                            Set<String> windowHandles = webDriver.getWindowHandles();
                                            windowHandles.remove(originalWindow);
                                            if (!windowHandles.isEmpty()) {
                                                String newWindowHandle = windowHandles.iterator().next();
                                                webDriver.switchTo().window(newWindowHandle);
                                            } else {
                                                logger.warn("새 창이 열리지 않음 - 키워드: {}, 페이지: {}", keyword, j);
                                                throw new CrawlingException();
                                            }

                                            //List<WebElement> imgElement = webDriver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));
                                            List<WebElement> imgElement = new ArrayList<>();
                                            try {
                                                imgElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[contains(@src, '/pet_care/photo/')]")));
                                            } catch (TimeoutException e) {
                                                logger.info("게시물 {} 의 이미지는 존재 하지 않음", post);
                                            }

                                            List<WebElement> infoElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//b")));
                                            Map<String, String> crawlingData = getStringMap(infoElement, imgElement);
                                            CrawlAnimal crawlAnimal = mapper.convertValue(crawlingData, CrawlAnimal.class);

                                            if (crawlAnimal.equals(latestAnimal)) {
                                                logger.info("기존 데이터와 일치하는 항목 발견, 크롤링 중단");
                                                webDriver.quit();
                                                return animals;
                                            }

                                            logger.info("크롤링 한 데이터: {}", crawlAnimal);
                                            animals.get(j).add(crawlAnimal);

                                            webDriver.close();
                                            webDriver.switchTo().window(originalWindow);
                                            Thread.sleep(400);
                                            break; // 성공 시 재시도 루프 탈출
                                        } catch (NoSuchElementException | TimeoutException | CrawlingException | IndexOutOfBoundsException | ElementClickInterceptedException e) {
                                            POST_RETRY_COUNT++;
                                            logger.warn("게시물 {} 크롤링 실패 (재시도 {}/{}) - {}", post, POST_RETRY_COUNT, MAX_POST_LOAD_RETRY, e.getMessage());

                                            webDriver.close();
                                            webDriver.switchTo().window(originalWindow);
                                            Thread.sleep(BASE_WAIT_TIME * POST_RETRY_COUNT);
                                        }
                                    }
                                    if (POST_RETRY_COUNT == MAX_POST_LOAD_RETRY) {
                                        logger.error("게시물 {} 최대 재시도 초과 - 다음 게시물로 이동", post);
                                    }
                                    post++;
                                }
                                break; // 페이지 성공 시 페이지 재시도 루프 탈출
                            } else {
                                throw new CrawlingException("데이터 없음: 페이지 " + j);
                            }
                        } catch (CrawlingException e) {
                            PAGE_RETRY_COUNT++;
                            logger.warn("페이지 {} 크롤링 실패 (재시도 {}/{}) - {}", j, PAGE_RETRY_COUNT, MAX_PAGE_LOAD_RETRY, e.getMessage());
                            Thread.sleep(BASE_WAIT_TIME * PAGE_RETRY_COUNT);
                        }
                    }
                    if (PAGE_RETRY_COUNT == MAX_PAGE_LOAD_RETRY) {
                        logger.error("페이지 {} 로딩 실패", j);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("최상위 오류 발생: {}", e.getMessage(), e);
            throw new Exception(e.getMessage(), e); // 작업 실패 전파를 위함, TransactionTemplate에서 ExecutionException 으로 전달될 거임
        } finally {
            if (webDriver != null) {
                logger.info("웹드라이버 {} 종료 - 키워드: {}", webDriver, keyword);
                webDriver.quit();
            }
        }

        return animals;
    }

    private Map<String, String> getStringMap(List<WebElement> infoElement, List<WebElement> imgElement) {
        Map<String, String> crawlingData = new HashMap<>();

        if (!imgElement.isEmpty()) {
            crawlingData.put("imgURL", imgElement.get(0).getDomAttribute("src"));
            if (infoElement.size() == 7) {
                crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
                crawlingData.put("lost_place", infoElement.get(1).getText());
                crawlingData.put("lost_date", infoElement.get(2).getText());
                crawlingData.put("title", infoElement.get(3).getText());
                crawlingData.put("gender", infoElement.get(5).getText());
                crawlingData.put("details", infoElement.get(6).getText());
            } else if (infoElement.size() == 8) {
                crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
                crawlingData.put("gratuity", infoElement.get(1).getText().split(":")[1].trim());
                crawlingData.put("lost_place", infoElement.get(2).getText());
                crawlingData.put("lost_date", infoElement.get(3).getText());
                crawlingData.put("title", infoElement.get(4).getText());
                crawlingData.put("gender", infoElement.get(6).getText());
                crawlingData.put("details", infoElement.get(7).getText());
            }
        } else {
            crawlingData.put("imgURL", "Not Found");
            if (infoElement.size() == 8) {
                crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
                crawlingData.put("lost_place", infoElement.get(2).getText());
                crawlingData.put("lost_date", infoElement.get(3).getText());
                crawlingData.put("title", infoElement.get(4).getText());
                crawlingData.put("gender", infoElement.get(6).getText());
                crawlingData.put("details", infoElement.get(7).getText());
            } else if (infoElement.size() == 9) {
                crawlingData.put("phoneNumber", infoElement.get(0).getText().substring(5).replace(" ", ""));
                crawlingData.put("gratuity", infoElement.get(2).getText().split(":")[1].trim());
                crawlingData.put("lost_place", infoElement.get(3).getText());
                crawlingData.put("lost_date", infoElement.get(4).getText());
                crawlingData.put("title", infoElement.get(5).getText());
                crawlingData.put("gender", infoElement.get(7).getText());
                crawlingData.put("details", infoElement.get(8).getText());
            }
        }
        
        return crawlingData;
    }
}