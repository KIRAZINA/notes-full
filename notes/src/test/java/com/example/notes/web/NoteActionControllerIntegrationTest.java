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
@org.springframework.test.context.TestPropertySource(properties = "test.security.mock=true")
class NoteActionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.example.notes.note.NoteService noteService;

    @Autowired
    private com.example.notes.user.UserService userService;

    private Long noteId;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void archiveNote_shouldReturnApiResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/archive", noteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(noteId.intValue()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void restoreArchive_shouldReturnApiResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/restore-archive", noteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(noteId.intValue()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void moveToTrash_shouldReturnApiResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/trash", noteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(noteId.intValue()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void restoreFromTrash_shouldReturnApiResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/restore-trash", noteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(noteId.intValue()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deletePermanent_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/notes/{id}/permanent", noteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @org.junit.jupiter.api.BeforeEach
    void createNote() {
        var user = userService.findByUsernameOrThrow("testuser");
        var note = noteService.createNote(user.getId(), "Integration seed", "content");
        this.noteId = note.getId();
    }
}
