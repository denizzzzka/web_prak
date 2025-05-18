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
public class StudentPageTest extends SeleniumTestBase {

    @LocalServerPort
    private int port;

    @Autowired private StudentDAO        studentDAO;
    @Autowired private CourseDAO         courseDAO;
    @Autowired private StreamDAO         streamDAO;
    @Autowired private GroupDAO          groupDAO;
    @Autowired private TeacherDAO        teacherDAO;
    @Autowired private AuditoriumDAO     auditoriumDAO;
    @Autowired private LessonDAO         lessonDAO;
    @Autowired private LessonStudentDAO  lessonStudentDAO;

    private Stream    stream1;
    private Group     group1;
    private Student   student1;
    private Course    course1, course2;
    private Teacher   teacher1;
    private Auditorium aud1;
    private Lesson    lesson1;
    private LessonStudent lessonStudent1;

    @BeforeEach
    void prepareData() {

        lessonStudentDAO.getAll().forEach(lessonStudentDAO::delete);
        lessonDAO.getAll().forEach(lessonDAO::delete);
        studentDAO.getAll().forEach(studentDAO::delete);
        courseDAO.getAll().forEach(courseDAO::delete);
        groupDAO.getAll().forEach(groupDAO::delete);
        streamDAO.getAll().forEach(streamDAO::delete);
        teacherDAO.getAll().forEach(teacherDAO::delete);
        auditoriumDAO.getAll().forEach(auditoriumDAO::delete);


        stream1 = new Stream();
        stream1.setName("ИТ");
        streamDAO.save(stream1);

        group1 = new Group();
        group1.setName("Группа A");
        group1.setStream(stream1);
        groupDAO.save(group1);


        student1 = new Student();
        student1.setFullName("Иванов И.И.");
        student1.setYearOfStudy(2);
        student1.setStream(stream1);
        student1.setGroup(group1);
        studentDAO.save(student1);


        course1 = new Course();
        course1.setName("Курс 1");
        course1.setStream(stream1);
        course1.setGroup(group1);
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

        aud1 = new Auditorium(null, "301", 30);
        auditoriumDAO.save(aud1);


        LocalDateTime start = LocalDate.of(2025, 4, 2).atTime(9, 0);
        LocalDateTime end   = LocalDate.of(2025, 4, 2).atTime(11, 0);
        lesson1 = new Lesson();
        lesson1.setCourse(course1);
        lesson1.setTeacher(teacher1);
        lesson1.setAuditorium(aud1);
        lesson1.setStartTime(start);
        lesson1.setEndTime(end);
        lessonDAO.save(lesson1);

        lessonStudent1 = new LessonStudent();

        LessonStudentId key = new LessonStudentId();
        key.setLessonId(lesson1.getId());
        key.setStudentId(student1.getId());
        lessonStudent1.setId(key);

        lessonStudent1.setLesson(lesson1);
        lessonStudent1.setStudent(student1);
        lessonStudentDAO.save(lessonStudent1);
    }


    @AfterAll
    void cleanupData() {

        lessonStudentDAO.getAll().forEach(lessonStudentDAO::delete);
        lessonDAO.getAll().forEach(lessonDAO::delete);
        studentDAO.getAll().forEach(studentDAO::delete);
        courseDAO.getAll().forEach(courseDAO::delete);
        groupDAO.getAll().forEach(groupDAO::delete);
        streamDAO.getAll().forEach(streamDAO::delete);

        teacherDAO.getAll().forEach(teacherDAO::delete);
        auditoriumDAO.getAll().forEach(auditoriumDAO::delete);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void studentsListPageDisplaysAllAndNavigatesToProfile() {
        driver.get(baseUrl() + "/students");


        assertThat(driver.getTitle()).isEqualTo("Студенты — University Schedule");
        WebElement navActive = driver.findElement(By.cssSelector(".navbar-nav .nav-link.active"));
        assertThat(navActive.getText()).isEqualTo("Студенты");


        List<String> headers = driver.findElements(By.cssSelector("table thead th"))
                .stream().map(WebElement::getText).toList();
        assertThat(headers).containsExactly("ID", "ФИО", "Поток", "Группа", "Действия");


        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(1);


        WebElement first = rows.get(0);
        List<String> cols = first.findElements(By.tagName("td"))
                .stream().map(WebElement::getText).toList();
        assertThat(cols.get(1)).isEqualTo(student1.getFullName());
        assertThat(cols.get(2)).isEqualTo(stream1.getName());
        assertThat(cols.get(3)).isEqualTo(group1.getName());


        WebElement btn = first.findElement(By.cssSelector("a.btn-info"));
        btn.click();
        assertThat(driver.getCurrentUrl())
                .endsWith("/students/" + student1.getId() + "/schedule");
    }

    @Test
    void filterFormRetainsValues() {
        String params = String.format("?streamId=%d&groupId=%d&search=Иван",
                stream1.getId(), group1.getId());

        driver.get(baseUrl() + "/students" + params);

        assertThat(driver.findElement(By.name("streamId")).getAttribute("value"))
                .isEqualTo(stream1.getId().toString());
        assertThat(driver.findElement(By.name("groupId")).getAttribute("value"))
                .isEqualTo(group1.getId().toString());
        assertThat(driver.findElement(By.name("search")).getAttribute("value"))
                .isEqualTo("Иван");

    }

    @Test
    void newStudentFormIsEmptyAndCancelGoesBack() {
        driver.get(baseUrl() + "/students/new");

        assertThat(driver.getTitle()).isEqualTo("Новый студент");
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo("Добавить нового студента");

        assertThat(driver.findElement(By.name("fullName")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.name("yearOfStudy")).getAttribute("value")).isEqualTo("0");
        assertThat(driver.findElement(By.name("stream.id")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.name("group.id")).getAttribute("value")).isEmpty();

        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/students");
    }

    @Test
    void editStudentFormIsPopulatedAndCancelGoesBack() {
        driver.get(baseUrl() + "/students/" + student1.getId() + "/edit");

        assertThat(driver.getTitle()).isEqualTo("Редактировать студента");
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo("Редактирование студента");

        assertThat(driver.findElement(By.name("fullName")).getAttribute("value"))
                .isEqualTo(student1.getFullName());
        assertThat(driver.findElement(By.name("yearOfStudy")).getAttribute("value"))
                .isEqualTo(String.valueOf(student1.getYearOfStudy()));
        assertThat(driver.findElement(By.name("stream.id")).getAttribute("value"))
                .isEqualTo(stream1.getId().toString());
        assertThat(driver.findElement(By.name("group.id")).getAttribute("value"))
                .isEqualTo(group1.getId().toString());

        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/students");
    }

    @Test
    void studentProfilePageShowsDetailsAndLinks() {
        driver.get(baseUrl() + "/students/" + student1.getId());

        assertThat(driver.getTitle())
                .isEqualTo("Профиль: " + student1.getFullName());
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo(student1.getFullName());

        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));

