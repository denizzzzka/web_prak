package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.Lesson;
import java.util.List;

public interface LessonDAO extends AbstractDAO<Lesson, Long> {
    List<Lesson> findByStudent(Long studentId);
    List<Lesson> findByTeacher(Long teacherId);
    List<Lesson> findByAuditorium(Long auditoriumId);
}

