package com.example.notes.tag;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a tag owned by a user.
 */
@Entity
@Table(name = "tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner of the tag; tags are private per user
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 64)
    private String name;
}
