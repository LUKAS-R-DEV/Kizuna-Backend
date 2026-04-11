package Kizuna_core_service.inventory_movement.dto;

import Kizuna_core_service.inventory_movement.domain.MovementType;

import java.time.LocalDateTime;

public record InventoryMovementResponseDto(Long id,Long inventoryId ,String inventoryName, Double quantity, String reason, LocalDateTime createdAt, MovementType type, LocalDateTime updatedAt) {
}
