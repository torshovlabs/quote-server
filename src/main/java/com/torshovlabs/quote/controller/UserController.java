package com.torshovlabs.quote.controller;

import com.torshovlabs.quote.controller.dto.RegisterUserDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/api/user")
@RestController
public class UserController {


    /**
     * Payload from user to register account
     * hence we need to keep this endpoint for backwards compatibility.
     */
    @RequestMapping(value = {"/register", "/register.json"}, method = GET)
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        String username = registerUserDTO.getUsername();

        return ResponseEntity.ok("User registered with username: " + username);
    }

}
