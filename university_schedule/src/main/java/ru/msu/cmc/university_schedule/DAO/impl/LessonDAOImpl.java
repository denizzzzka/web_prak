package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.LessonDAO;
import ru.msu.cmc.university_schedule.entities.Lesson;

import java.util.List;

@Repository
public class LessonDAOImpl extends AbstractDAOImpl<Lesson, Long> implements LessonDAO {

    public LessonDAOImpl() {
        super(Lesson.class);
    }

    @Override
    public List<Lesson> findByStudent(Long studentId) {
        try (Session session = getSession()) {
            return session.createQuery(
                            "SELECT ls.lesson FROM LessonStudent ls WHERE ls.student.id = :studentId", Lesson.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }

    @Override
    public List<Lesson> findByTeacher(Long teacherId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Lesson l WHERE l.teacher.id = :teacherId", Lesson.class)
                    .setParameter("teacherId", teacherId)
                    .getResultList();
        }
    }

    @Override
    public List<Lesson> findByAuditorium(Long auditoriumId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM Lesson l WHERE l.auditorium.id = :auditoriumId", Lesson.class)
                    .setParameter("auditoriumId", auditoriumId)
                    .getResultList();
        }
    }
}
