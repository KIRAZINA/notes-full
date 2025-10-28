package com.example.notes.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
