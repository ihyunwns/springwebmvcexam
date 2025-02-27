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


                /*CrawlerThread에서 while (!taskQueue.isEmpty()) 조건으로 작업을 가져오는데, taskQueue.poll()이 null을 반환하면 즉시 종료되지 않고 pages가 null인 상태로 진행될 수 있다. 또한, 모든 스레드가 동일한 큐를 체크하므로 스레드 간 작업 중복이나 누락이 발생할 수 있다.

                증상:
                NullPointerException이 발생하거나, 예상보다 적은 페이지가 처리된다.
                로그에서 "큐의 크기"가 빠르게 0으로 떨어진다.*/

                int[] pages = taskQueue.poll();

                logger.info("큐의 크기: {}", taskQueue.size());
                logger.info("크롤링 동작 범위: {} ~ {}", pages[0], pages[1]);

                for (int j = pages[0]; j <= pages[1]; j++) {
                    String page = "&page=" + j;
                    String url = BASE_CRAWLING_URL + keyword + page;

                    int PAGE_RETRY_COUNT = 0;
                    while(PAGE_RETRY_COUNT < MAX_PAGE_LOAD_RETRY) {
                        try{
                            webDriver.get(url);
                            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
                            List<WebElement> table = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@background='../images/board/main-search-img-frame-01.gif']//tr[2]/td//font[normalize-space(text())]")));
//                            List<WebElement> table = webDriver.findElements(By.xpath("//table[@background=\"../images/board/main-search-img-frame-01.gif\"]//tr[2]/td//font[normalize-space(text())]"));



                            if (!table.isEmpty()) {

                                String originalWindow = webDriver.getWindowHandle();
                                logger.info("original window: {}", originalWindow);

                                int post = 1;
                                for (WebElement we : table) {

                                    int POST_RETRY_COUNT = 0;
                                    while(POST_RETRY_COUNT < MAX_POST_LOAD_RETRY) {
                                        try {
                                            String text = we.getText().trim();
                                            if (text.isEmpty() || we.getText().contains("찾았어요")) {
                                                post++;
                                                continue;
                                            }
                                            logger.info("키워드: {}, 현재 작업중인 페이지: {}, 현재 작업중인 포스터: {}, 찾은 포스터 크기: {}",keyword, j, post++, table.size());
                                            we.click();

                                            Thread.sleep(400);

                                            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
                                            Set<String> windowHandles = webDriver.getWindowHandles(); //현재 열려있는 창
                                            windowHandles.remove(originalWindow);
                                            if (!windowHandles.isEmpty()) {
                                                String newWindowHandle = windowHandles.iterator().next();
                                                webDriver.switchTo().window(newWindowHandle);
                                            }

                                            List<WebElement> imgElement = webDriver.findElements(By.xpath("//img[contains(@src, '/pet_care/photo/')]"));
                                            List<WebElement> infoElement = webDriver.findElements(By.xpath("//b"));
                                            Map<String, String> crawlingData = getStringMap(infoElement, imgElement);
                                            logger.info("crawlingData: {}", crawlingData);

                                            webDriver.close();
                                            webDriver.switchTo().window(originalWindow);

                                            Thread.sleep(400);

                                        } catch (CrawlingException e) {
                                            POST_RETRY_COUNT++;
                                            logger.warn("게시물 {} 크롤링 실패 ( 재시도 {} / {} )", post, POST_RETRY_COUNT, MAX_POST_LOAD_RETRY);
                                            Thread.sleep(BASE_WAIT_TIME * POST_RETRY_COUNT);
                                        }
                                    }
                                }
                            } else {
                                throw new CrawlingException("데이터 없음: 페이지 " + j);
                            }
                        }
                        catch(CrawlingException e){

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
            logger.error(e.getMessage(), e);
        } finally {
            if(webDriver != null) {
                webDriver.quit();
                logger.info("WebDriver 종료");
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
