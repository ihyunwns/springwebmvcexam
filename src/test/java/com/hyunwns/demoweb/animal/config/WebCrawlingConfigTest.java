package com.hyunwns.demoweb.animal.config;

import io.github.bonigarcia.wdm.WebDriverManager;
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

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;


class WebCrawlingConfigTest {

    WebDriver driver;

    @BeforeEach
    void setupClass() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("headless");

        this.driver = new ChromeDriver(options);
    }

    @Test
    public void crawl() throws Exception{
        try{
            String url = "http://localhost:8080";
            driver.get(url);

            WebElement headerDiv = driver.findElement(By.className("header-container"));
            System.out.println("Header Text: " + headerDiv.getText());

        } catch (Exception e){
            Assertions.fail("크롤링 중 예외 발생: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}