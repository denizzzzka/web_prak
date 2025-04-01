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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class LessonStudentDAOTest {

    @Autowired
    private LessonStudentDAO lessonStudentDAO;
    @Autowired
    private LessonDAO lessonDAO;
    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    private StreamDAO streamDAO;
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private TeacherDAO teacherDAO;
    @Autowired
    private AuditoriumDAO auditoriumDAO;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private SessionFactory sessionFactory;

    private Stream stream;
    private Group group;
    private Student student1, student2;
    private Teacher teacher;
    private Auditorium auditorium;
    private Course course;
    private Lesson lesson1, lesson2;

    @BeforeEach
    void beforeEach() {
        // Создаем поток и группу
        stream = new Stream(null, "Поток 1");
        streamDAO.save(stream);

        group = new Group(null, "Группа 1", stream);
        groupDAO.save(group);

        // Создаем студентов
        student1 = new Student(null, "Студент 1", 1, stream, group);
        student2 = new Student(null, "Студент 2", 1, stream, group);
        studentDAO.saveCollection(List.of(student1, student2));

        // Создаем преподавателя и аудиторию
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
        lessonDAO.saveCollection(List.of(lesson1, lesson2));

        // Записываем студентов на уроки
        lessonStudentDAO.save(new LessonStudent(new LessonStudentId(lesson1.getId(), student1.getId()), lesson1, student1));
        lessonStudentDAO.save(new LessonStudent(new LessonStudentId(lesson1.getId(), student2.getId()), lesson1, student2));
        lessonStudentDAO.save(new LessonStudent(new LessonStudentId(lesson2.getId(), student1.getId()), lesson2, student1));
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
        // Получаем записи из lesson_students по студенту
        List<LessonStudent> lessonStudents = lessonStudentDAO.findByStudent(student1.getId());

        // Проверяем количество записей
        assertEquals(2, lessonStudents.size());

        // Проверяем ID уроков, к которым привязан студент
        List<Long> lessonIds = lessonStudents.stream().map(ls -> ls.getLesson().getId()).collect(Collectors.toList());
        assertTrue(lessonIds.contains(lesson1.getId()));
        assertTrue(lessonIds.contains(lesson2.getId()));
    }

    @Test
    void testFindByLesson() {
        // Получаем записи из lesson_students по уроку
        List<LessonStudent> lessonStudents = lessonStudentDAO.findByLesson(lesson1.getId());

        // Проверяем количество записей
        assertEquals(2, lessonStudents.size());

        // Проверяем ID студентов, записанных на урок
        List<Long> studentIds = lessonStudents.stream().map(ls -> ls.getStudent().getId()).collect(Collectors.toList());
        assertTrue(studentIds.contains(student1.getId()));
        assertTrue(studentIds.contains(student2.getId()));
    }
}
