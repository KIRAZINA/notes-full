package com.example.notes.note;

import com.example.notes.audit.AuditService;
import com.example.notes.common.NotFoundException;
import com.example.notes.tag.Tag;
import com.example.notes.tag.TagService;
import com.example.notes.user.User;
import com.example.notes.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Note service: CRUD, filters, combined search, batch tag operations.
 */
@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;
    private final TagService tagService;
    private final AuditService auditService;

    public NoteService(NoteRepository noteRepository,
                       UserService userService,
                       TagService tagService,
                       AuditService auditService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
        this.tagService = tagService;
        this.auditService = auditService;
    }


    public Note createNote(Long ownerId, String title, String content) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        
        User owner = userService.getByIdOrThrow(ownerId);
        Note note = Note.builder()
                .owner(owner)
                .title(title)
                .content(content)
                .pinned(false)
                .archived(false)
                .trashed(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        Note saved = noteRepository.save(note);
        auditService.record(ownerId, "CREATE_NOTE", "NOTE", saved.getId(), "Created note with title: " + title);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Note> listNotes(Long ownerId, Pageable pageable,
                                Boolean archived, Boolean trashed, Boolean pinned,
                                String query, boolean searchInContent,
                                List<Long> tagIds) {
        User owner = userService.getByIdOrThrow(ownerId);

        // Priority: process filters in order
        if (archived != null) {
            return noteRepository.findByOwnerAndArchived(owner, archived, pageable);
        }
        if (trashed != null) {
            return noteRepository.findByOwnerAndTrashed(owner, trashed, pageable);
        }
        if (pinned != null) {
            return noteRepository.findByOwnerAndPinned(owner, pinned, pageable);
        }

        // Filter by tags (if provided and not empty)
        if (tagIds != null && !tagIds.isEmpty()) {
            if (tagIds.size() == 1) {
                return noteRepository.findByOwnerAndTagId(owner, tagIds.get(0), pageable);
            }
            return noteRepository.findByOwnerAndAllTagIds(owner, tagIds, tagIds.size(), pageable);
        }

        // Combined search (title + content)
        if (query != null && !query.isBlank()) {
            return noteRepository.searchTitleOrContent(owner, query, pageable);
        }

        // Default: return all notes for owner
        return noteRepository.findByOwner(owner, pageable);
    }

    @Transactional(readOnly = true)
    public Note getNote(Long ownerId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NotFoundException("Note not found"));
        ensureOwner(ownerId, note);
        return note;
    }

    public Note updateNote(Long ownerId, Long noteId, String title, String content,
                           Boolean pinned, Boolean archived, Boolean trashed) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NotFoundException("Note not found"));
        ensureOwner(ownerId, note);

        if (title != null) note.setTitle(title);
        if (content != null) note.setContent(content);
        if (pinned != null) note.setPinned(pinned);
        if (archived != null) note.setArchived(archived);
        if (trashed != null) note.setTrashed(trashed);

        note.setUpdatedAt(Instant.now());
        Note saved = noteRepository.save(note);
        auditService.record(ownerId, "UPDATE_NOTE", "NOTE", saved.getId(), "Updated note");
        return saved;
    }

    public void deleteNote(Long ownerId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NotFoundException("Note not found"));
        ensureOwner(ownerId, note);
        noteRepository.delete(note);
        auditService.record(ownerId, "DELETE_NOTE", "NOTE", noteId, "Deleted note");
    }

    public Note addTag(Long ownerId, Long noteId, Long tagId) {
        if (tagId == null || tagId <= 0) {
            throw new IllegalArgumentException("Tag ID must be a positive number");
        }
        Note note = getNote(ownerId, noteId);
        if (note.isTrashed()) {
            throw new IllegalArgumentException("Cannot tag trashed note");
        }
        Tag tag = tagService.getOwnedTagOrThrow(ownerId, tagId);
        note.getTags().add(tag);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    public Note removeTag(Long ownerId, Long noteId, Long tagId) {
        if (tagId == null || tagId <= 0) {
            throw new IllegalArgumentException("Tag ID must be a positive number");
        }
        Note note = getNote(ownerId, noteId);
        Tag tag = tagService.getOwnedTagOrThrow(ownerId, tagId);
        note.getTags().remove(tag);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    /**
     * Replaces note tags with the given set (owned by the user).
     * Validates that tag list is not null and all tags exist.
     */
    public Note setTags(Long ownerId, Long noteId, List<Long> tagIds) {
        if (tagIds == null) {
            throw new IllegalArgumentException("Tag IDs list cannot be null");
        }
        if (tagIds.isEmpty()) {
            // Allow empty tags - clears all tags from note
            Note note = getNote(ownerId, noteId);
            note.setTags(new HashSet<>());
            note.setUpdatedAt(Instant.now());
            return noteRepository.save(note);
        }
        
        Note note = getNote(ownerId, noteId);
        if (note.isTrashed()) {
            throw new IllegalArgumentException("Cannot tag trashed note");
        }
        
        Set<Tag> newTags = new HashSet<>();
        for (Long tagId : tagIds) {
            if (tagId == null || tagId <= 0) {
                throw new IllegalArgumentException("Tag ID must be a positive number");
            }
            newTags.add(tagService.getOwnedTagOrThrow(ownerId, tagId));
        }
        note.setTags(newTags);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    private void ensureOwner(Long ownerId, Note note) {
        // Prevents leaking existence
        if (!note.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Note not found");
        }
    }
}
