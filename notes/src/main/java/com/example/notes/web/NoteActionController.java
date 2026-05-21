package com.example.notes.web;

import com.example.notes.note.Note;
import com.example.notes.note.NoteActionService;
import com.example.notes.user.CurrentUserResolver;
import com.example.notes.web.dto.ApiResponse;
import com.example.notes.web.dto.NoteResponse;
import com.example.notes.web.mapper.NoteMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints for archive and trash operations.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteActionController {

    private final NoteActionService noteActionService;
    private final NoteMapper noteMapper;
    private final CurrentUserResolver currentUserResolver;

    public NoteActionController(NoteActionService noteActionService, NoteMapper noteMapper, CurrentUserResolver currentUserResolver) {
        this.noteActionService = noteActionService;
        this.noteMapper = noteMapper;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/{id}/archive")
    public ApiResponse<NoteResponse> archive(@AuthenticationPrincipal Object principal,
                                @PathVariable Long id) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Note note = noteActionService.moveToArchive(ownerId, id);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @PostMapping("/{id}/restore-archive")
    public ApiResponse<NoteResponse> restoreArchive(@AuthenticationPrincipal Object principal,
                                       @PathVariable Long id) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Note note = noteActionService.restoreFromArchive(ownerId, id);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @PostMapping("/{id}/trash")
    public ApiResponse<NoteResponse> trash(@AuthenticationPrincipal Object principal,
                              @PathVariable Long id) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Note note = noteActionService.moveToTrash(ownerId, id);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @PostMapping("/{id}/restore-trash")
    public ApiResponse<NoteResponse> restoreTrash(@AuthenticationPrincipal Object principal,
                                     @PathVariable Long id) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Note note = noteActionService.restoreFromTrash(ownerId, id);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> deletePermanent(@AuthenticationPrincipal Object principal,
                                @PathVariable Long id) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        noteActionService.deletePermanently(ownerId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
