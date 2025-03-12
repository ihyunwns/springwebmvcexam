package com.hyunwns.demoweb.animal.service;

import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

@Slf4j
public class CrawlerThreadTest implements Callable<List<CrawlAnimal>> {

    private final BlockingQueue<int[]> taskQueue;
    private final CrawlAnimal latestAnimal;
    private final List<CrawlAnimal> data;

    public CrawlerThreadTest(BlockingQueue<int[]> taskQueue, CrawlAnimal latestAnimal, List<CrawlAnimal> data) {
        this.taskQueue = taskQueue;
        this.latestAnimal = latestAnimal;
        this.data = data;
    }

    @Override
    public List<CrawlAnimal> call() {
        List<CrawlAnimal> animals = new ArrayList<>();

        while (!taskQueue.isEmpty()) {
            if (Thread.currentThread().isInterrupted()) {
                log.info("다른 스레드의 작업 오류로 인한 작업 종료, {}", Thread.currentThread().getName());
                return Collections.emptyList();
            }

            int[] pages = taskQueue.poll();
            if (pages == null) break;

            log.info("큐 크기: {}, 크롤링 범위: {} ~ {}", taskQueue.size(), pages[0], pages[1]);
            for (int j = pages[0]; j <= pages[1]; j++) {
                int startIdx = (j - 1) * 30;
                int endIdx = Math.min(startIdx + 30, data.size());

                for (int k = startIdx; k < endIdx; k++) {
                    CrawlAnimal animal = data.get(k);
                    animals.add(animal);

                    // 최신 데이터 발견 시 중단
                    if (animal.equals(latestAnimal)) {
                        log.info("최신 데이터 발견! 페이지 {}에서 크롤링 중지.", j);
                        return animals;
                    }
                }
            }
        }
        return animals;
    }
}
