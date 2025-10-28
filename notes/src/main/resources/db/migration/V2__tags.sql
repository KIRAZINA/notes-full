-- src/main/resources/db/migration/V2__tags.sql

CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    CONSTRAINT uq_tag_owner_name UNIQUE (owner_id, name)
);

CREATE TABLE IF NOT EXISTS note_tags (
    note_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    CONSTRAINT fk_note_tags_note FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE,
    CONSTRAINT fk_note_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE,
    CONSTRAINT uq_note_tag UNIQUE (note_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_tags_owner ON tags (owner_id);
CREATE INDEX IF NOT EXISTS idx_note_tags_note ON note_tags (note_id);
CREATE INDEX IF NOT EXISTS idx_note_tags_tag ON note_tags (tag_id);
