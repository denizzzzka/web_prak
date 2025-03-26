package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.Student;
import java.util.List;

public interface StudentDAO extends AbstractDAO<Student, Long> {
    List<Student> findByStream(Long streamId);
    List<Student> findByGroup(Long groupId);
    List<Student> findByName(String namePrefix);
}
