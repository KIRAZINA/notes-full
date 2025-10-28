package com.example.notes.web;

import com.example.notes.note.Note;
import com.example.notes.note.NoteBatchService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.web.dto.ApiResponse;
import com.example.notes.web.dto.NoteBatchRequest;
import com.example.notes.web.dto.NoteResponse;
import com.example.notes.web.mapper.NoteMapper;
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

    public NoteBatchController(NoteBatchService noteBatchService, NoteMapper noteMapper) {
        this.noteBatchService = noteBatchService;
        this.noteMapper = noteMapper;
    }

    @PostMapping("/archive")
    public ApiResponse<List<NoteResponse>> archive(@AuthenticationPrincipal AppUserDetails principal,
                                                   @RequestBody NoteBatchRequest req) {
        List<Note> notes = noteBatchService.archiveNotes(principal.getId(), req.noteIds());
        return ApiResponse.ok(notes.stream().map(noteMapper::toResponse).toList());
    }

    @PostMapping("/restore-archive")
    public ApiResponse<List<NoteResponse>> restoreArchive(@AuthenticationPrincipal AppUserDetails principal,
                                                          @RequestBody NoteBatchRequest req) {
        List<Note> notes = noteBatchService.restoreFromArchive(principal.getId(), req.noteIds());
        return ApiResponse.ok(notes.stream().map(noteMapper::toResponse).toList());
    }

    @PostMapping("/trash")
    public ApiResponse<List<NoteResponse>> trash(@AuthenticationPrincipal AppUserDetails principal,
                                                 @RequestBody NoteBatchRequest req) {
        List<Note> notes = noteBatchService.trashNotes(principal.getId(), req.noteIds());
        return ApiResponse.ok(notes.stream().map(noteMapper::toResponse).toList());
    }

    @PostMapping("/restore-trash")
    public ApiResponse<List<NoteResponse>> restoreTrash(@AuthenticationPrincipal AppUserDetails principal,
                                                        @RequestBody NoteBatchRequest req) {
        List<Note> notes = noteBatchService.restoreFromTrash(principal.getId(), req.noteIds());
        return ApiResponse.ok(notes.stream().map(noteMapper::toResponse).toList());
    }

    @DeleteMapping("/permanent")
    public ApiResponse<Void> deletePermanent(@AuthenticationPrincipal AppUserDetails principal,
                                             @RequestBody NoteBatchRequest req) {
        noteBatchService.deletePermanently(principal.getId(), req.noteIds());
        return ApiResponse.ok(null);
    }
}
