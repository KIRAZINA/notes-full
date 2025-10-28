-- src/main/resources/db/migration/V4__audit_log.sql

CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(64) NOT NULL,
    entity_type VARCHAR(32) NOT NULL,
    entity_id BIGINT,
    description VARCHAR(512),
    timestamp TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_log (user_id);
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_log (entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_time ON audit_log (timestamp);
