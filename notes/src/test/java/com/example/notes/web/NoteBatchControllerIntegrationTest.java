package com.example.notes.web;

import com.example.notes.web.dto.NoteBatchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.TestPropertySource(properties = "test.security.mock=true")
class NoteBatchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.example.notes.note.NoteService noteService;

    @Autowired
    private com.example.notes.user.UserService userService;

    private Long note1;
    private Long note2;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void archiveNotes_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(note1, note2));

        mockMvc.perform(post("/api/notes/batch/archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void restoreArchive_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(note1));

        mockMvc.perform(post("/api/notes/batch/restore-archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void trashNotes_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(note1));

        mockMvc.perform(post("/api/notes/batch/trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void restoreTrash_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(note1));

        mockMvc.perform(post("/api/notes/batch/restore-trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deletePermanent_shouldReturnOk() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(note1));

        mockMvc.perform(delete("/api/notes/batch/permanent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void archiveNotes_withEmptyIds_shouldReturnValidationError() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of());

        mockMvc.perform(post("/api/notes/batch/archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("validation_error"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void archiveNotes_withNullIds_shouldReturnValidationError() throws Exception {
        mockMvc.perform(post("/api/notes/batch/archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"noteIds\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("validation_error"));
    }

    @org.junit.jupiter.api.BeforeEach
    void createNotes() {
        var user = userService.findByUsernameOrThrow("testuser");
        var n1 = noteService.createNote(user.getId(), "Batch note 1", "content");
        var n2 = noteService.createNote(user.getId(), "Batch note 2", "content");
        this.note1 = n1.getId();
        this.note2 = n2.getId();
    }
}
