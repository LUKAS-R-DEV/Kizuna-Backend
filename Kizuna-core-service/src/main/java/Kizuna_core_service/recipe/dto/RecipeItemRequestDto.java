package Kizuna_core_service.recipe.dto;

import jakarta.validation.constraints.NotNull;

public record RecipeItemRequestDto(
        @NotNull(message = "Quantity cannot be null")
        double quantity,
        @NotNull(message = "Inventory ID cannot be null")
        Long inventoryId) {
}
