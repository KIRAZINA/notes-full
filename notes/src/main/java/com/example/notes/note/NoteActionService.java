package com.example.notes.note;

import com.example.notes.common.NotFoundException;
import com.example.notes.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Handles note CRUD and archive/trash operations.
 */
@Service
@Transactional
public class NoteActionService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    public NoteActionService(NoteRepository noteRepository, UserService userService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    private Note loadOwned(Long ownerId, Long noteId) {
        var note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NotFoundException("Note not found"));
        if (!note.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Note not found");
        }
        return note;
    }

    public Note moveToArchive(Long ownerId, Long noteId) {
        Note note = loadOwned(ownerId, noteId);
        note.setArchived(true);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    public Note restoreFromArchive(Long ownerId, Long noteId) {
        Note note = loadOwned(ownerId, noteId);
        note.setArchived(false);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    public Note moveToTrash(Long ownerId, Long noteId) {
        Note note = loadOwned(ownerId, noteId);
        note.setTrashed(true);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    public Note restoreFromTrash(Long ownerId, Long noteId) {
        Note note = loadOwned(ownerId, noteId);
        note.setTrashed(false);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    public void deletePermanently(Long ownerId, Long noteId) {
        Note note = loadOwned(ownerId, noteId);
        noteRepository.delete(note);
    }
}
