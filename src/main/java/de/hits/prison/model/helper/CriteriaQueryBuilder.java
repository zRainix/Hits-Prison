package de.hits.prison.model.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class CriteriaQueryBuilder<T> {

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;
    private final CriteriaBuilder criteriaBuilder;
    private final CriteriaQuery<T> criteriaQuery;
    private final Root<T> root;
    private final List<Predicate> predicates;

    public CriteriaQueryBuilder(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
        this.criteriaBuilder = sessionFactory.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(entityClass);
        this.root = criteriaQuery.from(entityClass);
        this.predicates = new ArrayList<>();
    }

    public CriteriaQueryBuilder<T> equal(String attributeName, Object value) {
        predicates.add(criteriaBuilder.equal(root.get(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> notEqual(String attributeName, Object value) {
        predicates.add(criteriaBuilder.notEqual(root.get(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> like(String attributeName, String pattern) {
        predicates.add(criteriaBuilder.like(root.get(attributeName), pattern));
        return this;
    }

    public CriteriaQueryBuilder<T> notLike(String attributeName, String pattern) {
        predicates.add(criteriaBuilder.notLike(root.get(attributeName), pattern));
        return this;
    }

    public CriteriaQueryBuilder<T> in(String attributeName, Object... values) {
        predicates.add(criteriaBuilder.in(root.get(attributeName)).value(values));
        return this;
    }

    public CriteriaQueryBuilder<T> notIn(String attributeName, Object... values) {
        predicates.add(criteriaBuilder.not(root.get(attributeName)).in(values));
        return this;
    }

    public CriteriaQueryBuilder<T> isNull(String attributeName) {
        predicates.add(criteriaBuilder.isNull(root.get(attributeName)));
        return this;
    }

    public CriteriaQueryBuilder<T> isNotNull(String attributeName) {
        predicates.add(criteriaBuilder.isNotNull(root.get(attributeName)));
        return this;
    }

    public CriteriaQueryBuilder<T> greaterThan(String attributeName, Comparable value) {
        predicates.add(criteriaBuilder.greaterThan(root.get(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> greaterThanOrEqualTo(String attributeName, Comparable value) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> lessThan(String attributeName, Comparable value) {
        predicates.add(criteriaBuilder.lessThan(root.get(attributeName), value));
        return this;
    }

    public CriteriaQueryBuilder<T> lessThanOrEqualTo(String attributeName, Comparable value) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(attributeName), value));
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
        predicates.add(criteriaBuilder.between(root.get(attributeName), lowerBound, upperBound));
        return this;
    }

    public CriteriaQueryBuilder<T> orderAsc(String attributeName) {
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(attributeName)));
        return this;
    }

    public CriteriaQueryBuilder<T> orderDesc(String attributeName) {
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(attributeName)));
        return this;
    }

    public long count() {
        long count = 0;
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(entityClass)));
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(countQuery);
            count = query.uniqueResult();
            session.close();
        }
        return count;
    }

    public T findFirst() {
        T first = null;
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(criteriaQuery);
            query.setMaxResults(1);
            first = query.uniqueResult();
            session.close();
        }
        return first;
    }

    public boolean exists() {
        boolean exists = false;
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(criteriaQuery);
            query.setMaxResults(1);
            exists = query.uniqueResult() != null;
            session.close();
        }
        return exists;
    }

    public List<T> findAll() {
        List<T> all;
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(criteriaQuery);
            all = query.getResultList();
            session.close();
        }
        return all;
    }
}
