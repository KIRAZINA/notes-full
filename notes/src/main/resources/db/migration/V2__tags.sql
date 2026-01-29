-- src/main/resources/db/migration/V2__tags.sql
-- This migration file is DEPRECATED since tags are now created in V1__init.sql
-- with proper UNIQUE constraint (owner_id, name) and CASCADE delete.
-- Keeping this file for reference but all operations moved to V1.

-- NOTE: If you're upgrading from an old version where tags weren't in V1:
-- Uncomment the lines below to add missing constraints:

-- Add missing UNIQUE constraint if it doesn't exist (idempotent approach):
-- ALTER TABLE tags 
-- ADD CONSTRAINT uq_tag_owner_name UNIQUE (owner_id, name) 
-- WHERE NOT EXISTS (...);

-- Add CASCADE delete if missing:
-- ALTER TABLE note_tags DROP CONSTRAINT fk_note_tags_note;
-- ALTER TABLE note_tags ADD CONSTRAINT fk_note_tags_note 
--   FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE;
-- ALTER TABLE note_tags DROP CONSTRAINT fk_note_tags_tag;
-- ALTER TABLE note_tags ADD CONSTRAINT fk_note_tags_tag 
--   FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE;
