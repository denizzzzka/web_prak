package ru.msu.cmc.university_schedule.DAO;

import java.util.List;

public interface AbstractDAO<T, ID> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    T getById(ID id);
    List<T> getAll();
}