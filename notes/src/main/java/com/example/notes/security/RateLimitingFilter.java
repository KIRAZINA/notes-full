package com.example.notes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory rate limiting for API requests.
 * This is a quick MVP-level protection; production should use Redis or distributed bucket support.
 */
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int DEFAULT_LIMIT = 100;
    private static final int EXPENSIVE_LIMIT = 20;
    private static final long WINDOW_MS = 60_000L;

    private record Window(int count, long resetAt) {}

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final Set<String> expensivePaths = Set.of("/api/notes/batch", "/api/notes");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = extractKey(request);
        int limit = determineLimit(request);
        if (!isAllowed(key, limit)) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }
        filterChain.doFilter(request, response);
    }

    private String extractKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            return "user:" + auth.getName();
        }
        return "ip:" + request.getRemoteAddr();
    }

    private int determineLimit(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/notes/batch") || (uri.equals("/api/notes") && request.getParameter("q") != null)) {
            return EXPENSIVE_LIMIT;
        }
        return DEFAULT_LIMIT;
    }

    private boolean isAllowed(String key, int limit) {
        long now = System.currentTimeMillis();
        Window updated = windows.compute(key, (k, current) -> {
            if (current == null || now >= current.resetAt) {
                return new Window(1, now + WINDOW_MS);
            }
            if (current.count >= limit) {
                return current;
            }
            return new Window(current.count + 1, current.resetAt);
        });
        return updated.count <= limit;
    }
}
