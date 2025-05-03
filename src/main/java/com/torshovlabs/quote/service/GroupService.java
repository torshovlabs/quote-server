package com.torshovlabs.quote.service;

import com.torshovlabs.quote.dao.GroupDAO;
import com.torshovlabs.quote.dao.GroupMembershipDAO;
import com.torshovlabs.quote.dao.UserDAO;
import com.torshovlabs.quote.domain.Group;
import com.torshovlabs.quote.domain.GroupMembership;
import com.torshovlabs.quote.domain.User;
import com.torshovlabs.quote.util.exceptions.GroupAlreadyExistsException;
import com.torshovlabs.quote.util.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;


@Service
public class GroupService {

    private final GroupDAO groupDAO;
    private final GroupMembershipDAO groupMembershipDAO;
    private final UserDAO userDAO;

    @Autowired
    public GroupService(GroupDAO groupDAO, GroupMembershipDAO groupMembershipDAO, UserDAO userDAO) {
        this.groupDAO = groupDAO;
        this.groupMembershipDAO = groupMembershipDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    public Group createGroup(String groupName, String username) {
        // Check if group name already exists
        if (groupDAO.existsByName(groupName)) {
            throw new GroupAlreadyExistsException("Group name '" + groupName + "' is already taken");
        }

        // Find the user who is creating the group
        User creator = userDAO.findByName(username)
                .orElseThrow(() -> new UserNotFoundException("User '" + username + "' not found"));

        // Create the group
        Group group = new Group();
        group.setName(groupName);
        group.setCreatedBy(creator);
        group.setGroupMemberships(new HashSet<>());
        group.setQuotes(new HashSet<>());

        // Save the group first to get the generated ID
        Group savedGroup = groupDAO.save(group);

        // Create group membership for the creator
        GroupMembership membership = new GroupMembership();
        membership.setUser(creator);
        membership.setGroup(savedGroup);
        membership.setQueueNumber(1); // First member gets queue number 1

        // Save the membership
        groupMembershipDAO.save(membership);

        return savedGroup;
    }
}