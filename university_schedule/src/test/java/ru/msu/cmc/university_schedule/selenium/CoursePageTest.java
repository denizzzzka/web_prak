package ru.msu.cmc.university_schedule.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.msu.cmc.university_schedule.DAO.*;
import ru.msu.cmc.university_schedule.entities.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CoursePageTest extends SeleniumTestBase {

    @LocalServerPort
    private int port;

    @Autowired private CourseDAO courseDAO;
    @Autowired private StreamDAO streamDAO;
    @Autowired private GroupDAO groupDAO;
    @Autowired private TeacherDAO teacherDAO;
    @Autowired private AuditoriumDAO auditoriumDAO;
    @Autowired private LessonDAO lessonDAO;

    private Stream st1;
    private Group gr1;
    private Course course1, course2;
    private Teacher teacher1;
    private Auditorium aud1;
    private Lesson lesson1;

    @BeforeEach
    void prepareData() {

        lessonDAO.getAll().forEach(lessonDAO::delete);
        courseDAO.getAll().forEach(courseDAO::delete);
        groupDAO.getAll().forEach(groupDAO::delete);
        streamDAO.getAll().forEach(streamDAO::delete);
        teacherDAO.getAll().forEach(teacherDAO::delete);
        auditoriumDAO.getAll().forEach(auditoriumDAO::delete);


        st1 = new Stream();
        st1.setName("ИТ");
        streamDAO.save(st1);

        gr1 = new Group();
        gr1.setName("Группа A");
        gr1.setStream(st1);
        groupDAO.save(gr1);


        course1 = new Course();
        course1.setName("Курс 1");
        course1.setStream(st1);
        course1.setGroup(gr1);
        course1.setSpecialCourse(false);
        course1.setWeeklyIntensity(3);
        course1.setYearOfStudy(1);
        courseDAO.save(course1);

        course2 = new Course();
        course2.setName("Курс 2");
        course2.setSpecialCourse(true);
        course2.setWeeklyIntensity(5);
        course2.setYearOfStudy(2);
        courseDAO.save(course2);


        teacher1 = new Teacher();
        teacher1.setFullName("Петров П.П.");
        teacherDAO.save(teacher1);

        aud1 = new Auditorium(null, "201", 40);
        auditoriumDAO.save(aud1);


        LocalDateTime start = LocalDate.of(2025, 4, 1).atTime(10, 0);
        LocalDateTime end   = LocalDate.of(2025, 4, 1).atTime(12, 0);
        lesson1 = new Lesson();
        lesson1.setCourse(course1);
        lesson1.setTeacher(teacher1);
        lesson1.setAuditorium(aud1);
        lesson1.setStartTime(start);
        lesson1.setEndTime(end);
        lessonDAO.save(lesson1);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void addNewCourse() {
        driver.get(baseUrl() + "/courses");
        driver.findElement(By.cssSelector("a.btn-success")).click();

        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.titleIs("Новый курс"));

        String name = "Курс Новый";
        driver.findElement(By.name("name")).sendKeys(name);
        driver.findElement(By.name("weeklyIntensity")).clear();
        driver.findElement(By.name("weeklyIntensity")).sendKeys("7");
        driver.findElement(By.name("yearOfStudy")).clear();
        driver.findElement(By.name("yearOfStudy")).sendKeys("3");

        driver.findElement(By.name("stream.id"))
                .findElement(By.cssSelector("option[value='" + st1.getId() + "']")).click();
        driver.findElement(By.name("group.id"))
                .findElement(By.cssSelector("option[value='" + gr1.getId() + "']")).click();

        WebElement special = driver.findElement(By.cssSelector("input[type=checkbox]"));
        special.click();
        assertThat(special.isSelected()).isTrue();

        driver.findElement(By.cssSelector("button[type=submit]")).click();

        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.urlMatches(".*/courses$"));

        By link = By.xpath("//table//a[text()='" + name + "']");
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.visibilityOfElementLocated(link));

        assertThat(driver.findElements(link)).isNotEmpty();
    }

    @Test
    void editExistingCourse() {
        driver.get(baseUrl() + "/courses/" + course1.getId() + "/edit");
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.titleContains("Редактировать курс"));

        String updatedName = "Курс 1 Updated";
        WebElement nameInput = driver.findElement(By.name("name"));
        nameInput.clear();
        nameInput.sendKeys(updatedName);

        WebElement intensity = driver.findElement(By.name("weeklyIntensity"));
        intensity.clear();
        intensity.sendKeys("9");

        driver.findElement(By.cssSelector("button[type=submit]")).click();

        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.urlMatches(".*/courses/\\d+$"));
        assertThat(driver.getCurrentUrl()).endsWith("/courses/" + course1.getId());

        String header = driver.findElement(By.tagName("h1")).getText();
        assertThat(header).isEqualTo("Курс: " + updatedName);
    }

    @Test
    void deleteCourse() {
        Course tmp = new Course();
        tmp.setName("Temp to delete");
        tmp.setSpecialCourse(false);
        tmp.setWeeklyIntensity(1);
        tmp.setYearOfStudy(1);
        courseDAO.save(tmp);

        driver.get(baseUrl() + "/courses/" + tmp.getId());


        driver.findElement(By.cssSelector("form button.btn-danger")).click();

        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.alertIsPresent())
                .accept();

        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.urlMatches(".*/courses$"));

        assertThat(driver.findElements(By.linkText("Temp to delete"))).isEmpty();
    }


    @Test
    void addAndDeleteLessonInCourseSchedule() {
        driver.get(baseUrl() + "/courses/" + course1.getId() + "/schedule");
        int initial = driver.findElements(By.cssSelector("table tbody tr")).size();

        driver.findElement(By.name("teacherId"))
                .findElement(By.cssSelector("option[value='" + teacher1.getId() + "']")).click();
        driver.findElement(By.name("auditoriumId"))
                .findElement(By.cssSelector("option[value='" + aud1.getId() + "']")).click();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = '2025-05-01';",
                driver.findElement(By.name("startDate")));
        js.executeScript("arguments[0].value = '08:00';",
                driver.findElement(By.name("startTime")));
        js.executeScript("arguments[0].value = '2025-05-01';",
                driver.findElement(By.name("endDate")));
        js.executeScript("arguments[0].value = '10:00';",
                driver.findElement(By.name("endTime")));

        driver.findElement(By.cssSelector("button[type=submit]")).click();

        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.numberOfElementsToBe(
                        By.cssSelector("table tbody tr"), initial + 1));

        List<WebElement> deleteBtns = driver.findElements(
                By.cssSelector("button.btn-danger"));
        deleteBtns.get(deleteBtns.size() - 1).click();

        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.alertIsPresent())
                .accept();

        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(ExpectedConditions.numberOfElementsToBe(
                        By.cssSelector("table tbody tr"), initial));
    }

    @Test
    void coursesListPageDisplaysAllAndNavigatesToProfile() {
        driver.get(baseUrl() + "/courses");


        assertThat(driver.getTitle()).isEqualTo("Курсы — University Schedule");
        WebElement navActive = driver.findElement(By.cssSelector(".navbar-nav .nav-link.active"));
        assertThat(navActive.getText()).isEqualTo("Курсы");


        List<String> headers = driver.findElements(By.cssSelector("table thead th"))
                .stream().map(WebElement::getText).toList();
        assertThat(headers).containsExactly(
                "ID","Название","Поток","Группа","Спецкурс","Интенсивность","Год","Действия"
        );


        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(2);


        WebElement first = rows.get(0);
        List<String> cols = first.findElements(By.tagName("td"))
                .stream().map(WebElement::getText).toList();
        assertThat(cols.get(1)).isEqualTo(course1.getName());
        assertThat(cols.get(2)).isEqualTo(st1.getName());
        assertThat(cols.get(3)).isEqualTo(gr1.getName());
        assertThat(cols.get(4)).isEqualTo("Нет");
        assertThat(cols.get(5)).isEqualTo("3");
        assertThat(cols.get(6)).isEqualTo("1");


        first.findElement(By.cssSelector("a.btn-info")).click();
        assertThat(driver.getCurrentUrl())
                .endsWith("/courses/" + course1.getId());
    }

    @Test
    void filterFormRetainsValues() {
        String params = String.format(
                "?streamId=%d&groupId=%d&special=true",
                st1.getId(), gr1.getId()
        );
        driver.get(baseUrl() + "/courses" + params);


        WebElement selStream = driver.findElement(By.name("streamId"));
        assertThat(selStream.getAttribute("value"))
                .isEqualTo(st1.getId().toString());

        WebElement selGroup = driver.findElement(By.name("groupId"));
        assertThat(selGroup.getAttribute("value"))
                .isEqualTo(gr1.getId().toString());

        WebElement selSpecial = driver.findElement(By.name("special"));
        assertThat(selSpecial.getAttribute("value"))
                .isEqualTo("true");


        driver.findElement(By.cssSelector("button[type=submit]")).click();
        assertThat(driver.getCurrentUrl()).contains("streamId=").contains("special=true");
    }

    @Test
    void newCourseFormIsEmptyAndCancelGoesBack() {
        driver.get(baseUrl() + "/courses/new");

        assertThat(driver.getTitle()).isEqualTo("Новый курс");
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo("Добавить курс");


        assertThat(driver.findElement(By.name("name")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.name("weeklyIntensity")).getAttribute("value")).isEqualTo("0");
        assertThat(driver.findElement(By.name("yearOfStudy")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.name("stream.id")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.name("group.id")).getAttribute("value")).isEmpty();


        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/courses");
    }

    @Test
    void editCourseFormIsPopulatedAndCancelGoesBack() {
        driver.get(baseUrl() + "/courses/" + course1.getId() + "/edit");

        assertThat(driver.getTitle())
                .isEqualTo("Редактировать курс: " + course1.getName());
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo("Редактировать курс");


        assertThat(driver.findElement(By.name("name")).getAttribute("value"))
                .isEqualTo(course1.getName());
        assertThat(driver.findElement(By.name("weeklyIntensity")).getAttribute("value"))
                .isEqualTo(String.valueOf(course1.getWeeklyIntensity()));
        assertThat(driver.findElement(By.name("yearOfStudy")).getAttribute("value"))
                .isEqualTo(course1.getYearOfStudy().toString());
        assertThat(driver.findElement(By.name("stream.id")).getAttribute("value"))
                .isEqualTo(st1.getId().toString());
        assertThat(driver.findElement(By.name("group.id")).getAttribute("value"))
                .isEqualTo(gr1.getId().toString());

        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/courses");
    }

    @Test
    void courseProfilePageShowsDetailsAndLinks() {
        driver.get(baseUrl() + "/courses/" + course1.getId());

        assertThat(driver.getTitle())
                .isEqualTo("Курс: " + course1.getName());
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo("Курс: " + course1.getName());


        List<WebElement> rows = driver.findElements(By.cssSelector("table.table-borderless tr"));

        assertThat(rows.get(0).findElement(By.tagName("td")).getText()).isEqualTo(st1.getName());

        assertThat(rows.get(1).findElement(By.tagName("td")).getText()).isEqualTo(gr1.getName());

        assertThat(rows.get(2).findElement(By.tagName("td")).getText()).isEqualTo("Нет");

        assertThat(rows.get(3).findElement(By.tagName("td")).getText()).isEqualTo("3");

        assertThat(rows.get(4).findElement(By.tagName("td")).getText()).isEqualTo("1");


        WebElement edit = driver.findElement(By.cssSelector("a.btn-secondary"));
        assertThat(edit.getAttribute("href"))
                .endsWith("/courses/" + course1.getId() + "/edit");

        WebElement sched = driver.findElement(By.cssSelector("a.btn-primary"));
        assertThat(sched.getAttribute("href"))
                .endsWith("/courses/" + course1.getId() + "/schedule");
    }

    @Test
    void courseSchedulePageShowsLessonsAndBack() {
        driver.get(baseUrl() + "/courses/" + course1.getId() + "/schedule");

        assertThat(driver.getTitle())
                .isEqualTo("Расписание курса: " + course1.getName());

        List<String> hdr = driver.findElements(By.cssSelector("table thead th"))
                .stream().map(WebElement::getText).toList();
        assertThat(hdr).containsExactly("Дата","Время","Преподаватель","Аудитория","Удалить");


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

        assertThat(cols.get(2).getText()).isEqualTo(teacher1.getFullName());
        assertThat(cols.get(3).getText()).isEqualTo(aud1.getNumber());


        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/courses");
    }

    @Test
    void emptyScheduleShowsMessage() {

        lessonDAO.getAll().forEach(lessonDAO::delete);
        driver.get(baseUrl() + "/courses/" + course1.getId() + "/schedule");

        WebElement tr = driver.findElement(By.cssSelector("table tbody tr"));
        assertThat(tr.getText()).isEqualTo("Нет занятий");
        assertThat(tr.findElement(By.tagName("td"))
                .getAttribute("colspan")).isEqualTo("5");
    }
}
