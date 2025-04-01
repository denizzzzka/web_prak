package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.CourseDAO;
import ru.msu.cmc.university_schedule.entities.Course;

import java.util.List;

@Repository
public class CourseDAOImpl extends AbstractDAOImpl<Course, Long> implements CourseDAO {

    public CourseDAOImpl() {
        super(Course.class);
    }

    @Override
    public List<Course> findByStream(Long streamId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Course c WHERE c.stream.id = :streamId", Course.class)
                    .setParameter("streamId", streamId)
                    .getResultList();
        }
    }

    @Override
    public List<Course> findByGroup(Long groupId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Course c WHERE c.group.id = :groupId", Course.class)
                    .setParameter("groupId", groupId)
                    .getResultList();
        }
    }

    @Override
    public List<Course> findBySpecialCourse(boolean special) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Course c WHERE c.specialCourse = :special", Course.class)
                    .setParameter("special", special)
                    .getResultList();
        }
    }
}
