package com.torshovlabs.quote.service;

import com.torshovlabs.quote.dao.GroupDAO;
import com.torshovlabs.quote.dao.GroupMembershipDAO;
import com.torshovlabs.quote.dao.UserDAO;
import com.torshovlabs.quote.domain.Group;
import com.torshovlabs.quote.domain.GroupMembership;
import com.torshovlabs.quote.domain.User;
import com.torshovlabs.quote.util.exceptions.GroupAlreadyExistsException;
import com.torshovlabs.quote.util.exceptions.GroupNotFoundException;
import com.torshovlabs.quote.util.exceptions.NotAllowedToPostException;
import com.torshovlabs.quote.util.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupService {

    private final GroupDAO groupDAO;
    private final UserDAO userDAO;
    private final GroupMembershipDAO groupMembershipDAO;

    @Autowired
    public GroupService(GroupDAO groupDAO, UserDAO userDAO, GroupMembershipDAO groupMembershipDAO) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
        this.groupMembershipDAO = groupMembershipDAO;
    }

    @Transactional
    public Group createGroup(String groupName, String username)
            throws GroupAlreadyExistsException, UserNotFoundException, NotAllowedToPostException {
        // Check if group already exists
        if (groupDAO.findByName(groupName).isPresent()) {
            throw new GroupAlreadyExistsException("Group with name " + groupName + " already exists");
        }

        // Find the user
        User user = userDAO.findByName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        // Create the group
        Group group = new Group();
        group.setName(groupName);
        group.setCreatedBy(user);
        Group savedGroup = groupDAO.save(group);

        // Create the first membership with quote permission
        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(savedGroup);
        membership.setQueueNumber(1);
        membership.setCanQuote(true); // The creator gets initial quote permission
        groupMembershipDAO.save(membership);

        return savedGroup;
    }

    @Transactional
    public void joinGroup(String username, Long groupId)
            throws UserNotFoundException, GroupNotFoundException, NotAllowedToPostException {
        User user = userDAO.findByName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        // Check if user is already a member
        if (groupMembershipDAO.existsByUserAndGroup(user.getId(), groupId)) {
            throw new NotAllowedToPostException("User is already a member of this group");
        }

        // Get the next queue number for this group
        int maxQueueNumber = groupMembershipDAO.getMaxQueueNumberForGroup(groupId);
        int nextQueueNumber = maxQueueNumber + 1;

        // Add the user to the group
        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setQueueNumber(nextQueueNumber);
        membership.setCanQuote(false); // New members don't get quote permission initially
        groupMembershipDAO.save(membership);
    }

    @Transactional(readOnly = true)
    public List<User> getGroupMembers(Long groupId) throws GroupNotFoundException {
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        return groupMembershipDAO.findByGroup(groupId).stream()
                .map(GroupMembership::getUser)
                .toList();
    }

    @Transactional(readOnly = true)
    public User getCurrentPublisher(Long groupId) throws GroupNotFoundException {
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        return groupMembershipDAO.findByGroupAndCanQuote(groupId, true)
                .orElseThrow(() -> new GroupNotFoundException("No publisher found for this group"))
                .getUser();
    }

    @Transactional
    public void rotateQuotePermissions() {
        List<Group> allGroups = groupDAO.findAll();

        for (Group group : allGroups) {
            // Get all memberships for this group ordered by queue number
            List<GroupMembership> memberships =
                    groupMembershipDAO.findByGroupIdOrderByQueueNumber(group.getId());

            if (memberships.isEmpty()) {
                continue; // Skip if no members
            }

            // Find the current permitted user (if any)
            GroupMembership currentPermittedMember = null;
            for (GroupMembership member : memberships) {
                if (member.getCanQuote() != null && member.getCanQuote()) {
                    currentPermittedMember = member;
                    break;
                }
            }

            // If no one currently has permission or the last member quoted, start from the beginning
            int nextIndex = 0;
            if (currentPermittedMember != null) {
                // Find the index of the current permitted member
                for (int i = 0; i < memberships.size(); i++) {
                    if (memberships.get(i).getId().equals(currentPermittedMember.getId())) {
                        // Move to the next person in queue
                        nextIndex = (i + 1) % memberships.size();
                        break;
                    }
                }

                // Remove permission from current user
                currentPermittedMember.setCanQuote(false);
                groupMembershipDAO.save(currentPermittedMember);
            }

            // Grant permission to the next user
            GroupMembership nextMember = memberships.get(nextIndex);
            nextMember.setCanQuote(true);
            groupMembershipDAO.save(nextMember);
        }
    }
}