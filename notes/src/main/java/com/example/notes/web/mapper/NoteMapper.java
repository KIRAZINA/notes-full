package com.example.notes.web.mapper;

import com.example.notes.note.Note;
import com.example.notes.web.dto.NoteResponse;
import com.example.notes.web.dto.TagResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps Note entity to NoteResponse DTO.
 */
@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "tags", expression = "java(mapTags(note))")
    NoteResponse toResponse(Note note);

    // Map Tag entities to TagResponse using inline mapping to avoid circular deps
    default List<TagResponse> mapTags(Note note) {
        if (note.getTags() == null) return java.util.Collections.emptyList();
        return note.getTags().stream()
                .map(t -> new TagResponse(t.getId(), t.getName()))
                .collect(Collectors.toList());
    }
}
