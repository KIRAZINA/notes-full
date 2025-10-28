package com.example.notes.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for NoteActionController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class NoteActionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void archiveNote_shouldReturnNoteResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/archive", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void restoreArchive_shouldReturnNoteResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/restore-archive", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void moveToTrash_shouldReturnNoteResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/trash", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void restoreFromTrash_shouldReturnNoteResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/restore-trash", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deletePermanent_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/notes/{id}/permanent", 1))
                .andExpect(status().isOk());
    }
}
