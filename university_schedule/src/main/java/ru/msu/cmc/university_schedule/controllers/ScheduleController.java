package ru.msu.cmc.university_schedule.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.university_schedule.DAO.*;
import ru.msu.cmc.university_schedule.entities.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final LessonDAO          lessonDao;
    private final CourseDAO          courseDao;
    private final TeacherDAO         teacherDao;
    private final AuditoriumDAO      auditoriumDao;
    private final StudentDAO         studentDao;
    private final LessonStudentDAO   lessonStudentDao;

    public ScheduleController(LessonDAO l,
                              CourseDAO c,
                              TeacherDAO t,
                              AuditoriumDAO a,
                              StudentDAO s,
                              LessonStudentDAO ls) {
        this.lessonDao        = l;
        this.courseDao        = c;
        this.teacherDao       = t;
        this.auditoriumDao    = a;
        this.studentDao       = s;
        this.lessonStudentDao = ls;
    }

    @GetMapping("/student/{id}")
    public String forStudent(@PathVariable Long id, Model model) {
        Student student = studentDao.getById(id);
        List<Lesson> lessons = lessonStudentDao.findByStudent(id).stream()
                .map(LessonStudent::getLesson)
                .toList();
        model.addAttribute("student",     student);
        model.addAttribute("lessons",     lessons);
        model.addAttribute("courses",     courseDao.getAll());
        model.addAttribute("teachers",    teacherDao.getAll());
        model.addAttribute("auditoriums", auditoriumDao.getAll());
        return "schedule/student";
    }

    @PostMapping("/student/{id}/create")
    public String createForStudent(
            @PathVariable Long id,
            @RequestParam Long courseId,
            @RequestParam Long teacherId,
            @RequestParam Long auditoriumId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)  LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)  LocalTime endTime
    ) {
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end   = LocalDateTime.of(date, endTime);

        Lesson lesson = new Lesson();
        lesson.setCourse(    courseDao.getById(courseId));
        lesson.setTeacher(   teacherDao.getById(teacherId));
        lesson.setAuditorium(auditoriumDao.getById(auditoriumId));
        lesson.setStartTime(start);
        lesson.setEndTime(  end);
        lessonDao.save(lesson);

        Student student = studentDao.getById(id);
        LessonStudent ls = new LessonStudent();
        LessonStudentId lsId = new LessonStudentId(lesson.getId(), student.getId());
        ls.setId(lsId);
        ls.setLesson(lesson);
        ls.setStudent(student);
        lessonStudentDao.save(ls);

        return "redirect:/schedule/student/" + id;
    }

}
