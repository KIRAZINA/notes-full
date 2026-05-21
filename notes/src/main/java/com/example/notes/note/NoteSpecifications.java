package com.example.notes.note;

import com.example.notes.tag.Tag;
import com.example.notes.user.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;

public final class NoteSpecifications {

    private NoteSpecifications() {
    }

    public static Specification<Note> withOwner(User owner) {
        return (root, query, cb) -> cb.equal(root.get("owner"), owner);
    }

    public static Specification<Note> withArchived(Boolean archived) {
        return (root, query, cb) -> archived == null ? null : cb.equal(root.get("archived"), archived);
    }

    public static Specification<Note> withTrashed(Boolean trashed) {
        return (root, query, cb) -> trashed == null ? null : cb.equal(root.get("trashed"), trashed);
    }

    public static Specification<Note> withPinned(Boolean pinned) {
        return (root, query, cb) -> pinned == null ? null : cb.equal(root.get("pinned"), pinned);
    }

    public static Specification<Note> withQuery(String queryText, boolean searchInContent) {
        return (root, query, cb) -> {
            if (queryText == null || queryText.isBlank()) {
                return null;
            }
            String phrase = "%" + queryText.trim().toLowerCase() + "%";
            var titlePredicate = cb.like(cb.lower(root.get("title")), phrase);
            if (searchInContent) {
                var contentPredicate = cb.like(cb.lower(root.get("content")), phrase);
                return cb.or(titlePredicate, contentPredicate);
            }
            return titlePredicate;
        };
    }

    public static Specification<Note> withAllTagIds(List<Long> tagIds) {
        return (root, query, cb) -> {
            if (tagIds == null || tagIds.isEmpty()) {
                return null;
            }
            query.distinct(true);
            var subquery = query.subquery(Long.class);
            var subRoot = subquery.from(Note.class);
            var tagsJoin = subRoot.join("tags", JoinType.INNER);
            subquery.select(subRoot.get("id"));
            subquery.where(cb.equal(subRoot, root), tagsJoin.get("id").in(tagIds));
            subquery.groupBy(subRoot.get("id"));
            subquery.having(cb.equal(cb.countDistinct(tagsJoin.get("id")), tagIds.size()));
            return cb.exists(subquery);
        };
    }
}
