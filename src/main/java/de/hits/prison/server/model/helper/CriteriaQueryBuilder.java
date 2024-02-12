package de.hits.prison.server.model.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CriteriaQueryBuilder<T> {

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;
    private final CriteriaBuilder criteriaBuilder;
    private final CriteriaQuery<T> criteriaQuery;
    private final Root<T> root;
    private final List<Predicate> predicates;
    private final List<Join<?, ?>> joins;
    private final Session session;

    public CriteriaQueryBuilder(SessionFactory sessionFactory, Class<T> entityClass, Session session) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
        this.criteriaBuilder = sessionFactory.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(entityClass);
        this.root = criteriaQuery.from(entityClass);
        this.predicates = new ArrayList<>();
        this.joins = new ArrayList<>();
        this.session = session;
    }

    public CriteriaQueryBuilder<T> join(Function<Root<T>, Join<T, ?>> joinFunction) {
        joins.add(joinFunction.apply(root));
        return this;
    }

    private Path<?> resolveAttribute(String attributeName) {
        if (attributeName.contains(".")) {
            String[] attributes = attributeName.split("\\.");
            Path<?> path = root;
            for (String attribute : attributes) {
                path = path.get(attribute);
            }
            return path;
        } else {
            return root.get(attributeName);
        }
    }

    public CriteriaQueryBuilder<T> equal(String attributeName, Object value) {
        predicates.add(criteriaBuilder.equal(resolveAttribute(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> notEqual(String attributeName, Object value) {
        predicates.add(criteriaBuilder.notEqual(resolveAttribute(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> like(String attributeName, String pattern) {
        predicates.add(criteriaBuilder.like((Path<String>) resolveAttribute(attributeName), pattern));
        return this;
    }

    public CriteriaQueryBuilder<T> notLike(String attributeName, String pattern) {
        predicates.add(criteriaBuilder.notLike((Path<String>) resolveAttribute(attributeName), pattern));
        return this;
    }

    public CriteriaQueryBuilder<T> in(String attributeName, Object... values) {
        predicates.add(resolveAttribute(attributeName).in(values));
        return this;
    }

    public CriteriaQueryBuilder<T> notIn(String attributeName, Object... values) {
        predicates.add(criteriaBuilder.not((Path<Boolean>) resolveAttribute(attributeName)).in(values));
        return this;
    }

    public CriteriaQueryBuilder<T> isNull(String attributeName) {
        predicates.add(criteriaBuilder.isNull(resolveAttribute(attributeName)));
        return this;
    }

    public CriteriaQueryBuilder<T> isNotNull(String attributeName) {
        predicates.add(criteriaBuilder.isNotNull(resolveAttribute(attributeName)));
        return this;
    }

    public CriteriaQueryBuilder<T> greaterThan(String attributeName, Comparable value) {
        predicates.add(criteriaBuilder.greaterThan((Path<Comparable>) resolveAttribute(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> greaterThanOrEqualTo(String attributeName, Comparable value) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo((Path<Comparable>) resolveAttribute(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> lessThan(String attributeName, Comparable value) {
        predicates.add(criteriaBuilder.lessThan((Path<Comparable>) resolveAttribute(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> lessThanOrEqualTo(String attributeName, Comparable value) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo((Path<Comparable>) resolveAttribute(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> and(Predicate... conditions) {
        predicates.add(criteriaBuilder.and(conditions));
        return this;
    }

    public CriteriaQueryBuilder<T> or(Predicate... conditions) {
        predicates.add(criteriaBuilder.or(conditions));
        return this;
    }

    public CriteriaQueryBuilder<T> between(String attributeName, Comparable lowerBound, Comparable upperBound) {
        predicates.add(criteriaBuilder.between((Path<Comparable>) resolveAttribute(attributeName), lowerBound, upperBound));
        return this;
    }

    public CriteriaQueryBuilder<T> orderAsc(String attributeName) {
        criteriaQuery.orderBy(criteriaBuilder.asc(resolveAttribute(attributeName)));
        return this;
    }

    public CriteriaQueryBuilder<T> orderDesc(String attributeName) {
        criteriaQuery.orderBy(criteriaBuilder.desc(resolveAttribute(attributeName)));
        return this;
    }

    public long count() {
        long count = 0;
        Transaction tx = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            tx = session.beginTransaction();
            criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            countQuery.select(criteriaBuilder.count(countQuery.from(entityClass)));
            Query<Long> query = session.createQuery(countQuery);
            count = query.uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return count;
    }

    public T findFirst() {
        T first = null;
        Transaction tx = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            tx = session.beginTransaction();
            criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
            Query<T> query = session.createQuery(criteriaQuery);
            query.setMaxResults(1);
            first = query.uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return first;
    }

    public boolean exists() {
        boolean exists = false;
        Transaction tx = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            tx = session.beginTransaction();
            criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
            Query<T> query = session.createQuery(criteriaQuery);
            query.setMaxResults(1);
            exists = query.uniqueResult() != null;
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return exists;
    }

    public List<T> findMax(int max) {
        List<T> all = new ArrayList<>();
        Transaction tx = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            tx = session.beginTransaction();
            criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
            Query<T> query = session.createQuery(criteriaQuery);
            query.setMaxResults(max);
            all = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return all;
    }

    public List<T> findAll() {
        List<T> all = new ArrayList<>();
        Transaction tx = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            tx = session.beginTransaction();
            criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
            Query<T> query = session.createQuery(criteriaQuery);
            all = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return all;
    }

}
