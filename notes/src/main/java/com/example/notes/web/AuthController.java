package com.example.notes.web;

import com.example.notes.security.JwtService;
import com.example.notes.security.LoginAttemptService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.user.User;
import com.example.notes.user.UserService;
import com.example.notes.web.dto.*;
import com.example.notes.web.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints returning JWT and user info.
 * Includes brute-force protection via LoginAttemptService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserMapper userMapper,
                          LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.loginAttemptService = loginAttemptService;
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
     * Implements brute-force protection by tracking failed login attempts.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest req) {
        String username = req.username();

        // Check if account is locked due to too many failed attempts
        if (loginAttemptService.isAccountLocked(username)) {
            return ResponseEntity.status(429)  // Too Many Requests
                    .body(ApiResponse.fail(new ApiResponse.ApiError(
                        "account_locked",
                        "Account is temporarily locked. Try again later."
                    )));
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, req.password())
            );
            
            // Login successful - reset failed attempts
            loginAttemptService.loginSucceeded(username);
            
            String token = jwtService.generateToken(((AppUserDetails) auth.getPrincipal()).getUsername());
            return ResponseEntity.ok(ApiResponse.ok(new AuthResponse(token)));
            
        } catch (BadCredentialsException e) {
            // Login failed - increment failed attempts
            loginAttemptService.loginFailed(username);
            
            return ResponseEntity.status(401)
                    .body(ApiResponse.fail(new ApiResponse.ApiError(
                        "invalid_credentials",
                        "Invalid username or password"
                    )));
        }
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
