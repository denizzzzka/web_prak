package ru.msu.cmc.university_schedule.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.msu.cmc.university_schedule.DAO.*;
import ru.msu.cmc.university_schedule.entities.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeacherPageTest extends SeleniumTestBase {

    @LocalServerPort
    private int port;

    @Autowired private StreamDAO streamDAO;
    @Autowired private GroupDAO  groupDAO;
    @Autowired private CourseDAO courseDAO;
    @Autowired private TeacherDAO teacherDAO;
    @Autowired private TeacherCourseDAO teacherCourseDAO;
    @Autowired private LessonDAO lessonDAO;
    @Autowired private AuditoriumDAO auditoriumDAO;

    private Stream st;
    private Group gr;
    private Course course1, course2;
    private Teacher teacher1, teacher2;
    private Auditorium aud;
    private Lesson lesson1;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void prepareData() {

        lessonDAO.getAll().forEach(lessonDAO::delete);
        teacherCourseDAO.getAll().forEach(teacherCourseDAO::delete);
        courseDAO.getAll().forEach(courseDAO::delete);
        teacherDAO.getAll().forEach(teacherDAO::delete);
        groupDAO.getAll().forEach(groupDAO::delete);
        streamDAO.getAll().forEach(streamDAO::delete);
        auditoriumDAO.getAll().forEach(auditoriumDAO::delete);


        st = new Stream();
        st.setName("IT");
        streamDAO.save(st);

        gr = new Group();
        gr.setName("G1");
        gr.setStream(st);
        groupDAO.save(gr);


        course1 = new Course();
        course1.setName("Course A");
        course1.setStream(st);
        course1.setGroup(gr);
        course1.setSpecialCourse(false);
        course1.setWeeklyIntensity(2);
        course1.setYearOfStudy(1);
        courseDAO.save(course1);

        course2 = new Course();
        course2.setName("Course B");
        course2.setStream(st);
        course2.setGroup(gr);
        course2.setSpecialCourse(true);
        course2.setWeeklyIntensity(4);
        course2.setYearOfStudy(2);
        courseDAO.save(course2);


        aud = new Auditorium(null, "Aud-101", 30);
        auditoriumDAO.save(aud);


        teacher1 = new Teacher();
        teacher1.setFullName("Ivanov I.I.");
        teacherDAO.save(teacher1);

        teacher2 = new Teacher();
        teacher2.setFullName("Petrov P.P.");
        teacherDAO.save(teacher2);

        TeacherCourse tc1 = new TeacherCourse();
        tc1.setTeacher(teacher1);
        tc1.setCourse(course1);

        tc1.setId(new TeacherCourseId(teacher1.getId(), course1.getId(), 2025));
        tc1.setYear(2025);
        teacherCourseDAO.save(tc1);

        TeacherCourse tc2 = new TeacherCourse();
        tc2.setTeacher(teacher2);
        tc2.setCourse(course2);
        tc2.setId(new TeacherCourseId(teacher2.getId(), course2.getId(), 2025));
        tc2.setYear(2025);
        teacherCourseDAO.save(tc2);


        LocalDateTime start = LocalDate.of(2025, 4, 1).atTime(10, 0);
        LocalDateTime end   = LocalDate.of(2025, 4, 1).atTime(12, 0);
        lesson1 = new Lesson();
        lesson1.setTeacher(teacher1);
        lesson1.setCourse(course1);
        lesson1.setAuditorium(aud);
        lesson1.setStartTime(start);
        lesson1.setEndTime(end);
        lessonDAO.save(lesson1);
    }


    @AfterEach
    void cleanupData() {

        lessonDAO.getAll().forEach(lessonDAO::delete);
        teacherCourseDAO.getAll().forEach(teacherCourseDAO::delete);
        courseDAO.getAll().forEach(courseDAO::delete);
        teacherDAO.getAll().forEach(teacherDAO::delete);
        groupDAO.getAll().forEach(groupDAO::delete);
        streamDAO.getAll().forEach(streamDAO::delete);
        auditoriumDAO.getAll().forEach(auditoriumDAO::delete);
    }

    @Test
    void addNewTeacher() {
        driver.get(baseUrl() + "/teachers");
        driver.findElement(By.cssSelector("a.btn-success")).click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.titleContains("Добавить / Редактировать преподавателя"));

        String newName = "Sidorov S.S.";
        driver.findElement(By.name("fullName")).sendKeys(newName);

        String courseId = course1.getId().toString();
        WebElement chk = driver.findElement(
                By.cssSelector("input[name=courses][value='" + courseId + "']"));
        chk.click();
        assertThat(chk.isSelected()).isTrue();

        driver.findElement(By.cssSelector("button[type=submit]")).click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlMatches(".*/teachers(\\?.*)?$"));

        By linkLocator = By.xpath("//table//a[text()='" + newName + "']");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(linkLocator));

        assertThat(driver.findElements(linkLocator)).isNotEmpty();
    }


    @Test
    void teachersListPageDisplaysAllAndNavigatesToProfile() {
        driver.get(baseUrl() + "/teachers");


        assertThat(driver.getTitle()).isEqualTo("Преподаватели — University Schedule");
        WebElement navActive = driver.findElement(By.cssSelector(".navbar-nav .nav-link.active"));
        assertThat(navActive.getText()).isEqualTo("Преподаватели");


        List<String> headers = driver.findElements(By.cssSelector("table thead th"))
                .stream().map(WebElement::getText).toList();
        assertThat(headers).containsExactly("ID","ФИО","Действия");


        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(2);


        WebElement first = rows.get(0);
        List<String> cols = first.findElements(By.tagName("td"))
                .stream().map(WebElement::getText).toList();
        assertThat(cols.get(1)).isEqualTo(teacher1.getFullName());


        first.findElement(By.tagName("a")).click();
        assertThat(driver.getCurrentUrl())
                .endsWith("/teachers/" + teacher1.getId());
    }

    @Test
    void filterFormRetainsValuesAndFilters() {
        String params = String.format("?courseId=%d&search=%s",
                course1.getId(), "Ivanov");
        driver.get(baseUrl() + "/teachers" + params);


        WebElement selCourse = driver.findElement(By.name("courseId"));
        assertThat(selCourse.getAttribute("value"))
                .isEqualTo(course1.getId().toString());

        WebElement inpSearch = driver.findElement(By.name("search"));
        assertThat(inpSearch.getAttribute("value")).isEqualTo("Ivanov");


        driver.findElement(By.cssSelector("button[type=submit]")).click();
        String url = driver.getCurrentUrl();
        assertThat(url).contains("courseId=").contains("search=");


        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getText()).contains(teacher1.getFullName());
    }

    @Test
    void newTeacherFormIsEmptyAndCancelGoesBack() {
        driver.get(baseUrl() + "/teachers/new");

        assertThat(driver.getTitle()).isEqualTo("Добавить / Редактировать преподавателя");
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo("Новый преподаватель");


        assertThat(driver.findElement(By.name("fullName")).getAttribute("value")).isEmpty();

        List<WebElement> checks = driver.findElements(By.cssSelector("input[type=checkbox]"));
        assertThat(checks).allSatisfy(c -> assertThat(c.isSelected()).isFalse());


        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/teachers");
    }



    @Test
    void teacherProfilePageShowsDetailsAndLinks() {
        driver.get(baseUrl() + "/teachers/" + teacher1.getId());

        assertThat(driver.getTitle())
                .isEqualTo("Преподаватель: " + teacher1.getFullName());
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo(teacher1.getFullName());


        WebElement idSpan = driver.findElement(By.cssSelector("p span"));
        assertThat(idSpan.getText()).isEqualTo(teacher1.getId().toString());


        WebElement sched = driver.findElement(By.cssSelector("a.btn-info"));
        assertThat(sched.getAttribute("href"))
                .endsWith("/teachers/" + teacher1.getId() + "/schedule");

        WebElement back = driver.findElement(By.cssSelector("a.btn-secondary"));
        assertThat(back.getAttribute("href")).endsWith("/teachers");
    }

    @Test
    void teacherSchedulePageShowsLessonsAndBack() {
        driver.get(baseUrl() + "/teachers/" + teacher1.getId() + "/schedule");

        assertThat(driver.getTitle()).isEqualTo("Расписание преподавателя");


        List<String> hdr = driver.findElements(By.cssSelector("table thead th"))
                .stream().map(WebElement::getText).toList();
        assertThat(hdr).containsExactly("Дата","Время","Курс","Аудитория");


        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(1);
        WebElement r = rows.get(0);
        List<WebElement> cols = r.findElements(By.tagName("td"));

        String expDate = lesson1.getStartTime()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        assertThat(cols.get(0).getText()).isEqualTo(expDate);

        String t1 = lesson1.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String t2 = lesson1.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        assertThat(cols.get(1).getText().replace(" ",""))
                .contains(t1).contains(t2);

        assertThat(cols.get(2).getText()).isEqualTo(course1.getName());
        assertThat(cols.get(3).getText()).isEqualTo(aud.getNumber());


        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/teachers");
    }

    @Test
    void emptyScheduleShowsMessage() {

        lessonDAO.getAll().forEach(lessonDAO::delete);
        driver.get(baseUrl() + "/teachers/" + teacher1.getId() + "/schedule");

        WebElement tr = driver.findElement(By.cssSelector("table tbody tr"));
        assertThat(tr.getText()).isEqualTo("Нет занятий");
        assertThat(tr.findElement(By.tagName("td"))
                .getAttribute("colspan")).isEqualTo("4");
    }
}
