package ru.msu.cmc.university_schedule.DAO.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.AbstractDAO;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@Repository
public abstract class AbstractDAOImpl<T, ID> implements AbstractDAO<T, ID> {

    protected SessionFactory sessionFactory;
    protected Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    public AbstractDAOImpl() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(T entity) {
        getSession().persist(entity);
    }

    @Override
    public void update(T entity) {
        getSession().merge(entity);
    }

    @Override
    public void delete(T entity) {
        getSession().remove(entity);
    }

    @Override
    public T getById(ID id) {
        return getSession().find(persistentClass, id);
    }

    @Override
    public List<T> getAll() {
        return getSession().createQuery("FROM " + persistentClass.getSimpleName(), persistentClass).list();
    }
}
