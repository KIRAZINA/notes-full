package com.example.notes.web;

import com.example.notes.note.Note;
import com.example.notes.note.NoteService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.web.dto.NoteResponse;
import com.example.notes.web.dto.NoteTagsSetRequest;
import com.example.notes.web.mapper.NoteMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints to batch set tags on a note.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteTagController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;

    public NoteTagController(NoteService noteService, NoteMapper noteMapper) {
        this.noteService = noteService;
        this.noteMapper = noteMapper;
    }

    @PostMapping("/{id}/tags/{tagId}")
    public NoteResponse addTag(@AuthenticationPrincipal AppUserDetails principal,
                               @PathVariable Long id,
                               @PathVariable Long tagId) {
        Note note = noteService.addTag(principal.getId(), id, tagId);
        return noteMapper.toResponse(note);
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public NoteResponse removeTag(@AuthenticationPrincipal AppUserDetails principal,
                                  @PathVariable Long id,
                                  @PathVariable Long tagId) {
        Note note = noteService.removeTag(principal.getId(), id, tagId);
        return noteMapper.toResponse(note);
    }

    @PutMapping("/{id}/tags")
    public NoteResponse setTags(@AuthenticationPrincipal AppUserDetails principal,
                                @PathVariable Long id,
                                @RequestBody NoteTagsSetRequest req) {
        Note note = noteService.setTags(principal.getId(), id, req.tagIds());
        return noteMapper.toResponse(note);
    }
}
