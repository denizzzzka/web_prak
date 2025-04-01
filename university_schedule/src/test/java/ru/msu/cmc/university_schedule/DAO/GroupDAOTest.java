package ru.msu.cmc.university_schedule.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.university_schedule.DAO.impl.GroupDAOImpl;
import ru.msu.cmc.university_schedule.entities.*;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class GroupDAOTest {

    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private GroupDAOImpl groupDAOImpl;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private StreamDAO streamDAO;

    private Stream stream;
    private Group group1, group2, group3;

    @BeforeEach
    void beforeEach() {
        // Создаем потоки
        stream = new Stream(null, "Поток 1");
        streamDAO.save(stream);

        // Создаем группы и связываем с потоком
        group1 = new Group(null, "Группа 1", stream);
        group2 = new Group(null, "Группа 2", stream);
        group3 = new Group(null, "Группа 3", stream);
        groupDAO.saveCollection(List.of(group1, group2, group3));
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
        // Получаем группы по потоку
        List<Group> groups = groupDAOImpl.findByStream(stream);

        // Проверяем, что все группы принадлежат правильному потоку
        assertEquals(3, groups.size());
        assertTrue(groups.stream().allMatch(group -> group.getStream().getId().equals(stream.getId())));
    }

    @Test
    void testFindByName() {
        // Получаем группы по префиксу имени
        List<Group> groups = groupDAOImpl.findByName("Груп");

        // Проверяем, что все найденные группы имеют имя, начинающееся с "Груп"
        assertEquals(3, groups.size());
        assertTrue(groups.stream().allMatch(group -> group.getName().startsWith("Груп")));
    }

    @Test
    void testGetAll() {
        // Получаем все группы
        Collection<Group> groups = groupDAO.getAll();

        // Проверяем, что все группы сохранены
        assertNotNull(groups);
        assertTrue(groups.size() > 0, "Группы не должны быть пустыми");
        assertTrue(groups.stream().anyMatch(group -> group.getId() != null), "Группа должна иметь id");
    }

    @Test
    void testDeleteById() {
        // Удаляем одну группу
        groupDAO.deleteById(group1.getId());

        // Проверяем, что группа удалена
        Group deletedGroup = groupDAO.getById(group1.getId());
        assertNull(deletedGroup, "Группа должна быть удалена");
    }

    @Test
    void testDeleteByIdEntityNotFound() {
        // Создаем ID, которого нет в базе данных (например, больше не существует)
        Long nonExistentId = 999L;

        // Удаляем сущность с несуществующим ID
        groupDAO.deleteById(nonExistentId);
    }

    @Test
    void testUpdate() {
        // Обновляем название группы
        group2.setName("Обновленная группа");
        groupDAO.update(group2);

        // Проверяем, что группа обновлена
        Group updatedGroup = groupDAO.getById(group2.getId());
        assertEquals("Обновленная группа", updatedGroup.getName(), "Имя группы должно быть обновлено");
    }
}
