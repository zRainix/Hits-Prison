package de.hits.prison.model.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;

public class PrisonRepository<T, ID extends Serializable> {

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;
    private Session session;

    public PrisonRepository(Class<T> entityClass) {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.entityClass = entityClass;
        this.session = this.sessionFactory.openSession();
    }

    public List<T> findAll() {
        updateSession();
        return finder().findAll();
    }

    public T save(T entity) {
        updateSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(entity);
        tx.commit();
        return entity;
    }

    public void delete(T entity) {
        updateSession();
        Transaction tx = session.beginTransaction();
        if (entity != null) {
            session.delete(entity);
        }
        tx.commit();
    }

    public T findById(ID id) {
        updateSession();
        return finder().equal("id", id).findFirst();
    }

    public CriteriaQueryBuilder<T> finder() {
        updateSession();
        return new CriteriaQueryBuilder<>(sessionFactory, entityClass, session);
    }

    public void updateSession() {
        this.session.close();
        this.session = this.sessionFactory.openSession();
    }
}
