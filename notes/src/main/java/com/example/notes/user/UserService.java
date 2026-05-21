package com.example.notes.user;

import com.example.notes.common.NotFoundException;
import com.example.notes.common.ConflictException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * Handles user registration and lookups.
 */
@Service
@Transactional
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String email, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ConflictException("Username already taken");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already taken");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .roles(Set.of("ROLE_USER"))
                .createdAt(Instant.now())
                .build();

        return userRepository.save(user);
    }

    public User findByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User changePassword(Long id, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Both old and new passwords are required");
        }
        User user = getByIdOrThrow(id);
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid current password");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User changeEmail(Long id, String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Email already taken");
                });
        User user = getByIdOrThrow(id);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getByIdOrThrow(id);
        userRepository.delete(user);
    }

    @Override
    public AppUserDetails loadUserByUsername(String username) throws NotFoundException {
        return new AppUserDetails(findByUsernameOrThrow(username));
    }
}
