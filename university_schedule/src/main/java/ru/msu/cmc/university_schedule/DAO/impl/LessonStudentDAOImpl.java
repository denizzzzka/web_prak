package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.LessonStudentDAO;
import ru.msu.cmc.university_schedule.entities.LessonStudent;
import ru.msu.cmc.university_schedule.entities.LessonStudentId;

import java.util.List;

@Repository
public class LessonStudentDAOImpl extends AbstractDAOImpl<LessonStudent, LessonStudentId> implements LessonStudentDAO {

    public LessonStudentDAOImpl() {
        super(LessonStudent.class);
    }

    @Override
    public List<LessonStudent> findByStudent(Long studentId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM LessonStudent ls WHERE ls.student.id = :studentId", LessonStudent.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }

    @Override
    public List<LessonStudent> findByLesson(Long lessonId) {
        try (Session session = getSession()) {
            return session.createQuery("FROM LessonStudent ls WHERE ls.lesson.id = :lessonId", LessonStudent.class)
                    .setParameter("lessonId", lessonId)
                    .getResultList();
        }
    }
}
