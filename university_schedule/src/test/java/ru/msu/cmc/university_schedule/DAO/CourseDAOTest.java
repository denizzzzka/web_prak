package ru.msu.cmc.university_schedule.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.university_schedule.DAO.impl.CourseDAOImpl;
import ru.msu.cmc.university_schedule.entities.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class CourseDAOTest {

    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private CourseDAOImpl courseDAOImpl;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private StreamDAO streamDAO;
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private TeacherDAO teacherDAO;
    @Autowired
    private StudentDAO studentDAO;

    private Stream stream;
    private Group group;
    private Course course1, course2, course3;

    @BeforeEach
    void beforeEach() {
        // Создаем потоки и группы
        stream = new Stream(null, "Поток 1");
        streamDAO.save(stream);

        group = new Group(null, "Группа 1", stream);
        groupDAO.save(group);

        // Создаем курсы
        course1 = new Course(null, "Программирование", stream, group, false, 2, 1);
        course2 = new Course(null, "Математика", stream, group, true, 3, 1);
        course3 = new Course(null, "Физика", stream, group, false, 2, 2);

        courseDAO.saveCollection(List.of(course1, course2, course3));
    }

    @AfterEach
    void cleanup() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Очищаем таблицы
            session.createNativeQuery("TRUNCATE lesson_students RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE student_courses RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE teacher_courses RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE lessons RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE courses RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE students RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE teachers RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE groups RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE streams RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE auditoriums RESTART IDENTITY CASCADE;").executeUpdate();

            // Сбрасываем ID
            session.createNativeQuery("ALTER SEQUENCE streams_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE groups_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE students_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE auditoriums_id_seq RESTART WITH 1;").executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Test
    void testFindByStream() {
        // Получаем курсы по потоку
        List<Course> courses = courseDAOImpl.findByStream(stream.getId());

        // Проверяем, что курсы связаны с правильным потоком
        assertEquals(3, courses.size());
        assertTrue(courses.stream().allMatch(course -> course.getStream().getId().equals(stream.getId())));
    }

    @Test
    void testFindByGroup() {
        // Получаем курсы по группе
        List<Course> courses = courseDAOImpl.findByGroup(group.getId());

        // Проверяем, что курсы связаны с правильной группой
        assertEquals(3, courses.size());
        assertTrue(courses.stream().allMatch(course -> course.getGroup().getId().equals(group.getId())));
    }

    @Test
    void testFindBySpecialCourse() {
        // Получаем курсы, которые являются специальными
        List<Course> specialCourses = courseDAOImpl.findBySpecialCourse(true);

        // Проверяем, что курсы имеют флаг specialCourse == true
        assertEquals(1, specialCourses.size());
        assertTrue(specialCourses.stream().allMatch(Course::isSpecialCourse));
    }

    @Test
    void testFindBySpecialCourseFalse() {
        // Получаем курсы, которые не являются специальными
        List<Course> nonSpecialCourses = courseDAOImpl.findBySpecialCourse(false);

        // Проверяем, что курсы имеют флаг specialCourse == false
        assertEquals(2, nonSpecialCourses.size());
        assertTrue(nonSpecialCourses.stream().allMatch(course -> !course.isSpecialCourse()));
    }
}
