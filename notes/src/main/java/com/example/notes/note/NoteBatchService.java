package com.example.notes.note;

import com.example.notes.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Batch operations for notes (archive, trash, restore, delete).
 */
@Service
@Transactional
public class NoteBatchService {

    private final NoteRepository noteRepository;

    public NoteBatchService(NoteRepository noteRepository) {
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

    public List<Note> archiveNotes(Long ownerId, List<Long> ids) {
        return ids.stream().map(id -> {
            Note note = loadOwned(ownerId, id);
            if (note.isTrashed()) {
                throw new IllegalArgumentException("Cannot archive a trashed note");
            }
            note.setArchived(true);
            note.setPinned(false);
            note.setUpdatedAt(Instant.now());
            return noteRepository.save(note);
        }).toList();
    }

    public List<Note> restoreFromArchive(Long ownerId, List<Long> ids) {
        return ids.stream().map(id -> {
            Note note = loadOwned(ownerId, id);
            note.setArchived(false);
            note.setUpdatedAt(Instant.now());
            return noteRepository.save(note);
        }).toList();
    }

    public List<Note> trashNotes(Long ownerId, List<Long> ids) {
        return ids.stream().map(id -> {
            Note note = loadOwned(ownerId, id);
            note.setTrashed(true);
            note.setArchived(false);
            note.setPinned(false);
            note.setUpdatedAt(Instant.now());
            return noteRepository.save(note);
        }).toList();
    }

    public List<Note> restoreFromTrash(Long ownerId, List<Long> ids) {
        return ids.stream().map(id -> {
            Note note = loadOwned(ownerId, id);
            note.setTrashed(false);
            note.setUpdatedAt(Instant.now());
            return noteRepository.save(note);
        }).toList();
    }

    public void deletePermanently(Long ownerId, List<Long> ids) {
        ids.forEach(id -> {
            Note note = loadOwned(ownerId, id);
            noteRepository.delete(note);
        });
    }
}
