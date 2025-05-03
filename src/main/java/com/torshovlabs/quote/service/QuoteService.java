package com.torshovlabs.quote.service;

import com.torshovlabs.quote.dao.GroupDAO;
import com.torshovlabs.quote.dao.GroupMembershipDAO;
import com.torshovlabs.quote.dao.QuoteDAO;
import com.torshovlabs.quote.dao.UserDAO;
import com.torshovlabs.quote.domain.Group;
import com.torshovlabs.quote.domain.GroupMembership;
import com.torshovlabs.quote.domain.Quote;
import com.torshovlabs.quote.domain.User;
import com.torshovlabs.quote.util.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteService {

    private final QuoteDAO quoteDAO;
    private final UserDAO userDAO;
    private final GroupDAO groupDAO;
    private final GroupMembershipDAO groupMembershipDAO;

    @Autowired
    public QuoteService(QuoteDAO quoteDAO, UserDAO userDAO,
                        GroupDAO groupDAO, GroupMembershipDAO groupMembershipDAO) {
        this.quoteDAO = quoteDAO;
        this.userDAO = userDAO;
        this.groupDAO = groupDAO;
        this.groupMembershipDAO = groupMembershipDAO;
    }

    /**
     * Create a new quote and rotate quote permission to the next user
     */
    @Transactional
    public Quote createQuote(String quoteText, String author, String username, Long groupId)
            throws UserNotFoundException, GroupNotFoundException,
            QuoteAlreadyExistsException, NotAllowedToPostException {

        // Find the user by username
        User user = userDAO.findByName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        // Find the group
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        // Find the membership
        GroupMembership membership = groupMembershipDAO.findByUserAndGroup(user.getId(), groupId)
                .orElseThrow(() -> new NotAllowedToPostException("User is not a member of this group"));

        // Check if user has permission to post
        if (membership.getCanQuote() == null || !membership.getCanQuote()) {
            throw new NotAllowedToPostException("You do not have permission to post quotes in this group");
        }

        // Check if user has already posted in the last 24 hours
        LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(24);
        if (membership.getLastQuoteTime() != null &&
                membership.getLastQuoteTime().isAfter(oneDayAgo)) {

            long hoursLeft = ChronoUnit.HOURS.between(
                    LocalDateTime.now(),
                    membership.getLastQuoteTime().plusHours(24)
            ) + 1;

            throw new NotAllowedToPostException("You must wait " + hoursLeft +
                    " more hours before posting another quote");
        }

        // Create the quote
        Quote quote = new Quote();
        quote.setQuote(quoteText);
        quote.setAuthor(author);
        quote.setUser(user);
        quote.setGroup(group);
        quote.setCreationDate(LocalDateTime.now());

        // Update last quote time
        membership.setLastQuoteTime(LocalDateTime.now());
        groupMembershipDAO.save(membership);

        // Rotate quote permission to next user
        rotateQuotePermission(groupId);

        return quoteDAO.save(quote);
    }

    /**
     * Rotate quote permission to the next user in the queue
     */
    @Transactional
    public void rotateQuotePermission(Long groupId) throws GroupNotFoundException {
        // Find the current publisher
        Optional<GroupMembership> currentPublisher = groupMembershipDAO.findByGroupAndCanQuote(groupId, true);

        if (currentPublisher.isPresent()) {
            // Remove permission from current publisher
            GroupMembership current = currentPublisher.get();
            current.setCanQuote(false);
            groupMembershipDAO.save(current);

            // Get all members in queue order
            List<GroupMembership> members = groupMembershipDAO.findByGroupOrderByQueueNumber(groupId);

            if (members.isEmpty()) {
                return;
            }

            // Find next member in queue
            int currentQueueNumber = current.getQueueNumber();
            Optional<GroupMembership> nextMember = members.stream()
                    .filter(m -> m.getQueueNumber() > currentQueueNumber)
                    .findFirst();

            // If no next member, loop back to the first one
            if (nextMember.isEmpty()) {
                nextMember = Optional.of(members.get(0));
            }

            // Give permission to next member
            GroupMembership next = nextMember.get();
            next.setCanQuote(true);
            groupMembershipDAO.save(next);
        } else {
            // No current publisher, assign to first member
            List<GroupMembership> members = groupMembershipDAO.findByGroupOrderByQueueNumber(groupId);
            if (!members.isEmpty()) {
                GroupMembership first = members.get(0);
                first.setCanQuote(true);
                groupMembershipDAO.save(first);
            }
        }
    }

    /**
     * Get all quotes for a group
     */
    @Transactional(readOnly = true)
    public List<Quote> getQuotesForGroup(String username, Long groupId)
            throws UserNotFoundException, GroupNotFoundException, NotAllowedToPostException {

        // Check if user exists
        User user = userDAO.findByName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        // Check if group exists
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        // Check if user is a member of the group
        if (!groupMembershipDAO.existsByUserAndGroup(user.getId(), groupId)) {
            throw new NotAllowedToPostException("User is not a member of this group");
        }

        // Return all quotes for the group
        return quoteDAO.findByGroup(groupId);
    }

    /**
     * Get the current user with permission to post quotes
     */
    @Transactional(readOnly = true)
    public User getCurrentPublisher(Long groupId) throws GroupNotFoundException {
        GroupMembership publisher = groupMembershipDAO.findByGroupAndCanQuote(groupId, true)
                .orElseThrow(() -> new GroupNotFoundException("No publisher found for this group"));

        return publisher.getUser();
    }

    /**
     * Check if a user can post quotes in a group
     */
    @Transactional(readOnly = true)
    public boolean canUserPostQuote(String username, Long groupId)
            throws UserNotFoundException, GroupNotFoundException {

        User user = userDAO.findByName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Optional<GroupMembership> membership = groupMembershipDAO.findByUserAndGroup(user.getId(), groupId);

        if (membership.isEmpty() || membership.get().getCanQuote() == null || !membership.get().getCanQuote()) {
            return false;
        }

        // Check if 24 hours have passed since last quote
        if (membership.get().getLastQuoteTime() != null) {
            LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(24);
            return membership.get().getLastQuoteTime().isBefore(oneDayAgo);
        }

        return true;
    }

    /**
     * Get time remaining until a user can post again
     */
    @Transactional(readOnly = true)
    public long getHoursUntilNextQuote(String username, Long groupId)
            throws UserNotFoundException, GroupNotFoundException, NotAllowedToPostException {

        User user = userDAO.findByName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        GroupMembership membership = groupMembershipDAO.findByUserAndGroup(user.getId(), groupId)
                .orElseThrow(() -> new NotAllowedToPostException("User is not a member of this group"));

        if (membership.getLastQuoteTime() == null) {
            return 0;
        }

        LocalDateTime nextAllowedTime = membership.getLastQuoteTime().plusHours(24);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(nextAllowedTime)) {
            return 0;
        }

        return ChronoUnit.HOURS.between(now, nextAllowedTime) + 1; // +1 to round up
    }
}