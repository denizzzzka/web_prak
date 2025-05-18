package ru.msu.cmc.university_schedule.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.university_schedule.DAO.*;
import ru.msu.cmc.university_schedule.entities.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherDAO        teacherDao;
    private final CourseDAO         courseDao;
    private final TeacherCourseDAO  teacherCourseDao;
    private final LessonDAO         lessonDao;

    public TeacherController(TeacherDAO t, CourseDAO c, TeacherCourseDAO tc, LessonDAO l) {
        this.teacherDao       = t;
        this.courseDao        = c;
        this.teacherCourseDao = tc;
        this.lessonDao        = l;
    }

    @GetMapping
    public String list(
            @RequestParam Optional<Long> courseId,
            @RequestParam Optional<String> search,
            Model model
    ) {
        model.addAttribute("courses", courseDao.getAll());
        List<Teacher> teachers = teacherDao.getAll().stream()
                .filter(t -> courseId.map(id ->
                        teacherCourseDao.findByTeacher(t.getId())
                                .stream().anyMatch(tc -> tc.getCourse().getId().equals(id))
                ).orElse(true))
                .filter(t -> search.map(q ->
                        t.getFullName().toLowerCase().contains(q.toLowerCase())
                ).orElse(true))
                .collect(Collectors.toList());
        model.addAttribute("teachers", teachers);
        model.addAttribute("courseId", courseId.orElse(null));
        model.addAttribute("search",   search.orElse(""));
        return "teachers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("courses", courseDao.getAll());
        return "teachers/form";
    }

    @PostMapping
    public String create(@ModelAttribute Teacher teacher,
                         @RequestParam(name="courses", required=false) List<Long> courseIds) {
        teacherDao.save(teacher);
        if (courseIds != null) {
            int year = LocalDateTime.now().getYear();
            courseIds.forEach(cid -> {
                TeacherCourse tc = new TeacherCourse(
                        new TeacherCourseId(teacher.getId(), cid, year),
                        teacher,
                        courseDao.getById(cid),
                        year
                );
                teacherCourseDao.save(tc);
            });
        }
        return "redirect:/teachers";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherDao.getById(id));
        return "teachers/view";
    }

    @GetMapping("/{id}/schedule")
    public String schedule(@PathVariable Long id, Model model) {
        model.addAttribute("lessons", lessonDao.findByTeacher(id));
        return "teachers/schedule";
    }
}
