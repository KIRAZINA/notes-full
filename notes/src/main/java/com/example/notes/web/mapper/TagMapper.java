package com.example.notes.web.mapper;

import com.example.notes.tag.Tag;
import com.example.notes.web.dto.TagResponse;
import org.mapstruct.Mapper;

/**
 * Maps Tag to TagResponse.
 */
@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag tag);
}
