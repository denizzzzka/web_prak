package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.TeacherCourse;
import ru.msu.cmc.university_schedule.entities.TeacherCourseId;

import java.util.List;

public interface TeacherCourseDAO extends AbstractDAO<TeacherCourse, TeacherCourseId> {
    List<TeacherCourse> findByTeacher(Long teacherId);
    List<TeacherCourse> findByCourse(Long courseId);
    List<TeacherCourse> findByYear(int year);
}
