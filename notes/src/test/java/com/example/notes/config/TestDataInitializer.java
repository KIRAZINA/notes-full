package com.example.notes.config;

import com.example.notes.note.NoteRepository;
import com.example.notes.note.NoteService;
import com.example.notes.user.UserRepository;
import com.example.notes.user.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataInitializer {

    @Bean
    public ApplicationRunner testDataInitializerRunner(UserService userService,
                                                       UserRepository userRepository,
                                                       NoteRepository noteRepository,
                                                       NoteService noteService) {
        return args -> {
            if (userRepository.findByUsername("testuser").isEmpty()) {
                var user = userService.register("testuser", "testuser@example.com", "password");
                if (user != null && noteRepository.count() == 0) {
                    noteService.createNote(user.getId(), "Seed note 1", "Note content for test user");
                    noteService.createNote(user.getId(), "Seed note 2", "Another note content for test user");
                }
            }
        };
    }
}
