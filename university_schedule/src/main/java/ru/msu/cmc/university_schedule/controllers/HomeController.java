package ru.msu.cmc.university_schedule.controllers;

import org.springframework.format.annotation.DateTimeFormat;
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
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
