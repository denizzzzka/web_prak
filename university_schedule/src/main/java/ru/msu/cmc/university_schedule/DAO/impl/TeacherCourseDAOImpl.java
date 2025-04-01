package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.TeacherCourseDAO;
import ru.msu.cmc.university_schedule.entities.TeacherCourse;
import ru.msu.cmc.university_schedule.entities.TeacherCourseId;

import java.util.List;

@Repository
public class TeacherCourseDAOImpl extends AbstractDAOImpl<TeacherCourse, TeacherCourseId> implements TeacherCourseDAO {

    public TeacherCourseDAOImpl() {
        super(TeacherCourse.class);
    }

    @Override
    public List<TeacherCourse> findByTeacher(Long teacherId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM TeacherCourse tc WHERE tc.teacher.id = :teacherId", TeacherCourse.class)
                    .setParameter("teacherId", teacherId)
                    .getResultList();
        }
    }

    @Override
    public List<TeacherCourse> findByCourse(Long courseId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM TeacherCourse tc WHERE tc.course.id = :courseId", TeacherCourse.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        }
    }

    @Override
    public List<TeacherCourse> findByYear(int year) {
        try (Session session = getSession()) {
            return session.createQuery("FROM TeacherCourse tc WHERE tc.year = :year", TeacherCourse.class)
                    .setParameter("year", year)
                    .getResultList();
        }
    }
}
