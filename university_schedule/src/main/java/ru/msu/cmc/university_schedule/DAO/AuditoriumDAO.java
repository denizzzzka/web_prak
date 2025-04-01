package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.Auditorium;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriumDAO extends AbstractDAO<Auditorium, Long> {
    List<Auditorium> getAvailableAuditoriums(LocalDateTime startTime, LocalDateTime endTime);
}
