package com.torshovlabs.quote.service;

import com.torshovlabs.quote.domain.Group;
import com.torshovlabs.quote.domain.GroupMembership;
import com.torshovlabs.quote.domain.User;
import com.torshovlabs.quote.util.exceptions.GroupAlreadyExistsException;
import com.torshovlabs.quote.util.exceptions.GroupNotFoundException;
import com.torshovlabs.quote.util.exceptions.NotAllowedToPostException;
import com.torshovlabs.quote.util.exceptions.UserNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Group createGroup(String groupName, String creatorUsername) throws GroupAlreadyExistsException, UserNotFoundException, NotAllowedToPostException {
        // Check if group name already exists
        try {
            Group existingGroup = entityManager
                    .createQuery("SELECT g FROM Group g WHERE g.name = :name", Group.class)
                    .setParameter("name", groupName)
                    .getSingleResult();

            throw new GroupAlreadyExistsException("Group with name '" + groupName + "' already exists");
        } catch (NoResultException e) {
            // No group found with this name, so it's available
        }

        // Find the creator user
        User creator;
        try {
            creator = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                    .setParameter("name", creatorUsername)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new UserNotFoundException("User with username '" + creatorUsername + "' not found");
        }

        // Check if the user is already a member of another group
        Long groupCount = entityManager
                .createQuery("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.user.id = :userId", Long.class)
                .setParameter("userId", creator.getId())
                .getSingleResult();

        if (groupCount > 0) {
            throw new NotAllowedToPostException("User is already a member of a group. Users can only be in one group at a time.");
        }

        // Create and persist the new group
        Group newGroup = new Group();
        newGroup.setName(groupName);
        newGroup.setCreatedBy(creator);

        entityManager.persist(newGroup);

        // Add the creator as the first member with queue position 1
        addMemberToGroup(creator, newGroup, 1);

        return newGroup;
    }

    @Transactional
    public void addMemberToGroup(User user, Group group, int queuePosition) throws NotAllowedToPostException {
        // Check if user is already a member of any group
        Long groupCount = entityManager
                .createQuery("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.user.id = :userId", Long.class)
                .setParameter("userId", user.getId())
                .getSingleResult();

        if (groupCount > 0) {
            throw new NotAllowedToPostException("User is already a member of a group. Users can only be in one group at a time.");
        }

        // Create membership
        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setQueueNumber(queuePosition);

        entityManager.persist(membership);
    }

    @Transactional
    public void joinGroup(String username, Long groupId) throws UserNotFoundException, GroupNotFoundException, NotAllowedToPostException {
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

        // Check if user is already a member of any group
        Long groupCount = entityManager
                .createQuery("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.user.id = :userId", Long.class)
                .setParameter("userId", user.getId())
                .getSingleResult();

        if (groupCount > 0) {
            throw new NotAllowedToPostException("User is already a member of a group. Users can only be in one group at a time.");
        }

        // Determine queue position (next available)
        Integer maxPosition = entityManager
                .createQuery("SELECT MAX(gm.queueNumber) FROM GroupMembership gm WHERE gm.group.id = :groupId", Integer.class)
                .setParameter("groupId", groupId)
                .getSingleResult();

        int queuePosition = (maxPosition == null) ? 1 : maxPosition + 1;

        // Add the user to the group
        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setQueueNumber(queuePosition);

        entityManager.persist(membership);
    }

    public List<User> getGroupMembers(Long groupId) throws GroupNotFoundException {
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

        // Get all members
        return entityManager
                .createQuery(
                        "SELECT gm.user FROM GroupMembership gm WHERE gm.group.id = :groupId ORDER BY gm.queueNumber",
                        User.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    public boolean isUserInGroup(String userId, Long groupId) {
        Long count = entityManager
                .createQuery("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.group.id = :groupId", Long.class)
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .getSingleResult();

        return count > 0;
    }

    public User getCurrentPublisher(Long groupId) throws GroupNotFoundException {
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

        // For now, just return the creator
        // In the future, this would implement your queue-based logic
        return group.getCreatedBy();
    }
}