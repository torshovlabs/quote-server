package com.torshovlabs.quote.controller;

import com.torshovlabs.quote.controller.dto.RegisterGroupDTO;
import com.torshovlabs.quote.domain.Group;
import com.torshovlabs.quote.service.GroupService;
import com.torshovlabs.quote.util.exceptions.GroupAlreadyExistsException;
import com.torshovlabs.quote.util.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * Multiple endpoints for backwards compatibility
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
        }
    }
}