package com.example.notes.tag;

import com.example.notes.common.ConflictException;
import com.example.notes.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles tag CRUD bound to a user (owner).
 */
@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag createTag(Long ownerId, String name) {
        // Enforce uniqueness per user
        if (tagRepository.findByOwnerIdAndName(ownerId, name).isPresent()) {
            throw new ConflictException("Tag already exists");
        }
        Tag tag = Tag.builder()
                .ownerId(ownerId)
                .name(name)
                .build();
        return tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    public List<Tag> listTags(Long ownerId) {
        return tagRepository.findByOwnerId(ownerId);
    }

    public void deleteTag(Long ownerId, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag not found"));
        if (!tag.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Tag not found");
        }
        tagRepository.delete(tag);
    }

    public Tag getOwnedTagOrThrow(Long ownerId, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag not found"));
        // Verify ownership: return same error message to avoid information leakage
        if (!tag.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Tag not found");
        }
        return tag;
    }
}
