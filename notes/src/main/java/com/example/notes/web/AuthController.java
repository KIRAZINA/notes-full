package com.example.notes.web;

import com.example.notes.audit.AuditService;
import com.example.notes.security.JwtService;
import com.example.notes.security.LoginAttemptService;
import com.example.notes.security.TokenBlacklistService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.user.CurrentUserResolver;
import com.example.notes.user.User;
import com.example.notes.user.UserService;
import com.example.notes.web.dto.*;
import com.example.notes.web.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final TokenBlacklistService tokenBlacklistService;
    private final AuditService auditService;
    private final CurrentUserResolver currentUserResolver;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserMapper userMapper,
                          LoginAttemptService loginAttemptService,
                          TokenBlacklistService tokenBlacklistService,
                          AuditService auditService,
                          CurrentUserResolver currentUserResolver) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.loginAttemptService = loginAttemptService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.auditService = auditService;
        this.currentUserResolver = currentUserResolver;
    }

    /**
     * Register a new user and return JWT token.
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody @Valid RegisterRequest req) {
        User user = userService.register(req.username(), req.email(), req.password());
        String token = jwtService.generateToken(user.getUsername());
        auditService.record(user.getId(), "REGISTER", "USER", user.getId(), "User registered");
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
            auditService.record(null, "LOGIN_FAILURE", "USER", null, "Account locked for username: " + username);
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
            auditService.record(((AppUserDetails) auth.getPrincipal()).getId(), "LOGIN_SUCCESS", "USER", null, "Successful login");
            
            String token = jwtService.generateToken(((AppUserDetails) auth.getPrincipal()).getUsername());
            return ResponseEntity.ok(ApiResponse.ok(new AuthResponse(token)));
            
        } catch (AuthenticationException e) {
            // Login failed - increment failed attempts
            loginAttemptService.loginFailed(username);
            auditService.record(null, "LOGIN_FAILURE", "USER", null, "Failed login for username: " + username);
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
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal Object principal) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        User user = userService.getByIdOrThrow(ownerId);
        return ApiResponse.ok(userMapper.toResponse(user));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request,
                                    @AuthenticationPrincipal Object principal) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        String token = resolveToken(request);
        tokenBlacklistService.blacklistToken(token);
        auditService.record(ownerId, "LOGOUT", "USER", ownerId, "User logged out");
        return ApiResponse.ok(null);
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal Object principal,
                                            @RequestBody @Valid PasswordChangeRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        userService.changePassword(ownerId, req.oldPassword(), req.newPassword());
        auditService.record(ownerId, "CHANGE_PASSWORD", "USER", ownerId, "Password changed");
        return ApiResponse.ok(null);
    }

    @PutMapping("/email")
    public ApiResponse<Void> changeEmail(@AuthenticationPrincipal Object principal,
                                         @RequestBody @Valid EmailChangeRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        userService.changeEmail(ownerId, req.email());
        auditService.record(ownerId, "CHANGE_EMAIL", "USER", ownerId, "Email updated");
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> deleteAccount(@AuthenticationPrincipal Object principal) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        userService.deleteUser(ownerId);
        auditService.record(ownerId, "DELETE_ACCOUNT", "USER", ownerId, "User account deleted");
        return ApiResponse.ok(null);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
