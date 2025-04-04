package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.StudentDAO;
import ru.msu.cmc.university_schedule.entities.Student;

import java.util.List;

@Repository
public class StudentDAOImpl extends AbstractDAOImpl<Student, Long> implements StudentDAO {

    public StudentDAOImpl() {
        super(Student.class);
    }

    @Override
    public List<Student> findByStream(Long streamId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Student s WHERE s.stream.id = :streamId", Student.class)
                    .setParameter("streamId", streamId)
                    .getResultList();
        }
    }

    @Override
    public List<Student> findByGroup(Long groupId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Student s WHERE s.group.id = :groupId", Student.class)
                    .setParameter("groupId", groupId)
                    .getResultList();
        }
    }

    @Override
    public List<Student> findByName(String namePrefix) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Student s WHERE s.fullName LIKE :namePrefix", Student.class)
                    .setParameter("namePrefix", namePrefix + "%")
                    .getResultList();
        }
    }
}
