package ru.msu.cmc.university_schedule.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.university_schedule.DAO.*;
import ru.msu.cmc.university_schedule.entities.*;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentDAO studentDao;
    private final StreamDAO streamDao;
    private final GroupDAO groupDao;
    private final LessonStudentDAO lessonStudentDao;
    private final CourseDAO courseDao;
    private final StudentCourseDAO studentCourseDao;

    public StudentController(StudentDAO s,
                             StreamDAO st,
                             GroupDAO g,
                             LessonStudentDAO ls,
                             CourseDAO c,
                             StudentCourseDAO sc) {
        this.studentDao        = s;
        this.streamDao         = st;
        this.groupDao          = g;
        this.lessonStudentDao  = ls;
        this.courseDao         = c;
        this.studentCourseDao  = sc;
    }

    @GetMapping
    public String list(
            @RequestParam Optional<Long> streamId,
            @RequestParam Optional<Long> groupId,
            @RequestParam Optional<String> search,
            Model model
    ) {
        model.addAttribute("streams", streamDao.getAll());
        model.addAttribute("groups",  groupDao.getAll());
        List<Student> students = studentDao.getAll().stream()
                .filter(s -> streamId.map(id -> s.getStream().getId().equals(id)).orElse(true))
                .filter(s -> groupId .map(id -> s.getGroup().getId().equals(id)).orElse(true))
                .filter(s -> search  .map(q  -> s.getFullName().toLowerCase().contains(q.toLowerCase())).orElse(true))
                .collect(Collectors.toList());
        model.addAttribute("students", students);
        model.addAttribute("streamId", streamId.orElse(null));
        model.addAttribute("groupId",  groupId.orElse(null));
        model.addAttribute("search",   search.orElse(""));
        return "students/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("streams", streamDao.getAll());
        model.addAttribute("groups",  groupDao.getAll());
        model.addAttribute("student", new Student());
        return "students/form";
    }

    @PostMapping
    public String create(@ModelAttribute Student student) {
        studentDao.save(student);
        return "redirect:/students";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentDao.getById(id));
        return "students/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("streams", streamDao.getAll());
        model.addAttribute("groups",  groupDao.getAll());
        model.addAttribute("student", studentDao.getById(id));
        return "students/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Student student) {
        student.setId(id);
        studentDao.update(student);
        return "redirect:/students/{id}";
    }

    @GetMapping("/{id}/schedule")
    public String schedule(@PathVariable Long id, Model model) {
        Student student = studentDao.getById(id);
        model.addAttribute("student", student);

        List<Lesson> lessons = lessonStudentDao.findByStudent(id).stream()
                .map(LessonStudent::getLesson).collect(Collectors.toList());
        model.addAttribute("lessons", lessons);
        return "students/schedule";
    }

    @GetMapping("/{id}/enroll")
    public String enrollForm(@PathVariable Long id, Model model) {
        Student student = studentDao.getById(id);
        if (student == null) {
            return "redirect:/students";
        }

        List<Course> eligible = courseDao.getAll().stream()
                .filter(c ->
                        (c.getStream() != null && c.getStream().getId().equals(student.getStream().getId()))
                                || (c.getGroup()  != null && c.getGroup().getId().equals(student.getGroup().getId()))
                                || c.isSpecialCourse()
                )
                .collect(Collectors.toList());

        List<Long> already = studentCourseDao.findByStudent(id)
                .stream()
                .map(sc -> sc.getCourse().getId())
                .collect(Collectors.toList());
        eligible.removeIf(c -> already.contains(c.getId()));

        model.addAttribute("student", student);
        model.addAttribute("courses", eligible);
        return "students/enroll";
    }

    @PostMapping("/{id}/enroll")
    public String enroll(@PathVariable Long id,
                         @RequestParam Long courseId) {
        Student student = studentDao.getById(id);
        Course  course  = courseDao.getById(courseId);
        if (student != null && course != null) {
            int year = java.time.Year.now().getValue();
            StudentCourse sc = new StudentCourse(
                    new StudentCourseId(id, courseId, year),
                    student,
                    course,
                    year
            );
            studentCourseDao.save(sc);
        }
        return "redirect:/students/" + id;
    }

}
