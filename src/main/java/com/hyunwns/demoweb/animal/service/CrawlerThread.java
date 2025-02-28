package com.hyunwns.demoweb.animal.service;

import com.hyunwns.demoweb.animal.exception.CrawlingException;
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
import java.util.concurrent.BlockingQueue;
import org.openqa.selenium.TimeoutException;

import static com.hyunwns.demoweb.animal.service.WebCrawlingService.BASE_CRAWLING_URL;


public class CrawlerThread implements Runnable {

    private final ChromeOptions chromeOptions;
    private final Logger logger = LoggerFactory.getLogger(CrawlerThread.class);
    private final BlockingQueue<int[]> taskQueue;
    private final String keyword;

    private static final int MAX_PAGE_LOAD_RETRY = 3;
    private static final int MAX_POST_LOAD_RETRY = 3;
    private static final long BASE_WAIT_TIME = 2000; /* 2000 ms */

    public CrawlerThread(ChromeOptions options, BlockingQueue<int[]> taskQueue, String keyword) {
        this.chromeOptions = options;
        this.taskQueue = taskQueue;
        this.keyword = keyword;
    }

    @Override
    public void run() {
        WebDriver webDriver = null;

        try{
            webDriver = new ChromeDriver(chromeOptions);
            while (!taskQueue.isEmpty()) {

                int[] pages = taskQueue.poll();
                if (pages == null) break; // 큐가 비었으면 종료

                logger.info("키워드: {}, 큐 크기: {}, 크롤링 범위: {} ~ {}", keyword, taskQueue.size(), pages[0], pages[1]);

                for (int j = pages[0]; j <= pages[1]; j++) {
                    String page = "&page=" + j;
                    String url = BASE_CRAWLING_URL + keyword + page;

                    int PAGE_RETRY_COUNT = 0;
                    while(PAGE_RETRY_COUNT < MAX_PAGE_LOAD_RETRY) {
                        try{
                            webDriver.get(url);
                            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));
                            List<WebElement> table = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@background='../images/board/main-search-img-frame-01.gif']//tr[2]/td//font[normalize-space(text())]")));
//                            List<WebElement> table = webDriver.findElements(By.xpath("//table[@background=\"../images/board/main-search-img-frame-01.gif\"]//tr[2]/td//font[normalize-space(text())]"));
                            if (!table.isEmpty()) {

                                String originalWindow = webDriver.getWindowHandle();
                                int post = 1;
                                for (WebElement we : table) {
                                    int POST_RETRY_COUNT = 0;
                                    while(POST_RETRY_COUNT < MAX_POST_LOAD_RETRY) {
                                        try {
                                            String text = we.getText().trim();
                                            logger.info("포스터: {} 찾은 제목 이름: {}", post, text);
                                            if (text.isEmpty() || we.getText().contains("찾았어요")) {
                                                break;
                                            }

                                            logger.info("키워드: {}, 현재 작업중인 페이지: {}, 현재 작업중인 포스터: {}, 찾은 포스터 크기: {}",keyword, j, post, table.size());
                                            Thread.sleep(200);

                                            if (POST_RETRY_COUNT == 0) {
                                                we.click();
                                            } else {
                                                // 재시도: 요소를 새로 찾아 클릭
                                                List<WebElement> freshTable = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@background='../images/board/main-search-img-frame-01.gif']//tr[2]/td//font[normalize-space(text())]")));
                                                if (freshTable.size() < post) {
                                                    logger.warn("재시도 중 게시물 부족 - 찾은 개수: {}, 필요 포스트: {}", freshTable.size(), post);
                                                    throw new CrawlingException("게시물 목록 불일치");
                                                }
                                                WebElement freshElement = freshTable.get(post - 1);
                                                wait.until(ExpectedConditions.elementToBeClickable(freshElement));
                                                freshElement.click();
                                            }

                                            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
                                            Set<String> windowHandles = webDriver.getWindowHandles(); //현재 열려있는 창
                                            windowHandles.remove(originalWindow);
                                            if (!windowHandles.isEmpty()) {
                                                String newWindowHandle = windowHandles.iterator().next();
                                                webDriver.switchTo().window(newWindowHandle);
                                            } else {
                                                logger.warn("새 창이 열리지 않음 - 키워드: {}, 페이지: {}", keyword, j);
                                                throw new CrawlingException();
                                            }

                                            List<WebElement> imgElement = webDriver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));
                                            List<WebElement> infoElement = webDriver.findElements(By.xpath("//b"));
                                            Map<String, String> crawlingData = getStringMap(infoElement, imgElement);
                                            logger.info("크롤링 한 데이터: {}", crawlingData);

                                            webDriver.close();
                                            webDriver.switchTo().window(originalWindow);

                                            Thread.sleep(400);

                                            break; // 포스트 크롤링 성공 시 재시도 루프 탈출
                                        } catch (NoSuchElementException | TimeoutException | CrawlingException e) {
                                            POST_RETRY_COUNT++;
                                            logger.warn("게시물 {} 크롤링 실패 ( 재시도 {} / {} )", post, POST_RETRY_COUNT, MAX_POST_LOAD_RETRY);
                                            Thread.sleep(BASE_WAIT_TIME * POST_RETRY_COUNT);
                                        }
                                    }
                                    if (POST_RETRY_COUNT == MAX_POST_LOAD_RETRY) {
                                        logger.error("게시물 {} 최대 재시도 초과 - 다음 게시물로 이동", post);
                                    }
                                    post++;
                                }
                                break; // 페이지 로드 성공 시 재시도 루프 탈출
                            } else {
                                throw new CrawlingException("데이터 없음: 페이지 " + j);
                            }
                        }
                        catch(CrawlingException | NoSuchElementException e){

                            PAGE_RETRY_COUNT++;
                            logger.warn("페이지 {} 크롤링 실패 ( 재시도 {} / {} )", j, PAGE_RETRY_COUNT, MAX_PAGE_LOAD_RETRY);

                            Thread.sleep(BASE_WAIT_TIME * PAGE_RETRY_COUNT);
                        }
                    }
                    if (PAGE_RETRY_COUNT == MAX_PAGE_LOAD_RETRY) {
                        logger.error("페이지 {} 로딩 실패 ", j);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("스레드 오류: {}", e.getMessage(), e);
        } finally {
            if(webDriver != null) {
                webDriver.quit();
                logger.info("WebDriver 종료 - 키워드 : {}", keyword);
            }
        }
    }

    private Map<String, String> getStringMap(List<WebElement> infoElement, List<WebElement> imgElement) throws CrawlingException{
        Map<String, String> crawlingData = new HashMap<>();
        try{

            if (!imgElement.isEmpty()) {
                crawlingData.put("src", imgElement.get(0).getDomAttribute("src"));
            } else {
                crawlingData.put("src", "Not Found");
            }

            if (infoElement.size() < 7) {
                logger.warn("정보 요소 부족 - 찾은 개수 : {} ", infoElement.size());
                throw new CrawlingException();
            }

            crawlingData.put("phone", infoElement.get(0).getText().substring(5).replace(" ", ""));
            crawlingData.put("address", infoElement.get(1).getText());
            crawlingData.put("date", infoElement.get(2).getText());
            crawlingData.put("title", infoElement.get(3).getText());
            crawlingData.put("gender", infoElement.get(5).getText());
            crawlingData.put("details", infoElement.get(6).getText());
        }catch (IndexOutOfBoundsException e) {
            throw new CrawlingException();
        }

        return crawlingData;
    }
}
