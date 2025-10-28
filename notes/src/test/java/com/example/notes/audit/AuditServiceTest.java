package com.example.notes.audit;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuditServiceTest {

    private final AuditLogRepository repository = mock(AuditLogRepository.class);
    private final AuditService auditService = new AuditService(repository);

    @Test
    void record_shouldSaveAuditLog() {
        // when
        auditService.record(1L, "ACTION", "ENTITY", 10L, "Description");

        // then
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(repository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getAction()).isEqualTo("ACTION");
        assertThat(saved.getEntityType()).isEqualTo("ENTITY");
        assertThat(saved.getEntityId()).isEqualTo(10L);
        assertThat(saved.getDescription()).isEqualTo("Description");
        assertThat(saved.getTimestamp()).isNotNull();
    }
}
