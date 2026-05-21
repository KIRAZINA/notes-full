package com.example.notes.user;

import com.example.notes.common.ConflictException;
import com.example.notes.common.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    private final UserService userService = new UserService(userRepository, passwordEncoder);

    @Test
    void register_shouldSaveUser() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("hashed");

        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .passwordHash("hashed")
                .roles(Set.of("ROLE_USER"))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        User saved = userService.register("john", "john@example.com", "secret");

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getPasswordHash()).isEqualTo("hashed");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowIfUsernameExists() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(User.builder().id(1L).username("john").build()));

        assertThatThrownBy(() -> userService.register("john", "john@example.com", "secret"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void register_shouldThrowIfEmailExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(User.builder().id(1L).email("john@example.com").build()));

        assertThatThrownBy(() -> userService.register("john", "john@example.com", "secret"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void findByUsernameOrThrow_shouldReturnUser() {
        User user = User.builder().id(1L).username("john").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User found = userService.findByUsernameOrThrow("john");

        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    void findByUsernameOrThrow_shouldThrowIfNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsernameOrThrow("john"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByIdOrThrow_shouldReturnUser() {
        User user = User.builder().id(1L).username("john").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getByIdOrThrow(1L);

        assertThat(found.getUsername()).isEqualTo("john");
    }

    @Test
    void getByIdOrThrow_shouldThrowIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByIdOrThrow(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void changePassword_shouldUpdatePasswordHash() {
        User user = User.builder().id(1L).passwordHash("hashed").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-pass", "hashed")).thenReturn(true);
        when(passwordEncoder.encode("new-pass")).thenReturn("new-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.changePassword(1L, "old-pass", "new-pass");

        assertThat(updated.getPasswordHash()).isEqualTo("new-hash");
        verify(userRepository).save(updated);
    }

    @Test
    void changePassword_shouldThrowWhenCurrentPasswordInvalid() {
        User user = User.builder().id(1L).passwordHash("hashed").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-pass", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(1L, "old-pass", "new-pass"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changeEmail_shouldUpdateEmail() {
        User user = User.builder().id(1L).email("old@example.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.changeEmail(1L, "new@example.com");

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).save(updated);
    }

    @Test
    void changeEmail_shouldThrowWhenEmailTakenByOtherUser() {
        when(userRepository.findByEmail("new@example.com"))
                .thenReturn(Optional.of(User.builder().id(2L).email("new@example.com").build()));

        assertThatThrownBy(() -> userService.changeEmail(1L, "new@example.com"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}
