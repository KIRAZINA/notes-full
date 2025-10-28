package com.example.notes.web;

import com.example.notes.security.JwtService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.user.User;
import com.example.notes.user.UserService;
import com.example.notes.web.dto.*;
import com.example.notes.web.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints returning JWT and user info.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    /**
     * Register a new user and return JWT token.
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody @Valid RegisterRequest req) {
        User user = userService.register(req.username(), req.email(), req.password());
        String token = jwtService.generateToken(user.getUsername());
        return ApiResponse.ok(new AuthResponse(token));
    }

    /**
     * Authenticate user and return JWT token.
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        String token = jwtService.generateToken(((AppUserDetails) auth.getPrincipal()).getUsername());
        return ApiResponse.ok(new AuthResponse(token));
    }

    /**
     * Get current authenticated user info.
     */
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal AppUserDetails principal) {
        User user = userService.getByIdOrThrow(principal.getId());
        return userMapper.toResponse(user);
    }
}
