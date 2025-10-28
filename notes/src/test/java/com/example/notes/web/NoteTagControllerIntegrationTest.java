package com.example.notes.web;

import com.example.notes.web.dto.NoteTagsSetRequest;
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
class NoteTagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addTag_shouldReturnNoteResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/tags/{tagId}", 1, 5)
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void removeTag_shouldReturnNoteResponse() throws Exception {
        mockMvc.perform(delete("/api/notes/{id}/tags/{tagId}", 1, 5)
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void setTags_shouldReturnNoteResponse() throws Exception {
        NoteTagsSetRequest request = new NoteTagsSetRequest(List.of(5L, 6L));

        mockMvc.perform(put("/api/notes/{id}/tags", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
