package com.example.notes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 OpenAPI configuration for Swagger UI and API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notes API")
                        .description("REST API for managing notes, tags, and users")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("KIRA")
                                .email("your-email@example.com")
                        )
                );
    }
}
