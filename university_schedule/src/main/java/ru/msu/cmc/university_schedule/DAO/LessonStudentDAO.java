package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.LessonStudent;
import ru.msu.cmc.university_schedule.entities.LessonStudentId;

import java.util.List;

public interface LessonStudentDAO extends AbstractDAO<LessonStudent, LessonStudentId> {
    List<LessonStudent> findByStudent(Long studentId);
    List<LessonStudent> findByLesson(Long lessonId);
}
