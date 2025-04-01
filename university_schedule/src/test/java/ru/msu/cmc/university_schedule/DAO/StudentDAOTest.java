package ru.msu.cmc.university_schedule.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.university_schedule.entities.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class StudentDAOTest {

    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    private StreamDAO streamDAO;
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private TeacherDAO teacherDAO;
    @Autowired
    private AuditoriumDAO auditoriumDAO;
    @Autowired
    private LessonDAO lessonDAO;
    @Autowired
    private SessionFactory sessionFactory;

    private Stream stream1, stream2;
    private Group group1, group2;
    private Student student1, student2, student3;

    @BeforeEach
    void beforeEach() {
        // Создаем потоки
        stream1 = new Stream(null, "Информационные технологии");
        stream2 = new Stream(null, "Прикладная математика");
        streamDAO.saveCollection(List.of(stream1, stream2));

        // Создаем группы
        group1 = new Group(null, "ИТ-101", stream1);
        group2 = new Group(null, "ПМ-201", stream2);
        groupDAO.saveCollection(List.of(group1, group2));

        // Создаем студентов
        student1 = new Student(null, "Иван Иванов", 1, stream1, group1);
        student2 = new Student(null, "Мария Петрова", 2, stream1, group1);
        student3 = new Student(null, "Алексей Сидоров", 2, stream2, group2);
        studentDAO.saveCollection(List.of(student1, student2, student3));
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

            session.getTransaction().commit();
        }
    }


    @Test
    void testFindByName() {
        List<Student> students = studentDAO.findByName("Иван Иванов");
        assertEquals(1, students.size());
        assertEquals("Иван Иванов", students.get(0).getFullName());
    }

    @Test
    void testFindByGroup() {
        List<Student> students = studentDAO.findByGroup(group1.getId());
        assertEquals(2, students.size()); // В группе "ИТ-101" два студента
    }

    @Test
    void testFindByStream() {
        List<Student> studentsStream1 = studentDAO.findByStream(stream1.getId());
        List<Student> studentsStream2 = studentDAO.findByStream(stream2.getId());

        assertEquals(2, studentsStream1.size()); // В потоке "Информационные технологии" два студента
        assertEquals(1, studentsStream2.size()); // В потоке "Прикладная математика" один студент
    }

    @Test
    void testDeleteStudent() {
        Student student = studentDAO.getById(student1.getId());
        assertNotNull(student);

        studentDAO.delete(student);

        Student deletedStudent = studentDAO.getById(student1.getId());
        assertNull(deletedStudent);
    }
}
