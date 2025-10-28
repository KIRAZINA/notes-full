-- src/main/resources/db/migration/V3__search_indexes.sql

-- Indexes to speed up case-insensitive LIKE queries
CREATE INDEX IF NOT EXISTS idx_notes_title_lower ON notes (lower(title));
CREATE INDEX IF NOT EXISTS idx_notes_content_lower ON notes (lower(content));
