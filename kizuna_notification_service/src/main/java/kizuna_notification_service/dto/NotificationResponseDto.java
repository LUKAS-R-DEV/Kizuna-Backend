package kizuna_notification_service.dto;

import java.time.LocalDateTime;

public record NotificationResponseDto(String id, String title, String message, LocalDateTime timestamp, boolean isRead) {
}
