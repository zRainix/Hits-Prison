package de.hits.prison.model.helper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
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

    // Methoden zur Erstellung von Predicates

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

    // Zusätzliche Methoden

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
        CriteriaQuery<Long> count = criteriaBuilder.createQuery(Long.class);
        count.select(criteriaBuilder.count(count.from(entityClass)));
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(count);
            return query.uniqueResult();
        }
    }

    public T findFirst() {
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(criteriaQuery);
            query.setMaxResults(1);
            return query.uniqueResult();
        }
    }

    public boolean exists() {
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(criteriaQuery);
            query.setMaxResults(1);
            return query.uniqueResult() != null;
        }
    }

    public List<T> findAll() {
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(criteriaQuery);
            return query.getResultList();
        }
    }
}
