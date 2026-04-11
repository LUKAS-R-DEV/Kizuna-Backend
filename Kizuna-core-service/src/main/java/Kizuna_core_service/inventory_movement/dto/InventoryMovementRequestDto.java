package Kizuna_core_service.inventory_movement.dto;

import Kizuna_core_service.inventory_movement.domain.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryMovementRequestDto(
        @NotNull(message = "Inventory ID cannot be null")
        Long inventoryId,
        @NotNull(message = "Quantity cannot be null")
        @PositiveOrZero(message = "Quantity must be positive or zero")
        Double quantity,
        @NotBlank(message = "Reason cannot be blank")
        String reason,
        @NotNull(message = "Type cannot be null")
        MovementType type) {
}
