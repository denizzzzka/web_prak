package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.Group;
import ru.msu.cmc.university_schedule.entities.Stream;

import java.util.List;

public interface GroupDAO extends AbstractDAO<Group, Long> {
    List<Group> findByName(String namePrefix);
    List<Group> findByStream(Stream stream);
}
