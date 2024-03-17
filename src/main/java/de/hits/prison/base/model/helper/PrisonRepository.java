package de.hits.prison.base.model.helper;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

@Component
public class PrisonRepository<T, ID extends Serializable> {

    @Autowired
    private static Logger logger;

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;

    public static final BigInteger maxBigIntegerValue = new BigInteger("99999999999999999999999999999999999999999999999999999999999999999");

    public PrisonRepository(Class<T> entityClass) {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        return finder().findAll();
    }

    public T save(T entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(entity);
        tx.commit();
        session.close();
        return entity;
    }

    public void update(T entity, ID id) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.load(entity, id);
        tx.commit();
        session.close();
    }

    public void delete(T entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        if (entity != null) {
            session.delete(entity);
        }
        tx.commit();
        session.close();
    }

    public T findById(ID id) {
        return finder().equal("id", id).findFirst();
    }

    public CriteriaQueryBuilder<T> finder() {
        return new CriteriaQueryBuilder<>(sessionFactory, entityClass);
    }
}
