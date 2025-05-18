package ru.msu.cmc.university_schedule.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.msu.cmc.university_schedule.DAO.*;
import ru.msu.cmc.university_schedule.entities.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuditoriumPageTest extends SeleniumTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private AuditoriumDAO auditoriumDAO;
    @Autowired
    private CourseDAO     courseDAO;
    @Autowired
    private TeacherDAO    teacherDAO;
    @Autowired
    private LessonDAO     lessonDAO;

    private Auditorium aud1, aud2;
    private Lesson     lesson;

    @BeforeEach
    void prepareData() {
        lessonDAO.getAll().forEach(lessonDAO::delete);
        auditoriumDAO.getAll().forEach(auditoriumDAO::delete);
        courseDAO.getAll().forEach(courseDAO::delete);
        teacherDAO.getAll().forEach(teacherDAO::delete);


        aud1 = new Auditorium(null, "101", 50);
        auditoriumDAO.save(aud1);
        aud2 = new Auditorium(null, "102", 30);
        auditoriumDAO.save(aud2);


        Teacher teacher = new Teacher();
        teacher.setFullName("Иванов И.И.");
        teacherDAO.save(teacher);


        Course course = new Course();
        course.setName("Программирование");
        courseDAO.save(course);


        LocalDateTime start = LocalDate.of(2025, 4, 1).atTime(10, 0);
        LocalDateTime end   = LocalDate.of(2025, 4, 1).atTime(12, 0);
        lesson = new Lesson(null, course, teacher, aud1, start, end);
        lessonDAO.save(lesson);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void auditoriumsListPageDisplaysAll() {
        driver.get(baseUrl() + "/auditoriums");


        String title = driver.getTitle();
        assertThat(title).isEqualTo("Аудитории — University Schedule");


        List<WebElement> headers = driver.findElements(By.cssSelector("table thead th"));
        assertThat(headers).extracting(WebElement::getText)
                .containsExactly("ID", "Номер", "Вместимость", "Расписание");


        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(2);


        WebElement first = rows.get(0);
        List<String> cols = first.findElements(By.tagName("td"))
                .stream().map(WebElement::getText).toList();
        assertThat(cols.get(0)).isEqualTo(aud1.getId().toString());
        assertThat(cols.get(1)).isEqualTo("101");
        assertThat(cols.get(2)).isEqualTo("50");


        WebElement btn = first.findElement(By.cssSelector("a.btn-info"));
        btn.click();
        Assertions.assertTrue(driver.getCurrentUrl()
                .endsWith("/auditoriums/" + aud1.getId() + "/schedule"));
    }

    @Test
    void auditoriumSchedulePageShowsLessonsAndBack() {
        driver.get(baseUrl() + "/auditoriums/" + aud1.getId() + "/schedule");

        assertThat(driver.getTitle())
                .isEqualTo("Расписание аудитории");

        List<String> headers = driver.findElements(
                        By.cssSelector("table thead th"))
                .stream().map(WebElement::getText).toList();
        assertThat(headers)
                .containsExactly("Дата", "Время", "Курс", "Преподаватель");

        List<WebElement> rows = driver.findElements(
                By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(1);

        WebElement r = rows.get(0);
        List<WebElement> cols = r.findElements(By.tagName("td"));

        String expectedDate = lesson.getStartTime()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        assertThat(cols.get(0).getText()).isEqualTo(expectedDate);


        String timeText = cols.get(1).getText().replace(" ", "");
        assertThat(timeText).contains("10:00").contains("12:00");

        assertThat(cols.get(2).getText())
                .isEqualTo(lesson.getCourse().getName());
        assertThat(cols.get(3).getText())
                .isEqualTo(lesson.getTeacher().getFullName());


        WebElement back = driver.findElement(
                By.cssSelector("a.btn-secondary"));
        back.click();
        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/auditoriums"));
    }

    @Test
    void emptyLessonsMessageWhenNoLessons() {

        lessonDAO.getAll().forEach(lessonDAO::delete);
        driver.get(baseUrl() + "/auditoriums/" + aud1.getId() + "/schedule");

        WebElement tr = driver.findElement(
                By.cssSelector("table tbody tr"));
        assertThat(tr.getText()).isEqualTo("Нет занятий");
        assertThat(tr.findElement(By.tagName("td"))
                .getAttribute("colspan")).isEqualTo("4");
    }

    @Test
    void filterFormRetainsValues() {
        String startDate = "2025-04-01";
        String startTime = "08:30";
        String endDate   = "2025-04-02";
        String endTime   = "18:00";

        driver.get(baseUrl() + String.format(
                "/auditoriums?startDate=%s&startTime=%s&endDate=%s&endTime=%s",
                startDate, startTime, endDate, endTime));

        assertThat(driver.findElement(By.id("startDate"))
                .getAttribute("value")).isEqualTo(startDate);
        assertThat(driver.findElement(By.id("startTime"))
                .getAttribute("value")).isEqualTo(startTime);
        assertThat(driver.findElement(By.id("endDate"))
                .getAttribute("value")).isEqualTo(endDate);
        assertThat(driver.findElement(By.id("endTime"))
                .getAttribute("value")).isEqualTo(endTime);

        WebElement btn = driver.findElement(
                By.cssSelector("button[type=submit]"));
        btn.click();
        assertThat(driver.getCurrentUrl())
                .contains("startDate=").contains("endTime=");
    }
}
