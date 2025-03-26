package ru.msu.cmc.university_schedule.DAO;


import ru.msu.cmc.university_schedule.entities.Course;
import java.util.List;

public interface CourseDAO extends AbstractDAO<Course, Long> {
    List<Course> findByStream(Long streamId);
    List<Course> findByGroup(Long groupId);
    List<Course> findBySpecialCourse(boolean special);
}

