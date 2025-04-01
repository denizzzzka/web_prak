package ru.msu.cmc.university_schedule.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.university_schedule.DAO.impl.AuditoriumDAOImpl;
import ru.msu.cmc.university_schedule.entities.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class AuditoriumDAOTest {

    @Autowired
    private AuditoriumDAO auditoriumDAO;
    @Autowired
    private LessonDAO lessonDAO;
    @Autowired
    private AuditoriumDAOImpl auditoriumDAOImpl;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private TeacherDAO teacherDAO;
    @Autowired
    private StreamDAO streamDAO;
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private StudentDAO studentDAO;

    private Auditorium auditorium1, auditorium2, auditorium3;
    private Course course;
    private Teacher teacher;
    private Lesson lesson1;
    private Stream stream;
    private Group group;

    @BeforeEach
    void beforeEach() {
        // Создаем потоки и группы
        stream = new Stream(null, "Поток 1");
        streamDAO.save(stream);

        group = new Group(null, "Группа 1", stream);
        groupDAO.save(group);

        // Создаем аудитории
        auditorium1 = new Auditorium(null, "Аудитория 101", 30);
        auditorium2 = new Auditorium(null, "Аудитория 102", 25);
        auditorium3 = new Auditorium(null, "Аудитория 103", 40);
        auditoriumDAO.saveCollection(List.of(auditorium1, auditorium2, auditorium3));

        // Создаем курс и связываем с потоком и группой
        course = new Course(null, "Программирование", stream, group, false, 2, 1);
        courseDAO.save(course);

        // Создаем преподавателя
        teacher = new Teacher(null, "Иван Иванович");
        teacherDAO.save(teacher);

        // Создаем урок
        lesson1 = new Lesson();
        lesson1.setCourse(course);
        lesson1.setTeacher(teacher);
        lesson1.setAuditorium(auditorium1);
        lesson1.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        lesson1.setEndTime(LocalDateTime.of(2025, 4, 1, 12, 0));
        lessonDAO.save(lesson1);
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
    void testGetAvailableAuditoriums() {
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 4, 1, 10, 0);

        // Получаем доступные аудитории, пока нет пересечений
        List<Auditorium> availableAuditoriums = auditoriumDAOImpl.getAvailableAuditoriums(startTime, endTime);
        assertEquals(3, availableAuditoriums.size());  // Все аудитории свободны

        // Обновляем время так, чтобы одна аудитория была занята
        lesson1.setStartTime(LocalDateTime.of(2025, 4, 1, 9, 0));
        lesson1.setEndTime(LocalDateTime.of(2025, 4, 1, 11, 0));
        lessonDAO.save(lesson1);

        // Получаем доступные аудитории после того, как одна уже занята
        availableAuditoriums = auditoriumDAOImpl.getAvailableAuditoriums(startTime, endTime);
        assertEquals(2, availableAuditoriums.size());  // Аудитория 101 занята, остальные свободны
    }

    @Test
    void testGetAvailableAuditoriumsWithNoAvailable() {
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 4, 1, 12, 0);

        // Занимаем все аудитории
        Lesson lesson2 = new Lesson();
        lesson2.setCourse(course);
        lesson2.setTeacher(teacher);
        lesson2.setAuditorium(auditorium2);
        lesson2.setStartTime(startTime);
        lesson2.setEndTime(endTime);
        lessonDAO.save(lesson2);

        Lesson lesson3 = new Lesson();
        lesson3.setCourse(course);
        lesson3.setTeacher(teacher);
        lesson3.setAuditorium(auditorium3);
        lesson3.setStartTime(startTime);
        lesson3.setEndTime(endTime);
        lessonDAO.save(lesson3);

        // Получаем доступные аудитории (все заняты)
        List<Auditorium> availableAuditoriums = auditoriumDAOImpl.getAvailableAuditoriums(startTime, endTime);
        assertEquals(0, availableAuditoriums.size());  // Все аудитории заняты
    }

    @Test
    void testGetAvailableAuditoriumsWithOneAvailable() {
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 4, 1, 12, 0);

        // Занимаем одну аудиторию
        Lesson lesson2 = new Lesson();
        lesson2.setCourse(course);
        lesson2.setTeacher(teacher);
        lesson2.setAuditorium(auditorium2);
        lesson2.setStartTime(startTime);
        lesson2.setEndTime(endTime);
        lessonDAO.save(lesson2);

        // Получаем доступные аудитории (только одна аудитория свободна)
        List<Auditorium> availableAuditoriums = auditoriumDAOImpl.getAvailableAuditoriums(startTime, endTime);
        assertEquals(1, availableAuditoriums.size());  // Аудитория 102 занята, остальные свободны
    }
}
