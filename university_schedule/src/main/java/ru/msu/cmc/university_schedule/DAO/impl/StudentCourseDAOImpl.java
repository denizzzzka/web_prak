package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.StudentCourseDAO;
import ru.msu.cmc.university_schedule.entities.StudentCourse;
import ru.msu.cmc.university_schedule.entities.StudentCourseId;

import java.util.List;

@Repository
public class StudentCourseDAOImpl extends AbstractDAOImpl<StudentCourse, StudentCourseId> implements StudentCourseDAO {

    public StudentCourseDAOImpl() {
        super(StudentCourse.class);
    }

    @Override
    public List<StudentCourse> findByStudent(Long studentId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM StudentCourse sc WHERE sc.student.id = :studentId", StudentCourse.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }

    @Override
    public List<StudentCourse> findByCourse(Long courseId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM StudentCourse sc WHERE sc.course.id = :courseId", StudentCourse.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        }
    }

    @Override
    public List<StudentCourse> findByYear(int year) {
        try (Session session = getSession()) {
            return session.createQuery("FROM StudentCourse sc WHERE sc.year = :year", StudentCourse.class)
                    .setParameter("year", year)
                    .getResultList();
        }
    }
}
