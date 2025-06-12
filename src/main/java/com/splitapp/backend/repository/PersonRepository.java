package com.splitapp.backend.repository;

import com.splitapp.backend.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    // Find a person by name
    Optional<Person> findByName(String name);

    // Check if a person with the given name exists
    boolean existsByName(String name);
}
