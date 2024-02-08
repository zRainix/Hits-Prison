package de.hits.prison.model.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

public class Repository<T, ID extends Serializable> {

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;

    public Repository(Class<T> entityClass) {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            criteriaQuery.select(root);
            return session.createQuery(criteriaQuery).getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public T save(T entity) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(entity);
            tx.commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return entity;
    }

    public void delete(T entity) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            if (entity != null) {
                session.delete(entity);
            }
            tx.commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public T findById(ID id) {
        return finder().equal("id", id).findFirst();
    }

    public CriteriaQueryBuilder<T> finder() {
        return new CriteriaQueryBuilder<>(sessionFactory, entityClass);
    }
}
