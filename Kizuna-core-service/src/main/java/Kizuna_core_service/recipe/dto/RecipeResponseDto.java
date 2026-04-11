package Kizuna_core_service.recipe.dto;

import java.util.Set;

public record RecipeResponseDto(Long id, String name,String productName ,String description, Set<RecipeItemResponseDto> items, Long estimatedProductTime) {

}
