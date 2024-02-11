package de.hits.prison.model.helper;

import org.bukkit.Bukkit;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

public class PrisonRepository<T, ID extends Serializable> {

    private Logger logger = Bukkit.getLogger();

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;
    private Session session;

    public static BigInteger maxBigIntegerValue = new BigInteger("99999999999999999999999999999999999999999999999999999999999999999");

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
        try {
            this.session.close();
        } catch (Exception e) {
            logger.warning("Error while trying to close session: " + e.getMessage());
        }
        this.session = this.sessionFactory.openSession();
    }
}
