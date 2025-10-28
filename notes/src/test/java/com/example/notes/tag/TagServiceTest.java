package com.example.notes.tag;

import com.example.notes.common.ConflictException;
import com.example.notes.common.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    private final TagRepository tagRepository = mock(TagRepository.class);
    private final TagService tagService = new TagService(tagRepository);

    @Test
    void createTag_shouldSaveTag() {
        when(tagRepository.findByOwnerIdAndName(1L, "work")).thenReturn(Optional.empty());
        Tag tag = Tag.builder().id(10L).ownerId(1L).name("work").build();
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag saved = tagService.createTag(1L, "work");

        assertThat(saved.getId()).isEqualTo(10L);
        assertThat(saved.getName()).isEqualTo("work");
    }

    @Test
    void createTag_shouldThrowIfExists() {
        when(tagRepository.findByOwnerIdAndName(1L, "work"))
                .thenReturn(Optional.of(Tag.builder().id(10L).ownerId(1L).name("work").build()));

        assertThatThrownBy(() -> tagService.createTag(1L, "work"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void deleteTag_shouldThrowIfNotFound() {
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.deleteTag(1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getOwnedTagOrThrow_shouldReturnTag() {
        Tag tag = Tag.builder().id(5L).ownerId(1L).name("tag").build();
        when(tagRepository.findById(5L)).thenReturn(Optional.of(tag));

        Tag found = tagService.getOwnedTagOrThrow(1L, 5L);

        assertThat(found.getId()).isEqualTo(5L);
    }
}
