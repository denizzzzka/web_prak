package ru.msu.cmc.university_schedule;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class HibernateDatabaseConfigTest {

    @Autowired
    private SessionFactory sessionFactory;
    @Test
    void testSessionFactory() {
        assertNotNull(sessionFactory, "SessionFactory не должен быть null");

        try (Session session = sessionFactory.openSession()) {
            assertNotNull(session, "Сессия не должна быть null");
        }
    }
}
