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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseDAO courseDao;
    private final StreamDAO streamDao;
    private final GroupDAO groupDao;
    private final LessonDAO lessonDao;
    private final TeacherDAO teacherDao;
    private final AuditoriumDAO auditoriumDao;

    public CourseController(CourseDAO courseDao,
                            StreamDAO streamDao,
                            GroupDAO groupDao,
                            LessonDAO lessonDao,
                            TeacherDAO teacherDao,
                            AuditoriumDAO auditoriumDao) {
        this.courseDao = courseDao;
        this.streamDao = streamDao;
        this.groupDao = groupDao;
        this.lessonDao = lessonDao;
        this.teacherDao = teacherDao;
        this.auditoriumDao = auditoriumDao;
    }

    @GetMapping
    public String list(
            @RequestParam Optional<Long> streamId,
            @RequestParam Optional<Long> groupId,
            @RequestParam Optional<Boolean> special,
            Model model
    ) {
        model.addAttribute("streams", streamDao.getAll());
        model.addAttribute("groups", groupDao.getAll());

        List<Course> filtered = courseDao.getAll().stream()
                .filter(c -> streamId.map(id ->
                                c.getStream() != null && c.getStream().getId().equals(id))
                        .orElse(true))
                .filter(c -> groupId.map(id ->
                                c.getGroup() != null && c.getGroup().getId().equals(id))
                        .orElse(true))
                .filter(c -> special.map(sp -> c.isSpecialCourse() == sp).orElse(true))
                .collect(Collectors.toList());

        model.addAttribute("courses", filtered);
        model.addAttribute("streamId", streamId.orElse(null));
        model.addAttribute("groupId", groupId.orElse(null));
        model.addAttribute("special", special.orElse(null));

        return "courses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("streams", streamDao.getAll());
        model.addAttribute("groups", groupDao.getAll());
        return "courses/form";
    }

    @PostMapping
    public String create(@ModelAttribute Course course) {
        courseDao.save(course);
        return "redirect:/courses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseDao.getById(id));
        model.addAttribute("streams", streamDao.getAll());
        model.addAttribute("groups", groupDao.getAll());
        return "courses/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Course course) {
        course.setId(id);
        courseDao.update(course);
        return "redirect:/courses/{id}";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseDao.getById(id));
        return "courses/view";
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseDao.deleteById(id);
        return "redirect:/courses";
    }

    @GetMapping("/{id}/schedule")
    public String schedule(@PathVariable Long id, Model model) {
        Course course = courseDao.getById(id);
        List<Lesson> lessons = lessonDao.getAll().stream()
                .filter(l -> l.getCourse().getId().equals(id))
                .collect(Collectors.toList());

        model.addAttribute("course",      course);
        model.addAttribute("lessons",     lessons);
        model.addAttribute("teachers",    teacherDao.getAll());
        model.addAttribute("auditoriums", auditoriumDao.getAll());
        return "courses/schedule";
    }

    @PostMapping("/{id}/schedule")
    public String addLesson(@PathVariable Long id,
                            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                            @RequestParam("endDate")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                            @RequestParam("endTime")   @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                            @RequestParam("teacherId") Long teacherId,
                            @RequestParam("auditoriumId") Long auditoriumId
    ) {
        Lesson lesson = new Lesson();
        lesson.setCourse(courseDao.getById(id));
        lesson.setTeacher(teacherDao.getById(teacherId));
        lesson.setAuditorium(auditoriumDao.getById(auditoriumId));
        lesson.setStartTime(LocalDateTime.of(startDate, startTime));
        lesson.setEndTime(LocalDateTime.of(endDate,   endTime));
        lessonDao.save(lesson);
        return "redirect:/courses/" + id + "/schedule";
    }

    @PostMapping("/{courseId}/schedule/{lessonId}/delete")
    public String deleteLesson(@PathVariable Long courseId,
                               @PathVariable Long lessonId) {
        lessonDao.deleteById(lessonId);
        return "redirect:/courses/" + courseId + "/schedule";
    }
}
