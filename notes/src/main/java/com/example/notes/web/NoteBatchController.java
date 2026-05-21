package com.example.notes.web;

import com.example.notes.audit.AuditService;
import com.example.notes.note.Note;
import com.example.notes.note.NoteBatchService;
import com.example.notes.user.CurrentUserResolver;
import com.example.notes.web.dto.ApiResponse;
import com.example.notes.web.dto.NoteBatchRequest;
import com.example.notes.web.dto.NoteResponse;
import com.example.notes.web.mapper.NoteMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints for batch operations on notes.
 */
@RestController
@RequestMapping("/api/notes/batch")
public class NoteBatchController {

    private final NoteBatchService noteBatchService;
    private final NoteMapper noteMapper;
    private final AuditService auditService;
    private final CurrentUserResolver currentUserResolver;

    public NoteBatchController(NoteBatchService noteBatchService, NoteMapper noteMapper, AuditService auditService, CurrentUserResolver currentUserResolver) {
        this.noteBatchService = noteBatchService;
        this.noteMapper = noteMapper;
        this.auditService = auditService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/archive")
    public ApiResponse<List<NoteResponse>> archive(@AuthenticationPrincipal Object principal,
                                                   @RequestBody @Valid NoteBatchRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        List<Note> notes = noteBatchService.archiveNotes(ownerId, req.noteIds());
        auditService.record(ownerId, "NOTE_BATCH_ARCHIVE", "NOTE", null,
                "Archived " + notes.size() + " notes");
        return ApiResponse.ok(notes.stream().map(noteMapper::toResponse).toList());
    }

    @PostMapping("/restore-archive")
    public ApiResponse<List<NoteResponse>> restoreArchive(@AuthenticationPrincipal Object principal,
                                                          @RequestBody @Valid NoteBatchRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        List<Note> notes = noteBatchService.restoreFromArchive(ownerId, req.noteIds());
        auditService.record(ownerId, "NOTE_BATCH_RESTORE_ARCHIVE", "NOTE", null,
                "Restored " + notes.size() + " archived notes");
        return ApiResponse.ok(notes.stream().map(noteMapper::toResponse).toList());
    }

    @PostMapping("/trash")
    public ApiResponse<List<NoteResponse>> trash(@AuthenticationPrincipal Object principal,
                                                 @RequestBody @Valid NoteBatchRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        List<Note> notes = noteBatchService.trashNotes(ownerId, req.noteIds());
        auditService.record(ownerId, "NOTE_BATCH_TRASH", "NOTE", null,
                "Moved " + notes.size() + " notes to trash");
        return ApiResponse.ok(notes.stream().map(noteMapper::toResponse).toList());
    }

    @PostMapping("/restore-trash")
    public ApiResponse<List<NoteResponse>> restoreTrash(@AuthenticationPrincipal Object principal,
                                                        @RequestBody @Valid NoteBatchRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        List<Note> notes = noteBatchService.restoreFromTrash(ownerId, req.noteIds());
        auditService.record(ownerId, "NOTE_BATCH_RESTORE_TRASH", "NOTE", null,
                "Restored " + notes.size() + " trashed notes");
        return ApiResponse.ok(notes.stream().map(noteMapper::toResponse).toList());
    }

    @DeleteMapping("/permanent")
    public ApiResponse<Void> deletePermanent(@AuthenticationPrincipal Object principal,
                                             @RequestBody @Valid NoteBatchRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        noteBatchService.deletePermanently(ownerId, req.noteIds());
        auditService.record(ownerId, "NOTE_BATCH_PERMANENT_DELETE", "NOTE", null,
                "Permanently deleted " + req.noteIds().size() + " notes");
        return ApiResponse.ok(null);
    }
}
