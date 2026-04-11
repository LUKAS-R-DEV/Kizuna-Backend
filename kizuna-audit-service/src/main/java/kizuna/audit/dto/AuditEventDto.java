package kizuna.audit.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
@Builder
public record AuditEventDto(String action, String entity, String entityId, String username, String userId, LocalDateTime timestamp, Map<String, Object> details) {
}
