package com.example.notes.config;

/**
 * DEPRECATED: CORS configuration has been consolidated into SecurityConfig.
 * This file is kept for reference but should be deleted.
 * 
 * The CorsConfigurationSource bean is now defined in SecurityConfig.java
 * to avoid conflicts between WebMvcConfigurer and SecurityFilterChain CORS configurations.
 * 
 * This approach ensures a single source of truth for CORS settings.
 */
@Deprecated(forRemoval = true)
public class CorsConfig {
    // CORS configuration moved to SecurityConfig.corsConfigurationSource()
}
