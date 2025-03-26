package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.Teacher;
import java.util.List;

public interface TeacherDAO extends AbstractDAO<Teacher, Long> {
    List<Teacher> findByCourse(Long courseId);
    List<Teacher> findByName(String namePrefix);
}
