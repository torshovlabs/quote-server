package com.torshovlabs.quote.dao.impl;

import com.torshovlabs.quote.dao.QuoteDAO;
import com.torshovlabs.quote.domain.Quote;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class QuoteDAOImpl implements QuoteDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Quote save(Quote quote) {
        if (quote.getId() == null) {
            entityManager.persist(quote);
            return quote;
        } else {
            return entityManager.merge(quote);
        }
    }

    @Override
    public Optional<Quote> findById(Long id) {
        Quote quote = entityManager.find(Quote.class, id);
        return Optional.ofNullable(quote);
    }

    @Override
    public List<Quote> findByUser(String userId) {
        TypedQuery<Quote> query = entityManager.createQuery(
                "SELECT q FROM Quote q WHERE q.user.id = :userId ORDER BY q.creationDate DESC",
                Quote.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Quote> findByGroup(Long groupId) {
        TypedQuery<Quote> query = entityManager.createQuery(
                "SELECT q FROM Quote q WHERE q.group.id = :groupId ORDER BY q.creationDate DESC",
                Quote.class);
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }

    @Override
    public List<Quote> findByUserAndGroup(String userId, Long groupId) {
        TypedQuery<Quote> query = entityManager.createQuery(
                "SELECT q FROM Quote q WHERE q.user.id = :userId AND q.group.id = :groupId ORDER BY q.creationDate DESC",
                Quote.class);
        query.setParameter("userId", userId);
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Quote quote = entityManager.find(Quote.class, id);
        if (quote != null) {
            entityManager.remove(quote);
        }
    }

    @Override
    public boolean existsByUserAndGroupAndCreationDateAfter(String userId, Long groupId, LocalDateTime date) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(q) FROM Quote q WHERE q.user.id = :userId AND q.group.id = :groupId AND q.creationDate > :date",
                Long.class);
        query.setParameter("userId", userId);
        query.setParameter("groupId", groupId);
        query.setParameter("date", date);
        return query.getSingleResult() > 0;
    }
}