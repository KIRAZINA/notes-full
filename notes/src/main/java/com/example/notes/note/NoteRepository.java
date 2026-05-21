package com.example.notes.note;

import com.example.notes.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository for Note with combined search, tag filters, and entity graph support.
 */
public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {

    @EntityGraph(value = "Note.withTags", type = EntityGraph.EntityGraphType.LOAD)
    Page<Note> findByOwner(User owner, Pageable pageable);

    @EntityGraph(value = "Note.withTags", type = EntityGraph.EntityGraphType.LOAD)
    Page<Note> findByOwnerAndArchived(User owner, boolean archived, Pageable pageable);

    @EntityGraph(value = "Note.withTags", type = EntityGraph.EntityGraphType.LOAD)
    Page<Note> findByOwnerAndTrashed(User owner, boolean trashed, Pageable pageable);

    @EntityGraph(value = "Note.withTags", type = EntityGraph.EntityGraphType.LOAD)
    Page<Note> findByOwnerAndPinned(User owner, boolean pinned, Pageable pageable);

    Page<Note> findByOwnerAndTitleContainingIgnoreCase(User owner, String title, Pageable pageable);
    Page<Note> findByOwnerAndContentContainingIgnoreCase(User owner, String content, Pageable pageable);

    @Query("""
        select n from Note n
        where n.owner = :owner and (
            lower(n.title) like lower(concat('%', :query, '%')) or
            lower(n.content) like lower(concat('%', :query, '%'))
        )
        """)
    @EntityGraph(value = "Note.withTags", type = EntityGraph.EntityGraphType.LOAD)
    Page<Note> searchTitleOrContent(User owner, String query, Pageable pageable);

    @Query("""
        select n from Note n
        join n.tags t
        where n.owner = :owner and t.id = :tagId
        """)
    @EntityGraph(value = "Note.withTags", type = EntityGraph.EntityGraphType.LOAD)
    Page<Note> findByOwnerAndTagId(User owner, Long tagId, Pageable pageable);

    @Query("""
        select n from Note n
        join n.tags t
        where n.owner = :owner and t.id in :tagIds
        group by n
        having count(distinct t.id) = :tagCount
        """)
    @EntityGraph(value = "Note.withTags", type = EntityGraph.EntityGraphType.LOAD)
    Page<Note> findByOwnerAndAllTagIds(User owner, java.util.List<Long> tagIds, long tagCount, Pageable pageable);
}
