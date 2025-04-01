package ru.msu.cmc.university_schedule.DAO;

import ru.msu.cmc.university_schedule.entities.CommonEntity;

import java.util.Collection;
import java.util.List;

public interface AbstractDAO<T extends CommonEntity<ID>, ID> {
    T getById(ID id);

    Collection<T> getAll();

    void save(T entity);

    void saveCollection(Collection<T> entities);

    void delete(T entity);

    void deleteById(ID id);

    void update(T entity);
}
