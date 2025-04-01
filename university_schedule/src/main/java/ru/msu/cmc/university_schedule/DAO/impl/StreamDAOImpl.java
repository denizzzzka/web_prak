package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.StreamDAO;
import ru.msu.cmc.university_schedule.entities.Stream;

import java.util.List;

@Repository
public class StreamDAOImpl extends AbstractDAOImpl<Stream, Long> implements StreamDAO {

    public StreamDAOImpl() {
        super(Stream.class);
    }

    @Override
    public List<Stream> findByName(String namePrefix) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Stream s WHERE s.name LIKE :namePrefix", Stream.class)
                    .setParameter("namePrefix", namePrefix + "%")
                    .getResultList();
        }
    }
}
