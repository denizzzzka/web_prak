package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.StudentCourse;
import ru.msu.cmc.university_schedule.entities.StudentCourseId;

import java.util.List;

public interface StudentCourseDAO extends AbstractDAO<StudentCourse, StudentCourseId> {
    List<StudentCourse> findByStudent(Long studentId);
    List<StudentCourse> findByCourse(Long courseId);
    List<StudentCourse> findByYear(int year);
}
