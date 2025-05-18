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
import java.util.Optional;


@Controller
@RequestMapping("/auditoriums")
public class AuditoriumController {

    private final AuditoriumDAO  audDao;
    private final LessonDAO      lessonDao;

    public AuditoriumController(AuditoriumDAO a, LessonDAO l) {
        this.audDao    = a;
        this.lessonDao = l;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)   LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)   LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)   LocalDate endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)   LocalTime endTime,
            Model model
    ) {
        LocalDateTime start = (startDate != null && startTime != null)
                ? LocalDateTime.of(startDate, startTime)
                : null;
        LocalDateTime end = (endDate != null && endTime != null)
                ? LocalDateTime.of(endDate, endTime)
                : null;

        model.addAttribute("auditoriums",
                (start != null && end != null)
                        ? audDao.getAvailableAuditoriums(start, end)
                        : audDao.getAll()
        );
        model.addAttribute("start", start);
        model.addAttribute("end",   end);
        return "auditoriums/list";
    }



    @GetMapping("/{id}/schedule")
    public String schedule(@PathVariable Long id, Model model) {
        model.addAttribute("lessons",
                lessonDao.findByAuditorium(id)
        );
        return "auditoriums/schedule";
    }
}
