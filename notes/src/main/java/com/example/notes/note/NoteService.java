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

        if (archived != null) return noteRepository.findByOwnerAndArchived(owner, archived, pageable);
        if (trashed != null) return noteRepository.findByOwnerAndTrashed(owner, trashed, pageable);
        if (pinned != null) return noteRepository.findByOwnerAndPinned(owner, pinned, pageable);

        if (tagIds != null && !tagIds.isEmpty()) {
            return tagIds.size() == 1
                    ? noteRepository.findByOwnerAndTagId(owner, tagIds.get(0), pageable)
                    : noteRepository.findByOwnerAndAllTagIds(owner, tagIds, tagIds.size(), pageable);
        }

        if (query != null && !query.isBlank()) {
            // Combined search regardless of inContent flag
            return noteRepository.searchTitleOrContent(owner, query, pageable);
        }

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
        Note note = getNote(ownerId, noteId);
        if (note.isTrashed()) throw new IllegalArgumentException("Cannot tag trashed note");
        Tag tag = tagService.getOwnedTagOrThrow(ownerId, tagId);
        note.getTags().add(tag);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    public Note removeTag(Long ownerId, Long noteId, Long tagId) {
        Note note = getNote(ownerId, noteId);
        Tag tag = tagService.getOwnedTagOrThrow(ownerId, tagId);
        note.getTags().remove(tag);
        note.setUpdatedAt(Instant.now());
        return noteRepository.save(note);
    }

    /**
     * Replaces note tags with the given set (owned by the user).
     */
    public Note setTags(Long ownerId, Long noteId, List<Long> tagIds) {
        Note note = getNote(ownerId, noteId);
        if (note.isTrashed()) throw new IllegalArgumentException("Cannot tag trashed note");
        Set<Tag> newTags = new HashSet<>();
        for (Long tagId : tagIds) {
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
