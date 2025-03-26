package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.Stream;

import java.util.List;

public interface StreamDAO extends AbstractDAO<Stream, Long> {
    List<Stream> findByName(String namePrefix);
}
