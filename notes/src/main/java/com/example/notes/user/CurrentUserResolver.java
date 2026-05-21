package com.example.notes.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class CurrentUserResolver {

    private final UserService userService;

    public CurrentUserResolver(UserService userService) {
        this.userService = userService;
    }

    public Long resolveUserId(Object principal) {
        if (principal == null) {
            throw new IllegalStateException("Authenticated principal is required");
        }
        if (principal instanceof AppUserDetails appUserDetails) {
            return appUserDetails.getId();
        }
        if (principal instanceof UserDetails userDetails) {
            return userService.findByUsernameOrThrow(userDetails.getUsername()).getId();
        }
        if (principal instanceof Principal genericPrincipal) {
            return userService.findByUsernameOrThrow(genericPrincipal.getName()).getId();
        }
        throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
    }
}
