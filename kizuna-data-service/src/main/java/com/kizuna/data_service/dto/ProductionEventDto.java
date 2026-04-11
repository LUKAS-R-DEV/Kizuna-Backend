package com.kizuna.data_service.dto;

import java.time.LocalDateTime;

public record ProductionEventDto(Long id, Long orderId, String recipeName,String type ,LocalDateTime timestamp) {
}
