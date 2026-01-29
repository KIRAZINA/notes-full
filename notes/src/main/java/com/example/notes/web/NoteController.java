package com.example.notes.web;

import com.example.notes.note.Note;
import com.example.notes.note.NoteService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.web.dto.*;

import com.example.notes.web.mapper.NoteMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notes endpoints with search, filtering, and unified page response.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;

    public NoteController(NoteService noteService, NoteMapper noteMapper) {
        this.noteService = noteService;
        this.noteMapper = noteMapper;
    }

    /**
     * Get a paginated list of notes with optional filters.
     */
    @GetMapping
    public ApiResponse<PageResponse<NoteResponse>> list(
            @AuthenticationPrincipal AppUserDetails principal,
            Pageable pageable,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) Boolean trashed,
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<Long> tagIds
    ) {
        var page = noteService.listNotes(principal.getId(), pageable, archived, trashed, pinned, q, true, tagIds);
        var content = page.map(noteMapper::toResponse).getContent();
        var response = new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return ApiResponse.ok(response);
    }

    /**
     * Create a new note.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> create(
            @AuthenticationPrincipal AppUserDetails principal,
            @RequestBody @Valid NoteCreateRequest req
    ) {
        var note = noteService.createNote(principal.getId(), req.title(), req.content());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(noteMapper.toResponse(note)));
    }

    /**
     * Get a single note by ID.
     */
    @GetMapping("/{id}")
    public ApiResponse<NoteResponse> get(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Long id
    ) {
        return ApiResponse.ok(noteMapper.toResponse(noteService.getNote(principal.getId(), id)));
    }

    /**
     * Update an existing note.
     */
    @PutMapping("/{id}")
    public ApiResponse<NoteResponse> update(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Long id,
            @RequestBody @Valid NoteUpdateRequest req
    ) {
        var note = noteService.updateNote(
                principal.getId(), id,
                req.title(), req.content(),
                req.pinned(), req.archived(), req.trashed()
        );
        return ApiResponse.ok(noteMapper.toResponse(note));
    }

    /**
     * Delete a note by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Long id
    ) {
        noteService.deleteNote(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
