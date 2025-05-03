package com.torshovlabs.quote.dao;

import com.torshovlabs.quote.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {

    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByName(String name);

    List<User> findAll();

    void deleteById(String id);

    boolean existsByName(String name);

}