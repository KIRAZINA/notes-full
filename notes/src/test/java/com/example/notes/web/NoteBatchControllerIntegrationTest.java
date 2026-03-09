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
class NoteBatchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void archiveNotes_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L, 2L));

        mockMvc.perform(post("/api/notes/batch/archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void restoreArchive_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L));

        mockMvc.perform(post("/api/notes/batch/restore-archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void trashNotes_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L));

        mockMvc.perform(post("/api/notes/batch/trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void restoreTrash_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L));

        mockMvc.perform(post("/api/notes/batch/restore-trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deletePermanent_shouldReturnOk() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L));

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
}
