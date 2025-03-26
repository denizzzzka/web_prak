package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.TeacherDAO;
import ru.msu.cmc.university_schedule.entities.Teacher;

import java.util.List;

@Repository
public class TeacherDAOImpl extends AbstractDAOImpl<Teacher, Long> implements TeacherDAO {

    @Override
    public List<Teacher> findByCourse(Long courseId) {
        return getSession().createQuery("SELECT tc.teacher FROM TeacherCourse tc WHERE tc.course.id = :courseId", Teacher.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    @Override
    public List<Teacher> findByName(String namePrefix) {
        return getSession().createQuery("FROM Teacher t WHERE t.fullName LIKE :namePrefix", Teacher.class)
                .setParameter("namePrefix", namePrefix + "%")
                .getResultList();
    }
}
