package ru.msu.cmc.university_schedule.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HomePageTest extends SeleniumTestBase {

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void initUrl() {
        baseUrl = "http://localhost:" + port;
    }

    private String url(String path) {
        return baseUrl + path;
    }

    @Test
    void pageTitleIsCorrect() {
        driver.get(url("/"));
        String title = driver.getTitle();
        assertEquals("Главная — University Schedule", title);
    }

    @Test
    void navbarBrandLinksToHome() {
        driver.get(url("/"));
        WebElement brand = driver.findElement(By.cssSelector(".navbar-brand"));
        assertEquals("University Schedule", brand.getText());
        String href = brand.getAttribute("href");
        assertTrue(href.endsWith("/"));
    }

    @Test
    void navbarContainsAllSections() {
        driver.get(url("/"));
        Map<String, String> expected = Map.of(
                "/students",      "Студенты",
                "/teachers",      "Преподаватели",
                "/auditoriums",   "Аудитории",
                "/courses",       "Курсы"
        );

        List<WebElement> navLinks = driver.findElements(By.cssSelector(".navbar-nav .nav-link"));
        assertEquals(expected.size(), navLinks.size());

        for (WebElement link : navLinks) {
            String text = link.getText();
            String href = link.getAttribute("href");
            assertTrue(expected.containsValue(text), () -> "Unexpected nav link text: " + text);

            String path = expected.entrySet()
                    .stream()
                    .filter(e -> e.getValue().equals(text))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();
            assertTrue(href.endsWith(path), () -> "Link for " + text + " should end with " + path);
        }
    }

    @Test
    void mainListContainsAllSectionsWithIcons() {
        driver.get(url("/"));
        Map<String, String> expected = Map.of(
                "/students",      "Студенты",
                "/teachers",      "Преподаватели",
                "/auditoriums",   "Аудитории",
                "/courses",       "Курсы"
        );

        List<WebElement> items = driver.findElements(By.cssSelector(".list-group-item-action"));
        assertEquals(expected.size(), items.size());

        for (WebElement item : items) {
            String text = item.getText();
            String href = item.getAttribute("href");

            boolean ok = expected.values().stream().anyMatch(text::contains);
            assertTrue(ok, "Unexpected list item text: " + text);


            String key = expected.entrySet()
                    .stream()
                    .filter(e -> text.contains(e.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();
            assertTrue(href.endsWith(key), () -> "List item " + text + " should link to " + key);
        }
    }

    @Test
    void navbarAndMainListAreConsistent() {
        driver.get(url("/"));
        List<WebElement> navLinks  = driver.findElements(By.cssSelector(".navbar-nav .nav-link"));
        List<WebElement> listItems = driver.findElements(By.cssSelector(".list-group-item-action"));


        List<String> navHrefs  = navLinks .stream().map(e -> e.getAttribute("href")).sorted().toList();
        List<String> listHrefs = listItems.stream().map(e -> e.getAttribute("href")).sorted().toList();
        assertEquals(navHrefs, listHrefs, "Navbar links и Main list links должны совпадать");
    }

    @Test
    void navigationToEachSectionWorks() {
        Map<String,String> expectedPaths = new HashMap<>();
        expectedPaths.put("Студенты",    "/students");
        expectedPaths.put("Преподаватели","/teachers");
        expectedPaths.put("Аудитории",   "/auditoriums");
        expectedPaths.put("Курсы",       "/courses");

        for (Map.Entry<String, String> entry : expectedPaths.entrySet()) {
            driver.get(url("/"));

            WebElement item = driver.findElement(
                    By.xpath("//a[contains(@class,'list-group-item') and contains(.,'" + entry.getKey() + "')]")
            );
            item.click();

            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.urlContains(entry.getValue()));
            assertTrue(driver.getCurrentUrl().endsWith(entry.getValue()),
                    () -> "После клика по '" + entry.getKey() +
                            "' мы ожидаем URL заканчивающийся на " + entry.getValue());
        }
    }
}
