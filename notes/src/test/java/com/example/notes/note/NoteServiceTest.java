package com.example.notes.note;

import com.example.notes.audit.AuditService;
import com.example.notes.common.NotFoundException;
import com.example.notes.tag.Tag;
import com.example.notes.tag.TagService;
import com.example.notes.user.User;
import com.example.notes.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NoteServiceTest {

    private final NoteRepository noteRepository = mock(NoteRepository.class);
    private final UserService userService = mock(UserService.class);
    private final TagService tagService = mock(TagService.class);
    private final AuditService auditService = mock(AuditService.class);

    private final NoteService noteService =
            new NoteService(noteRepository, userService, tagService, auditService);

    @Test
    void createNote_shouldSaveAndReturnNote() {
        User user = User.builder().id(1L).build();
        when(userService.getByIdOrThrow(1L)).thenReturn(user);

        Note note = Note.builder().id(100L).title("Test").content("Content").owner(user).build();
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        Note saved = noteService.createNote(1L, "Test", "Content");

        assertThat(saved.getId()).isEqualTo(100L);
        verify(auditService).record(eq(1L), eq("CREATE_NOTE"), eq("NOTE"), eq(100L), anyString());
    }

    @Test
    void getNote_shouldThrowIfNotFound() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.getNote(1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addTag_shouldAddTagAndSave() {
        User user = User.builder().id(1L).build();
        Note note = Note.builder().id(10L).owner(user).tags(Set.of()).build();

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));
        when(tagService.getOwnedTagOrThrow(1L, 5L)).thenReturn(Tag.builder().id(5L).ownerId(1L).name("tag").build());
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        Note updated = noteService.addTag(1L, 10L, 5L);

        assertThat(updated.getTags()).extracting("id").contains(5L);
    }
}
