package com.example.notes.web;

import com.example.notes.note.Note;
import com.example.notes.note.NoteService;
import com.example.notes.user.CurrentUserResolver;
import com.example.notes.web.dto.ApiResponse;
import com.example.notes.web.dto.NoteResponse;
import com.example.notes.web.dto.NoteTagsSetRequest;
import com.example.notes.web.mapper.NoteMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints to manage tags on a note.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteTagController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;
    private final CurrentUserResolver currentUserResolver;

    public NoteTagController(NoteService noteService, NoteMapper noteMapper, CurrentUserResolver currentUserResolver) {
        this.noteService = noteService;
        this.noteMapper = noteMapper;
        this.currentUserResolver = currentUserResolver;
    }

    @RequestMapping(path = "/{id}/tags/{tagId}", method = {RequestMethod.PUT, RequestMethod.POST})
    public ApiResponse<NoteResponse> addTag(@AuthenticationPrincipal Object principal,
                               @PathVariable Long id,
                               @PathVariable Long tagId) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Note note = noteService.addTag(ownerId, id, tagId);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public ApiResponse<NoteResponse> removeTag(@AuthenticationPrincipal Object principal,
                                  @PathVariable Long id,
                                  @PathVariable Long tagId) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Note note = noteService.removeTag(ownerId, id, tagId);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @PutMapping("/{id}/tags")
    public ApiResponse<NoteResponse> setTags(@AuthenticationPrincipal Object principal,
                                @PathVariable Long id,
                                @RequestBody @Valid NoteTagsSetRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Note note = noteService.setTags(ownerId, id, req.tagIds());
        return ApiResponse.ok(noteMapper.toResponse(note));
    }
}
