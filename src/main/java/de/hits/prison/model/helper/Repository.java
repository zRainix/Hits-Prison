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
    private Session session;

    public Repository(Class<T> entityClass) {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.entityClass = entityClass;
        this.session = this.sessionFactory.openSession();
    }

    public List<T> findAll() {
        checkSession();
        return finder().findAll();
    }

    public T save(T entity) {
        checkSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(entity);
        tx.commit();
        return entity;
    }

    public void delete(T entity) {
        checkSession();
        Transaction tx = session.beginTransaction();
        if (entity != null) {
            session.delete(entity);
        }
        tx.commit();
    }

    public T findById(ID id) {
        checkSession();
        return finder().equal("id", id).findFirst();
    }

    public CriteriaQueryBuilder<T> finder() {
        checkSession();
        return new CriteriaQueryBuilder<>(sessionFactory, entityClass, session);
    }

    public void checkSession() {
        if (this.session == null) {
            this.session = this.sessionFactory.openSession();
            return;
        }
        if(!this.session.isConnected()) {
            this.session.close();
            this.session = this.sessionFactory.openSession();
            return;
        }
    }
}
