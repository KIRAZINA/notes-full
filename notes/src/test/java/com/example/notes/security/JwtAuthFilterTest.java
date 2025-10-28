package com.example.notes.security;

import com.example.notes.user.AppUserDetails;
import com.example.notes.user.User;
import com.example.notes.user.UserService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final UserService userService = mock(UserService.class);

    private final JwtAuthFilter filter = new JwtAuthFilter(jwtService, userService);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldAuthenticateWhenValidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.isValid("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("john");

        User user = User.builder().id(1L).username("john").roles(Set.of("ROLE_USER")).build();
        AppUserDetails userDetails = new AppUserDetails(user);
        when(userService.loadUserByUsername("john")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(AppUserDetails.class);
        assertThat(((AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername()).isEqualTo("john");
    }

    @Test
    void doFilterInternal_shouldNotAuthenticateWhenNoHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticateWhenInvalidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.isValid("invalid-token")).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService).isValid("invalid-token");
        verify(jwtService, never()).extractUsername(any());
        verifyNoInteractions(userService);
    }
}
