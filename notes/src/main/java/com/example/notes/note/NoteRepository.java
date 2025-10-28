package com.example.notes.note;

import com.example.notes.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for Note with combined search and tag filters.
 */
public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findByOwner(User owner, Pageable pageable);
    Page<Note> findByOwnerAndArchived(User owner, boolean archived, Pageable pageable);
    Page<Note> findByOwnerAndTrashed(User owner, boolean trashed, Pageable pageable);
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
    Page<Note> searchTitleOrContent(User owner, String query, Pageable pageable);

    @Query("""
        select n from Note n
        join n.tags t
        where n.owner = :owner and t.id = :tagId
        """)
    Page<Note> findByOwnerAndTagId(User owner, Long tagId, Pageable pageable);

    @Query("""
        select n from Note n
        join n.tags t
        where n.owner = :owner and t.id in :tagIds
        group by n
        having count(distinct t.id) = :tagCount
        """)
    Page<Note> findByOwnerAndAllTagIds(User owner, java.util.List<Long> tagIds, long tagCount, Pageable pageable);
}
