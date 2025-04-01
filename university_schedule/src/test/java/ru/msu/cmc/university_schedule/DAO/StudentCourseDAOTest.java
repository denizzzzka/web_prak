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
public class StudentCourseDAOTest {

    @Autowired
    private StudentCourseDAO studentCourseDAO;
    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private StreamDAO streamDAO;
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private SessionFactory sessionFactory;

    private Stream stream;
    private Group group;
    private Student student1, student2;
    private Course course1, course2;
    private StudentCourse studentCourse1, studentCourse2, studentCourse3;

    @BeforeEach
    void beforeEach() {
        // Создаем поток и группу
        stream = new Stream(null, "Информатика");
        streamDAO.save(stream);

        group = new Group(null, "Группа 1", stream);
        groupDAO.save(group);

        // Создаем студентов
        student1 = new Student(null, "Студент 1", 1, stream, group);
        student2 = new Student(null, "Студент 2", 2, stream, group);
        studentDAO.saveCollection(List.of(student1, student2));

        // Создаем курсы
        course1 = new Course(null, "Алгоритмы", stream, group, false, 3, 1);
        course2 = new Course(null, "Базы данных", stream, group, false, 2, 2);
        courseDAO.saveCollection(List.of(course1, course2));

        // Записываем студентов на курсы
        studentCourse1 = new StudentCourse(new StudentCourseId(student1.getId(), course1.getId(), 2024), student1, course1, 2024);
        studentCourse2 = new StudentCourse(new StudentCourseId(student2.getId(), course2.getId(), 2024), student2, course2, 2024);
        studentCourse3 = new StudentCourse(new StudentCourseId(student1.getId(), course2.getId(), 2023), student1, course2, 2023);
        studentCourseDAO.saveCollection(List.of(studentCourse1, studentCourse2, studentCourse3));
    }

    @AfterEach
    void cleanup() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Очищаем таблицы
            session.createNativeQuery("TRUNCATE student_courses RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE courses RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE students RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE groups RESTART IDENTITY CASCADE;").executeUpdate();
            session.createNativeQuery("TRUNCATE streams RESTART IDENTITY CASCADE;").executeUpdate();

            // Сбрасываем ID
            session.createNativeQuery("ALTER SEQUENCE streams_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE groups_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE students_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE courses_id_seq RESTART WITH 1;").executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Test
    void testFindByStudent() {
        // Получаем курсы студента1
        List<StudentCourse> foundCourses = studentCourseDAO.findByStudent(student1.getId());

        assertEquals(2, foundCourses.size());

        List<Long> foundCourseIds = foundCourses.stream().map(sc -> sc.getCourse().getId()).collect(Collectors.toList());
        assertTrue(foundCourseIds.contains(course1.getId()));
        assertTrue(foundCourseIds.contains(course2.getId()));
    }

    @Test
    void testFindByCourse() {
        // Получаем студентов, записанных на course2
        List<StudentCourse> foundStudents = studentCourseDAO.findByCourse(course2.getId());

        assertEquals(2, foundStudents.size());

        List<Long> foundStudentIds = foundStudents.stream().map(sc -> sc.getStudent().getId()).collect(Collectors.toList());
        assertTrue(foundStudentIds.contains(student1.getId()));
        assertTrue(foundStudentIds.contains(student2.getId()));
    }

    @Test
    void testFindByYear() {
        // Получаем курсы за 2024 год
        List<StudentCourse> foundByYear = studentCourseDAO.findByYear(2024);

        assertEquals(2, foundByYear.size());

        List<Long> foundStudentIds = foundByYear.stream().map(sc -> sc.getStudent().getId()).collect(Collectors.toList());
        assertTrue(foundStudentIds.contains(student1.getId()));
        assertTrue(foundStudentIds.contains(student2.getId()));
    }

    @Test
    void testFindByYearNoMatch() {
        // Ищем записи за 2025 год (должно быть пусто)
        List<StudentCourse> foundByYear = studentCourseDAO.findByYear(2025);

        assertTrue(foundByYear.isEmpty());
    }
}
