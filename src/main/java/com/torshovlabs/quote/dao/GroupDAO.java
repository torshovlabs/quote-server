package com.torshovlabs.quote.dao;

import com.torshovlabs.quote.domain.Group;
import java.util.List;
import java.util.Optional;

public interface GroupDAO {

    Group save(Group group);

    Optional<Group> findById(Long id);

    Optional<Group> findByName(String name);

    List<Group> findAll();

    List<Group> findByCreatedBy(String userId);

    void deleteById(Long id);

    boolean existsByName(String name);
}