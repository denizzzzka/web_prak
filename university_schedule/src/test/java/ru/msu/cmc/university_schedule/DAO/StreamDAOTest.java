package ru.msu.cmc.university_schedule.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.university_schedule.entities.Stream;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class StreamDAOTest {

    @Autowired
    private StreamDAO streamDAO;
    @Autowired
    private SessionFactory sessionFactory;

    private Stream stream1, stream2, stream3;

    @BeforeEach
    void beforeEach() {
        // Создаем потоки
        stream1 = new Stream(null, "Программирование");
        stream2 = new Stream(null, "Программирование 2");
        stream3 = new Stream(null, "Математика");

        streamDAO.saveCollection(List.of(stream1, stream2, stream3));
    }

    @AfterEach
    void cleanup() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Очищаем таблицу
            session.createNativeQuery("TRUNCATE streams RESTART IDENTITY CASCADE;").executeUpdate();

            // Сбрасываем ID
            session.createNativeQuery("ALTER SEQUENCE streams_id_seq RESTART WITH 1;").executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Test
    void testFindByName() {
        // Ищем потоки, начинающиеся с "Программирование"
        List<Stream> foundStreams = streamDAO.findByName("Программирование");

        // Проверяем количество найденных записей
        assertEquals(2, foundStreams.size());

        // Проверяем ID найденных потоков
        List<Long> foundIds = foundStreams.stream().map(Stream::getId).collect(Collectors.toList());
        assertTrue(foundIds.contains(stream1.getId()));
        assertTrue(foundIds.contains(stream2.getId()));
    }

    @Test
    void testFindByNameNoMatch() {
        // Ищем потоки, начинающиеся с "Физика" (нет таких)
        List<Stream> foundStreams = streamDAO.findByName("Физика");

        // Должен вернуться пустой список
        assertTrue(foundStreams.isEmpty());
    }
}
