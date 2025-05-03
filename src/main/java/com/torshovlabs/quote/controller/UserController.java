package com.torshovlabs.quote.controller;

import com.torshovlabs.quote.controller.dto.RegisterUserDTO;
import com.torshovlabs.quote.domain.User;
import com.torshovlabs.quote.service.UserService;
import com.torshovlabs.quote.util.exceptions.UsernameAlreadyExistsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Payload from user to register account
     */
    @RequestMapping(value = {"/register", "/register.json"}, method = POST)
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        try {
            User registeredUser = userService.registerUser(registerUserDTO.getUsername());
            return ResponseEntity.ok("User registered successfully with username: " + registeredUser.getName());
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}