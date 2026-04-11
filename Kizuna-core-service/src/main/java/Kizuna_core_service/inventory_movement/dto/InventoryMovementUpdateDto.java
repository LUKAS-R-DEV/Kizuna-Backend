package Kizuna_core_service.inventory_movement.dto;

import jakarta.validation.constraints.NotBlank;

public record InventoryMovementUpdateDto(
        @NotBlank(message = "Reason cannot be blank")
        String reason) {

}
