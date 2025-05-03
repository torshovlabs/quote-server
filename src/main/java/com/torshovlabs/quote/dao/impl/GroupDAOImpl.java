package com.torshovlabs.quote.dao.impl;

import com.torshovlabs.quote.dao.GroupDAO;
import com.torshovlabs.quote.domain.Group;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class GroupDAOImpl implements GroupDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Group save(Group group) {
        if (group.getId() == null) {
            entityManager.persist(group);
            return group;
        } else {
            return entityManager.merge(group);
        }
    }

    @Override
    public Optional<Group> findById(Long id) {
        Group group = entityManager.find(Group.class, id);
        return Optional.ofNullable(group);
    }

    @Override
    public Optional<Group> findByName(String name) {
        TypedQuery<Group> query = entityManager.createQuery(
                "SELECT g FROM Group g WHERE g.name = :name", Group.class);
        query.setParameter("name", name);

        List<Group> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Group> findAll() {
        TypedQuery<Group> query = entityManager.createQuery(
                "SELECT g FROM Group g", Group.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Group group = entityManager.find(Group.class, id);
        if (group != null) {
            entityManager.remove(group);
        }
    }

    @Override
    public List<Group> findByCreatedById(String userId) {
        TypedQuery<Group> query = entityManager.createQuery(
                "SELECT g FROM Group g WHERE g.createdBy.id = :userId", Group.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}