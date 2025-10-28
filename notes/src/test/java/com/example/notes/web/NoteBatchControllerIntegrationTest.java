package com.example.notes.web;

import com.example.notes.web.dto.NoteBatchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    void archiveNotes_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L, 2L));

        mockMvc.perform(post("/api/notes/batch/archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void restoreArchive_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L));

        mockMvc.perform(post("/api/notes/batch/restore-archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void trashNotes_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L));

        mockMvc.perform(post("/api/notes/batch/trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void restoreTrash_shouldReturnListOfNotes() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L));

        mockMvc.perform(post("/api/notes/batch/restore-trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deletePermanent_shouldReturnOk() throws Exception {
        NoteBatchRequest request = new NoteBatchRequest(List.of(1L));

        mockMvc.perform(delete("/api/notes/batch/permanent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
