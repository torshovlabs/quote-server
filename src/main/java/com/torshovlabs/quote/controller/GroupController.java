package com.torshovlabs.quote.controller;

import com.torshovlabs.quote.controller.dto.RegisterGroupDTO;
import com.torshovlabs.quote.domain.Group;
import com.torshovlabs.quote.domain.User;
import com.torshovlabs.quote.service.GroupService;
import com.torshovlabs.quote.transport.TUser;
import com.torshovlabs.quote.util.exceptions.GroupAlreadyExistsException;
import com.torshovlabs.quote.util.exceptions.GroupNotFoundException;
import com.torshovlabs.quote.util.exceptions.NotAllowedToPostException;
import com.torshovlabs.quote.util.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping("/api/group")
@RestController
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Create a new group
     * The user creating the group automatically becomes the first member
     */
    @RequestMapping(value = {"/register", "/register.json"}, method = POST)
    public ResponseEntity<?> registerGroup(@Valid @RequestBody RegisterGroupDTO registerGroupDTO) {
        try {
            Group createdGroup = groupService.createGroup(
                    registerGroupDTO.getGroupName(),
                    registerGroupDTO.getUser().getUsername()
            );

            return ResponseEntity.ok("Group created successfully with name: " + createdGroup.getName() + " (ID: " + createdGroup.getId() + ")");
        } catch (GroupAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NotAllowedToPostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Join an existing group
     */
    @RequestMapping(value = "/join/{groupId}", method = POST)
    public ResponseEntity<?> joinGroup(@PathVariable Long groupId, @RequestBody TUser user) {
        try {
            groupService.joinGroup(user.getUsername(), groupId);
            return ResponseEntity.ok("Successfully joined the group");
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NotAllowedToPostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Get all members of a group
     */
    @RequestMapping(value = "/{groupId}/members", method = GET)
    public ResponseEntity<?> getGroupMembers(@PathVariable Long groupId) {
        try {
            List<User> members = groupService.getGroupMembers(groupId);

            // Transform to a simplified format for the response
            List<Map<String, Object>> result = members.stream().map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("name", m.getName());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get the current publisher for a group
     */
    @RequestMapping(value = "/{groupId}/publisher", method = GET)
    public ResponseEntity<?> getCurrentPublisher(@PathVariable Long groupId) {
        try {
            User publisher = groupService.getCurrentPublisher(groupId);

            Map<String, Object> result = new HashMap<>();
            result.put("id", publisher.getId());
            result.put("name", publisher.getName());

            return ResponseEntity.ok(result);
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get quote status for a group
     */
    @RequestMapping(value = "/{groupId}/quote-status", method = GET)
    public ResponseEntity<?> getQuoteStatus(@PathVariable Long groupId, @RequestParam String username) {
        try {
            // Get the current publisher
            User publisher = groupService.getCurrentPublisher(groupId);
            boolean isCurrentUser = publisher.getName().equals(username);

            Map<String, Object> result = new HashMap<>();
            result.put("publisherId", publisher.getId());
            result.put("publisherName", publisher.getName());
            result.put("isCurrentUserPublisher", isCurrentUser);

            return ResponseEntity.ok(result);
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}