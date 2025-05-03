package com.torshovlabs.quote.service;

import com.torshovlabs.quote.domain.Group;
import com.torshovlabs.quote.domain.Quote;
import com.torshovlabs.quote.domain.User;
import com.torshovlabs.quote.util.exceptions.GroupNotFoundException;
import com.torshovlabs.quote.util.exceptions.NotAllowedToPostException;
import com.torshovlabs.quote.util.exceptions.QuoteAlreadyExistsException;
import com.torshovlabs.quote.util.exceptions.UserNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuoteService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Quote createQuote(String quoteText, String author, String username, Long groupId)
            throws UserNotFoundException, GroupNotFoundException, QuoteAlreadyExistsException, NotAllowedToPostException {

        // Find the user
        User user;
        try {
            user = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                    .setParameter("name", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new UserNotFoundException("User with username '" + username + "' not found");
        }

        // Find the group
        Group group;
        try {
            group = entityManager.find(Group.class, groupId);
            if (group == null) {
                throw new GroupNotFoundException("Group with ID '" + groupId + "' not found");
            }
        } catch (Exception e) {
            throw new GroupNotFoundException("Group with ID '" + groupId + "' not found");
        }

        // Check if user is a member of the group
        boolean isMember = entityManager
                .createQuery("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.group.id = :groupId", Long.class)
                .setParameter("userId", user.getId())
                .setParameter("groupId", groupId)
                .getSingleResult() > 0;

        if (!isMember) {
            throw new NotAllowedToPostException("You must be a member of the group to post a quote");
        }

        // Check if a quote has already been posted today for this group
        LocalDate today = LocalDate.now();
        try {
            entityManager
                    .createQuery("SELECT q FROM Quote q WHERE q.group.id = :groupId AND function('date', q.creationDate) = :today", Quote.class)
                    .setParameter("groupId", groupId)
                    .setParameter("today", today)
                    .getSingleResult();

            // If we get here, a quote already exists
            throw new QuoteAlreadyExistsException("A quote has already been posted in this group today");
        } catch (NoResultException e) {
            // No quote found for today, which is good
        }

        // Check if this user is allowed to post based on the queue system
        // For now, we'll implement a simple rule: only the creator of the group can post
        // In the future, this would be replaced with the queue-based logic
        if (!user.getId().equals(group.getCreatedBy().getId())) {
            throw new NotAllowedToPostException("You are not the current designated publisher for this group");
        }

        // Create and save the quote
        Quote quote = new Quote();
        quote.setQuote(quoteText);
        quote.setAuthor(author);
        quote.setUser(user);
        quote.setGroup(group);
        quote.setCreationDate(LocalDateTime.now());

        entityManager.persist(quote);
        return quote;
    }

    public List<Quote> getQuotesForGroup(String username, Long groupId)
            throws UserNotFoundException, GroupNotFoundException, NotAllowedToPostException {

        // Find the user
        User user;
        try {
            user = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                    .setParameter("name", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new UserNotFoundException("User with username '" + username + "' not found");
        }

        // Find the group
        Group group;
        try {
            group = entityManager.find(Group.class, groupId);
            if (group == null) {
                throw new GroupNotFoundException("Group with ID '" + groupId + "' not found");
            }
        } catch (Exception e) {
            throw new GroupNotFoundException("Group with ID '" + groupId + "' not found");
        }

        // Check if user is a member of the group
        boolean isMember = entityManager
                .createQuery("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.group.id = :groupId", Long.class)
                .setParameter("userId", user.getId())
                .setParameter("groupId", groupId)
                .getSingleResult() > 0;

        if (!isMember) {
            throw new NotAllowedToPostException("You must be a member of the group to view quotes");
        }

        // Get quotes for the group
        return entityManager
                .createQuery("SELECT q FROM Quote q WHERE q.group.id = :groupId ORDER BY q.creationDate DESC", Quote.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }
}