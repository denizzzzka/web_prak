package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.GroupDAO;
import ru.msu.cmc.university_schedule.entities.Group;
import ru.msu.cmc.university_schedule.entities.Stream;

import java.util.List;

@Repository
public class GroupDAOImpl extends AbstractDAOImpl<Group, Long> implements GroupDAO {

    @Override
    public List<Group> findByName(String namePrefix) {
        Session session = getSession();
        return session.createQuery("FROM Group g WHERE g.name LIKE :namePrefix", Group.class)
                .setParameter("namePrefix", namePrefix + "%")
                .getResultList();
    }

    @Override
    public List<Group> findByStream(Stream stream) {
        Session session = getSession();
        return session.createQuery("FROM Group g WHERE g.stream = :stream", Group.class)
                .setParameter("stream", stream)
                .getResultList();
    }
}
