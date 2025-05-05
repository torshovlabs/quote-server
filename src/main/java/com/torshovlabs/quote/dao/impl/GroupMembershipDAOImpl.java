package com.torshovlabs.quote.dao.impl;

import com.torshovlabs.quote.dao.GroupMembershipDAO;
import com.torshovlabs.quote.domain.GroupMembership;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class GroupMembershipDAOImpl implements GroupMembershipDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public GroupMembership save(GroupMembership groupMembership) {
        if (groupMembership.getId() == null) {
            entityManager.persist(groupMembership);
            return groupMembership;
        } else {
            return entityManager.merge(groupMembership);
        }
    }

    @Override
    public Optional<GroupMembership> findById(Long id) {
        GroupMembership groupMembership = entityManager.find(GroupMembership.class, id);
        return Optional.ofNullable(groupMembership);
    }

    @Override
    public Optional<GroupMembership> findByUserAndGroup(String userId, Long groupId) {
        TypedQuery<GroupMembership> query = entityManager.createQuery(
                "SELECT gm FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.group.id = :groupId",
                GroupMembership.class);
        query.setParameter("userId", userId);
        query.setParameter("groupId", groupId);

        List<GroupMembership> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<GroupMembership> findByUser(String userId) {
        TypedQuery<GroupMembership> query = entityManager.createQuery(
                "SELECT gm FROM GroupMembership gm WHERE gm.user.id = :userId", GroupMembership.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<GroupMembership> findByGroup(Long groupId) {
        TypedQuery<GroupMembership> query = entityManager.createQuery(
                "SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId", GroupMembership.class);
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }

    @Override
    public Optional<GroupMembership> findByGroupAndCanQuote(Long groupId, Boolean canQuote) {
        TypedQuery<GroupMembership> query = entityManager.createQuery(
                "SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.canQuote = :canQuote",
                GroupMembership.class);
        query.setParameter("groupId", groupId);
        query.setParameter("canQuote", canQuote);

        List<GroupMembership> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<GroupMembership> findByGroupOrderByQueueNumber(Long groupId) {
        TypedQuery<GroupMembership> query = entityManager.createQuery(
                "SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId ORDER BY gm.queueNumber ASC",
                GroupMembership.class);
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        GroupMembership groupMembership = entityManager.find(GroupMembership.class, id);
        if (groupMembership != null) {
            entityManager.remove(groupMembership);
        }
    }

    @Override
    public boolean existsByUserAndGroup(String userId, Long groupId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.group.id = :groupId",
                Long.class);
        query.setParameter("userId", userId);
        query.setParameter("groupId", groupId);
        return query.getSingleResult() > 0;
    }

    @Override
    public int getMaxQueueNumberForGroup(Long groupId) {
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT COALESCE(MAX(gm.queueNumber), 0) FROM GroupMembership gm WHERE gm.group.id = :groupId",
                Integer.class);
        query.setParameter("groupId", groupId);
        return query.getSingleResult();
    }

    @Override
    public void updateCanQuoteStatus(Long membershipId, Boolean canQuote) {
        GroupMembership membership = entityManager.find(GroupMembership.class, membershipId);
        if (membership != null) {
            membership.setCanQuote(canQuote);
            entityManager.merge(membership);
        }
    }

    @Override
    public void resetAllCanQuoteForGroup(Long groupId, Boolean canQuote) {
        entityManager.createQuery(
                        "UPDATE GroupMembership gm SET gm.canQuote = :canQuote WHERE gm.group.id = :groupId")
                .setParameter("canQuote", canQuote)
                .setParameter("groupId", groupId)
                .executeUpdate();
    }
    @Override
    public List<GroupMembership> findByGroupIdOrderByQueueNumber(Long groupId) {
        TypedQuery<GroupMembership> query = entityManager.createQuery(
                "SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId ORDER BY gm.queueNumber",
                GroupMembership.class);
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }
}