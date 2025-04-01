package ru.msu.cmc.university_schedule.DAO.impl;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.university_schedule.DAO.AbstractDAO;
import ru.msu.cmc.university_schedule.entities.CommonEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
@Repository
public abstract class AbstractDAOImpl<T extends CommonEntity<ID>, ID extends Serializable> implements AbstractDAO<T, ID> {

    protected SessionFactory sessionFactory;
    protected Class<T> persistentClass;

    public AbstractDAOImpl(Class<T> entityClass) {
        this.persistentClass = entityClass;
    }

    @Autowired
    public void setSessionFactory(LocalSessionFactoryBean sessionFactoryBean) {
        this.sessionFactory = sessionFactoryBean.getObject();
    }

    protected Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public T getById(ID id) {
        try (Session session = getSession()) {
            return session.get(persistentClass, id);
        }
    }

    @Override
    public List<T> getAll() {
        try (Session session = getSession()) {
            CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(persistentClass);
            criteriaQuery.from(persistentClass);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public void save(T entity) {
        try (Session session = getSession()) {
            session.beginTransaction();
            if (entity.getId() == null) {
                session.persist(entity);  // Используем persist для новых сущностей
            } else {
                session.merge(entity);  // Используем merge для существующих сущностей
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public void saveCollection(Collection<T> entities) {
        try (Session session = getSession()) {
            session.beginTransaction();
            for (T entity : entities) {
                if (entity.getId() == null) {
                    session.persist(entity);  // Используем persist для новых сущностей
                } else {
                    session.merge(entity);  // Используем merge для существующих сущностей
                }
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(T entity) {
        try (Session session = getSession()) {
            session.beginTransaction();
            session.merge(entity);  // Используем merge для обновления сущности
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(T entity) {
        try (Session session = getSession()) {
            session.beginTransaction();
            session.remove(entity);  // Используем remove для удаления сущности
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteById(ID id) {
        try (Session session = getSession()) {
            session.beginTransaction();
            T entity = getById(id);
            if (entity != null) {
                session.remove(entity);  // Используем remove для удаления сущности
            }
            session.getTransaction().commit();
        }
    }

}