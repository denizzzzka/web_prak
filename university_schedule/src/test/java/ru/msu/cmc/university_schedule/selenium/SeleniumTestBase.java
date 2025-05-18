package ru.msu.cmc.university_schedule.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SeleniumTestBase {

    protected WebDriver driver;

    @BeforeAll
    static void setupClass() {

    }

    @BeforeEach
    public void setupTest() throws IOException {
        System.setProperty("webdriver.chrome.driver",
                "/home/denis/Downloads/chromedriver-linux64/chromedriver"
        );
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/home/denis/Downloads/chrome-linux64/chrome");


        Path userDataDir = Files.createTempDirectory("selenium-profile-");
        options.addArguments("--user-data-dir=" + userDataDir);


        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");



        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }


    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
