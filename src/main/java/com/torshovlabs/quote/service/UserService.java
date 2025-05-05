package com.torshovlabs.quote.service;

import com.torshovlabs.quote.dao.UserDAO;
import com.torshovlabs.quote.domain.User;
import com.torshovlabs.quote.util.exceptions.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.torshovlabs.quote.util.exceptions.*;

import java.util.UUID;

@Service
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    public User registerUser(String username) {
        if (userDAO.existsByName(username)) { // Checking if user exists
            throw new UsernameAlreadyExistsException("Username '" + username + "' is already taken");
        }

        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        newUser.setName(username);

        return userDAO.save(newUser);
    }
}