        assertThat(rows.get(0).findElement(By.tagName("td")).getText())
                .isEqualTo(String.valueOf(student1.getYearOfStudy()));

        assertThat(rows.get(1).findElement(By.tagName("td")).getText())
                .isEqualTo(stream1.getName());

        assertThat(rows.get(2).findElement(By.tagName("td")).getText())
                .isEqualTo(group1.getName());


        WebElement enroll = driver.findElement(By.cssSelector("a.btn-success"));
        assertThat(enroll.getAttribute("href"))
                .endsWith("/students/" + student1.getId() + "/enroll");

        WebElement edit = driver.findElement(By.cssSelector("a.btn-primary"));
        assertThat(edit.getAttribute("href"))
                .endsWith("/students/" + student1.getId() + "/edit");

        WebElement sched = driver.findElement(By.cssSelector("a.btn-info"));
        assertThat(sched.getAttribute("href"))
                .endsWith("/students/" + student1.getId() + "/schedule");
    }

    @Test
    void enrollPageShowsCoursesAndBack() {
        driver.get(baseUrl() + "/students/" + student1.getId() + "/enroll");


        List<String> hdr = driver.findElements(By.cssSelector("table thead th"))
                .stream().map(WebElement::getText).toList();
        assertThat(hdr).containsExactly(
                "ID","Название курса","Поток","Группа","Спецкурс","Интенсивность",""
        );

        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(2);

        WebElement first = rows.get(0);
        List<String> cols = first.findElements(By.tagName("td"))
                .stream().map(WebElement::getText).toList();
        assertThat(cols.get(1)).isEqualTo(course1.getName());
        assertThat(cols.get(2)).isEqualTo(stream1.getName());
        assertThat(cols.get(3)).isEqualTo(group1.getName());
        assertThat(cols.get(4)).isEqualTo("Нет");
        assertThat(cols.get(5)).isEqualTo("3");


        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl())
                .endsWith("/students/" + student1.getId());
    }

    @Test
    void schedulePageShowsLessonsAndBack() {
        driver.get(baseUrl() + "/students/" + student1.getId() + "/schedule");

        assertThat(driver.getTitle())
                .isEqualTo("Расписание: " + student1.getFullName());
        assertThat(driver.findElement(By.tagName("h1")).getText())
                .isEqualTo("Расписание студента");

        List<String> hdr = driver.findElements(By.cssSelector("table thead th"))
                .stream().map(WebElement::getText).toList();
        assertThat(hdr).containsExactly("Дата","Время","Курс","Преподаватель","Аудитория");

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
        assertThat(cols.get(3).getText()).isEqualTo(teacher1.getFullName());
        assertThat(cols.get(4).getText()).isEqualTo(aud1.getNumber());

        driver.findElement(By.cssSelector("a.btn-secondary")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/students");
    }

    @Test
    void emptyScheduleShowsMessage() {

        lessonStudentDAO.getAll().forEach(lessonStudentDAO::delete);

        driver.get(baseUrl() + "/students/" + student1.getId() + "/schedule");

        WebElement tr = driver.findElement(By.cssSelector("table tbody tr"));
        assertThat(tr.getText()).isEqualTo("Нет занятий");
        assertThat(tr.findElement(By.tagName("td"))
                .getAttribute("colspan")).isEqualTo("5");
    }
}
