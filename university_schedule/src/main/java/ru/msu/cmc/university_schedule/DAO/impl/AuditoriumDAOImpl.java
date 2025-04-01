package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.AuditoriumDAO;
import ru.msu.cmc.university_schedule.entities.Auditorium;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AuditoriumDAOImpl extends AbstractDAOImpl<Auditorium, Long> implements AuditoriumDAO {

    public AuditoriumDAOImpl() {
        super(Auditorium.class);
    }

    @Override
    public List<Auditorium> getAvailableAuditoriums(LocalDateTime start, LocalDateTime end) {
        try (Session session = getSession()) {
            return session.createQuery(
                            "SELECT a FROM Auditorium a WHERE a.id NOT IN " +
                                    "(SELECT l.auditorium.id FROM Lesson l WHERE " +
                                    "(l.startTime < :end AND l.endTime > :start))", Auditorium.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
        }
    }
}
