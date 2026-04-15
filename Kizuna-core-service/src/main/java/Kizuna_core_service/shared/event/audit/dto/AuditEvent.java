package Kizuna_core_service.shared.event.audit.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AuditEvent(
        String action,
        String entity,
        String entityId,
        String username,
        LocalDateTime timestamp,
        Serializable data
) implements Serializable {

    public AuditEvent(String action, String entity, String entityId, String username, Serializable data) {
        this(
                action,
                entity,
                entityId,
                username,
                LocalDateTime.now(),
                data
        );
    }
}