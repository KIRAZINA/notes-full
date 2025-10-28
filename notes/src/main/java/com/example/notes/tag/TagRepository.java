package com.example.notes.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Tag entity.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByOwnerId(Long ownerId);
    Optional<Tag> findByOwnerIdAndName(Long ownerId, String name);
}
