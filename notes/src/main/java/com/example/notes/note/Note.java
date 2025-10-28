package com.example.notes.note;

import com.example.notes.tag.Tag;
import com.example.notes.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a note created by a user.
 */
@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner of the note
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean pinned;
    private boolean archived;
    private boolean trashed;

    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Tags many-to-many. Using join table for simplicity.
     */
    @ManyToMany
    @JoinTable(
            name = "note_tags",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

}
