package kizuna_notification_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record GenericEventDto(
    String entity,
    String entityId,
    String userId,
    String username,
    LocalDateTime timestamp,
    Map<String, Object> data
) {}
