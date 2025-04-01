package ru.msu.cmc.university_schedule.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.university_schedule.DAO.impl.LessonDAOImpl;
import ru.msu.cmc.university_schedule.entities.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class LessonDAOTest {

    @Autowired
    private LessonDAO lessonDAO;
    @Autowired
    private LessonDAOImpl lessonDAOImpl;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private StreamDAO streamDAO;
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    private TeacherDAO teacherDAO;
    @Autowired
    private AuditoriumDAO auditoriumDAO;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private LessonStudentDAO lessonStudentDAO;

    private Stream stream;
    private Group group;
    private Student student;
    private Teacher teacher;
    private Auditorium auditorium;
    private Course course;
    private Lesson lesson1, lesson2, lesson3;

    @BeforeEach
    void beforeEach() {
        // Создаем поток и группу
        stream = new Stream(null, "Поток 1");
        streamDAO.save(stream);

        group = new Group(null, "Группа 1", stream);
        groupDAO.save(group);

        // Создаем студента, преподавателя и аудиторию
        student = new Student(null, "Студент 1", 1, stream, group);
        studentDAO.save(student);

        teacher = new Teacher(null, "Преподаватель 1");
        teacherDAO.save(teacher);

        auditorium = new Auditorium(null, "Аудитория 101", 30);
        auditoriumDAO.save(auditorium);

        // Создаем курс
        course = new Course(null, "Программирование", stream, group, false, 2, 1);
        courseDAO.save(course);

        // Создаем уроки
        lesson1 = new Lesson(null, course, teacher, auditorium, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        lesson2 = new Lesson(null, course, teacher, auditorium, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3));
        lesson3 = new Lesson(null, course, teacher, auditorium, LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(5));
        lessonDAO.saveCollection(List.of(lesson1, lesson2, lesson3));

        // Связываем студента с уроками
        lessonStudentDAO.save(new LessonStudent(new LessonStudentId(lesson1.getId(), student.getId()), lesson1, student));
        lessonStudentDAO.save(new LessonStudent(new LessonStudentId(lesson2.getId(), student.getId()), lesson2, student));
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
            session.createNativeQuery("ALTER SEQUENCE teachers_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE courses_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE lessons_id_seq RESTART WITH 1;").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE auditoriums_id_seq RESTART WITH 1;").executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Test
    void testFindByStudent() {
        // Получаем уроки по студенту
        List<Lesson> lessons = lessonDAOImpl.findByStudent(student.getId());

        // Получаем ID уроков
        List<Long> lessonIds = lessons.stream().map(Lesson::getId).collect(Collectors.toList());

        // Проверяем, что студент записан на 2 урока
        assertEquals(2, lessons.size());
        assertTrue(lessonIds.contains(lesson1.getId()));
        assertTrue(lessonIds.contains(lesson2.getId()));
    }

    @Test
    void testFindByTeacher() {
        // Получаем уроки по преподавателю
        List<Lesson> lessons = lessonDAOImpl.findByTeacher(teacher.getId());

        // Получаем ID уроков
        List<Long> lessonIds = lessons.stream().map(Lesson::getId).collect(Collectors.toList());

        // Проверяем, что все уроки связаны с этим преподавателем
        assertEquals(3, lessons.size());
        assertTrue(lessonIds.contains(lesson1.getId()));
        assertTrue(lessonIds.contains(lesson2.getId()));
        assertTrue(lessonIds.contains(lesson3.getId()));
    }

    @Test
    void testFindByAuditorium() {
        // Получаем уроки по аудитории
        List<Lesson> lessons = lessonDAOImpl.findByAuditorium(auditorium.getId());

        // Получаем ID уроков
        List<Long> lessonIds = lessons.stream().map(Lesson::getId).collect(Collectors.toList());

        // Проверяем, что все уроки проходят в правильной аудитории
        assertEquals(3, lessons.size());
        assertTrue(lessonIds.contains(lesson1.getId()));
        assertTrue(lessonIds.contains(lesson2.getId()));
        assertTrue(lessonIds.contains(lesson3.getId()));
    }
}
