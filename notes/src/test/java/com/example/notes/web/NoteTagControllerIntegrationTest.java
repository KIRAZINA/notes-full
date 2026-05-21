package com.example.notes.web;

import com.example.notes.web.dto.NoteTagsSetRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import com.example.notes.user.UserService;
import com.example.notes.user.UserRepository;
import com.example.notes.note.NoteService;
import com.example.notes.tag.TagService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "test.security.mock=true")
class NoteTagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteService noteService;

    @Autowired
    private TagService tagService;

    @Autowired
    private com.example.notes.tag.TagRepository tagRepository;

    private Long noteId;
    private Long tagId;

    @BeforeEach
    void setupData() {
        var user = userRepository.findByUsername("testuser").orElseGet(() -> userService.register("testuser", "testuser@example.com", "password"));
        var note = noteService.createNote(user.getId(), "Integration test note", "Some content");
        // create or reuse a tag with unique name per test run
        var existing = tagRepository.findByOwnerIdAndName(user.getId(), "test-tag");
        var tag = existing.orElseGet(() -> tagService.createTag(user.getId(), "test-tag"));
        noteId = note.getId();
        tagId = tag.getId();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void addTag_shouldReturnApiResponse() throws Exception {
        mockMvc.perform(post("/api/notes/{id}/tags/{tagId}", noteId, tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(noteId.intValue()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void removeTag_shouldReturnApiResponse() throws Exception {
        mockMvc.perform(delete("/api/notes/{id}/tags/{tagId}", noteId, tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(noteId.intValue()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void setTags_shouldReturnApiResponse() throws Exception {
        NoteTagsSetRequest request = new NoteTagsSetRequest(List.of(tagId));

        mockMvc.perform(put("/api/notes/{id}/tags", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(noteId.intValue()));
    }
}
