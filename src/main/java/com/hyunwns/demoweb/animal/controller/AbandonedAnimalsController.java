package com.hyunwns.demoweb.animal.controller;

import com.hyunwns.demoweb.animal.domain.CrawlAnimal;
import com.hyunwns.demoweb.animal.service.WebCrawlingService;
import com.hyunwns.demoweb.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.checkerframework.checker.units.qual.A;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/animal")
public class AbandonedAnimalsController {

    private final SecurityUtils securityUtils;
    private final WebCrawlingService webCrawlingService;

    @GetMapping("/home")
    public String animal(@RequestParam(name = "keyword", required = false) String keyword, Model model) throws SQLException {
        securityUtils.addAttributeUserInfo(model);

        return "animal/home";
    }

    @GetMapping("/manage")
    public String manage(Model model) throws SQLException {
        securityUtils.addAttributeUserInfo(model);

        boolean running = webCrawlingService.getRunningState();
        log.info("running: {}", running);

        model.addAttribute("running", running);

        try {
            String updated_at = webCrawlingService.getLastUpdatedDate();
            model.addAttribute("updated_at", updated_at);
        }catch (EmptyResultDataAccessException e){
            model.addAttribute("updated_at", "데이터 갱신 필요");
        }

        model.addAttribute("dog_preview", webCrawlingService.getAnimalData("dog", 10));
        model.addAttribute("cat_preview", webCrawlingService.getAnimalData("cat", 10));
        model.addAttribute("etc_preview", webCrawlingService.getAnimalData("etc", 10));

        return "animal/manage";
    }

    @GetMapping("/details")
    public void detailsCrawledAnimal(@RequestParam("category") String category, @RequestParam("id") int id, Model model) throws SQLException {

        log.info("details 클릭, {}", id);
    }

    @GetMapping("/more")
    public String moreAnimals(@RequestParam("category") String category, Model model) throws SQLException {

        return "animal/more";
    }

    @PostMapping("/syncData")
    public ResponseEntity<String> requestCrawling(@RequestBody String crawlData) throws SQLException {

        try {
            if (webCrawlingService.syncAnimalData()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
