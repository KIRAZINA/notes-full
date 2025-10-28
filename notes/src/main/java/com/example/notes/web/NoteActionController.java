package com.example.notes.web;

import com.example.notes.note.Note;
import com.example.notes.note.NoteActionService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.web.dto.NoteResponse;
import com.example.notes.web.mapper.NoteMapper;
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
    public NoteResponse archive(@AuthenticationPrincipal AppUserDetails principal,
                                @PathVariable Long id) {
        Note note = noteActionService.moveToArchive(principal.getId(), id);
        return noteMapper.toResponse(note);
    }

    @PostMapping("/{id}/restore-archive")
    public NoteResponse restoreArchive(@AuthenticationPrincipal AppUserDetails principal,
                                       @PathVariable Long id) {
        Note note = noteActionService.restoreFromArchive(principal.getId(), id);
        return noteMapper.toResponse(note);
    }

    @PostMapping("/{id}/trash")
    public NoteResponse trash(@AuthenticationPrincipal AppUserDetails principal,
                              @PathVariable Long id) {
        Note note = noteActionService.moveToTrash(principal.getId(), id);
        return noteMapper.toResponse(note);
    }

    @PostMapping("/{id}/restore-trash")
    public NoteResponse restoreTrash(@AuthenticationPrincipal AppUserDetails principal,
                                     @PathVariable Long id) {
        Note note = noteActionService.restoreFromTrash(principal.getId(), id);
        return noteMapper.toResponse(note);
    }

    @DeleteMapping("/{id}/permanent")
    public void deletePermanent(@AuthenticationPrincipal AppUserDetails principal,
                                @PathVariable Long id) {
        noteActionService.deletePermanently(principal.getId(), id);
    }
}
