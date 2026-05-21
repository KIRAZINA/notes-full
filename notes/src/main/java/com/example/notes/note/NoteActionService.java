package com.example.notes.note;

import com.example.notes.common.NotFoundException;
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

    public NoteActionService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
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
        if (note.isTrashed()) {
            throw new IllegalArgumentException("Cannot archive a trashed note");
        }
        note.setArchived(true);
        note.setPinned(false);
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
        note.setArchived(false);
        note.setPinned(false);
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
