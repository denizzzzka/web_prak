package ru.msu.cmc.university_schedule.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.university_schedule.entities.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class TeacherDAOTest {

    @Autowired
    private TeacherDAO teacherDAO;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private TeacherCourseDAO teacherCourseDAO;
    @Autowired
    private SessionFactory sessionFactory;

    private Teacher teacher1, teacher2;
    private Course course1, course2;
    private TeacherCourse teacherCourse1, teacherCourse2;

    @BeforeEach
    void beforeEach() {
        // Создаем преподавателей
        teacher1 = new Teacher(null, "Преподаватель 1");
        teacher2 = new Teacher(null, "Преподаватель 2");
        teacherDAO.saveCollection(List.of(teacher1, teacher2));

        // Создаем курсы
        course1 = new Course(null, "Программирование", null, null, false, 3, 1);
        course2 = new Course(null, "Алгоритмы", null, null, false, 2, 2);
        courseDAO.saveCollection(List.of(course1, course2));

        // Записываем преподавателей на курсы
        teacherCourse1 = new TeacherCourse(new TeacherCourseId(teacher1.getId(), course1.getId(), 2024), teacher1, course1, 2024);
        teacherCourse2 = new TeacherCourse(new TeacherCourseId(teacher2.getId(), course2.getId(), 2024), teacher2, course2, 2024);
        teacherCourseDAO.saveCollection(List.of(teacherCourse1, teacherCourse2));
    }

    @AfterEach
    void cleanup() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Очищаем таблицы
            session.createNativeQuery("TRUNCATE teacher_courses RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE courses RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE teachers RESTART IDENTITY CASCADE;").executeUpdate();

            // Сбрасываем ID
            session.createNativeQuery("ALTER SEQUENCE teachers_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE courses_id_seq RESTART WITH 1;").executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Test
    void testFindByCourse() {
        // Получаем преподавателей, которые ведут курс1
        List<Teacher> foundTeachers = teacherDAO.findByCourse(course1.getId());

        assertEquals(1, foundTeachers.size());
        assertEquals(teacher1.getId(), foundTeachers.get(0).getId());
    }

    @Test
    void testFindByName() {
        // Ищем преподавателей с именем, начинающимся на "Преподаватель"
        List<Teacher> foundTeachers = teacherDAO.findByName("Преподаватель");

        assertEquals(2, foundTeachers.size());

        List<Long> foundTeacherIds = foundTeachers.stream().map(Teacher::getId).collect(Collectors.toList());
        assertTrue(foundTeacherIds.contains(teacher1.getId()));
        assertTrue(foundTeacherIds.contains(teacher2.getId()));
    }

    @Test
    void testFindByNameNoMatch() {
        // Ищем преподавателей с именем, начинающимся на "Доктор"
        List<Teacher> foundTeachers = teacherDAO.findByName("Доктор");

        assertTrue(foundTeachers.isEmpty());
    }
}
