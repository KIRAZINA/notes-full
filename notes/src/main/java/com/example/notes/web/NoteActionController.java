package com.example.notes.web;

import com.example.notes.note.Note;
import com.example.notes.note.NoteActionService;
import com.example.notes.user.AppUserDetails;
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

    public NoteActionController(NoteActionService noteActionService, NoteMapper noteMapper) {
        this.noteActionService = noteActionService;
        this.noteMapper = noteMapper;
    }

    @PostMapping("/{id}/archive")
    public ApiResponse<NoteResponse> archive(@AuthenticationPrincipal AppUserDetails principal,
                                @PathVariable Long id) {
        Note note = noteActionService.moveToArchive(principal.getId(), id);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @PostMapping("/{id}/restore-archive")
    public ApiResponse<NoteResponse> restoreArchive(@AuthenticationPrincipal AppUserDetails principal,
                                       @PathVariable Long id) {
        Note note = noteActionService.restoreFromArchive(principal.getId(), id);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @PostMapping("/{id}/trash")
    public ApiResponse<NoteResponse> trash(@AuthenticationPrincipal AppUserDetails principal,
                              @PathVariable Long id) {
        Note note = noteActionService.moveToTrash(principal.getId(), id);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @PostMapping("/{id}/restore-trash")
    public ApiResponse<NoteResponse> restoreTrash(@AuthenticationPrincipal AppUserDetails principal,
                                     @PathVariable Long id) {
        Note note = noteActionService.restoreFromTrash(principal.getId(), id);
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> deletePermanent(@AuthenticationPrincipal AppUserDetails principal,
                                @PathVariable Long id) {
        noteActionService.deletePermanently(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
