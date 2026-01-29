package com.example.notes.web;

import com.example.notes.security.JwtService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.user.User;
import com.example.notes.user.UserService;
import com.example.notes.web.dto.*;
import com.example.notes.web.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 * Integration tests for AuthController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserMapper userMapper;

    @Test
    void register_shouldReturnJwtToken() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "secret");

        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .passwordHash("hashed")
                .roles(Set.of("ROLE_USER"))
                .build();

        when(userService.register("john", "john@example.com", "secret")).thenReturn(user);
        when(jwtService.generateToken("john")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("fake-jwt-token"));
    }

    @Test
    void login_shouldReturnJwtToken() throws Exception {
        LoginRequest request = new LoginRequest("john", "secret");

        AppUserDetails principal = new AppUserDetails(
                User.builder().id(1L).username("john").passwordHash("hashed").build()
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken("john")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("fake-jwt-token"));
    }

    @Test
    void me_shouldReturnUserResponse() throws Exception {
        User user = User.builder().id(1L).username("john").email("john@example.com").build();
        AppUserDetails principal = new AppUserDetails(user);
        UserResponse response = new UserResponse(1L, "john", "john@example.com");

        when(userService.getByIdOrThrow(1L)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(get("/api/auth/me")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("john"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

}
