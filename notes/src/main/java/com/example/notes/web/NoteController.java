package com.example.notes.web;

import com.example.notes.note.Note;
import com.example.notes.note.NoteService;
import com.example.notes.user.CurrentUserResolver;
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
    private final CurrentUserResolver currentUserResolver;

    public NoteController(NoteService noteService, NoteMapper noteMapper, CurrentUserResolver currentUserResolver) {
        this.noteService = noteService;
        this.noteMapper = noteMapper;
        this.currentUserResolver = currentUserResolver;
    }

    /**
     * Get a paginated list of notes with optional filters.
     */
    @GetMapping
    public ApiResponse<PageResponse<NoteResponse>> list(
            @AuthenticationPrincipal Object principal,
            Pageable pageable,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) Boolean trashed,
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean searchInContent,
            @RequestParam(required = false) List<Long> tagIds
    ) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        boolean includeContent = searchInContent == null || searchInContent;
        var page = noteService.listNotes(ownerId, pageable, archived, trashed, pinned, q, includeContent, tagIds);
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
            @AuthenticationPrincipal Object principal,
            @RequestBody @Valid NoteCreateRequest req
    ) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        var note = noteService.createNote(ownerId, req.title(), req.content());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(noteMapper.toResponse(note)));
    }

    /**
     * Get a single note by ID.
     */
    @GetMapping("/{id}")
    public ApiResponse<NoteResponse> get(
            @AuthenticationPrincipal Object principal,
            @PathVariable Long id
    ) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        return ApiResponse.ok(noteMapper.toResponse(noteService.getNote(ownerId, id)));
    }

    /**
     * Update an existing note.
     */
    @PutMapping("/{id}")
    public ApiResponse<NoteResponse> update(
            @AuthenticationPrincipal Object principal,
            @PathVariable Long id,
            @RequestBody @Valid NoteUpdateRequest req
    ) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        var note = noteService.updateNote(
                ownerId, id,
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
            @AuthenticationPrincipal Object principal,
            @PathVariable Long id
    ) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        noteService.deleteNote(ownerId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
