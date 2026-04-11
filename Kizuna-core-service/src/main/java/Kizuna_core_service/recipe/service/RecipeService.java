package Kizuna_core_service.recipe.service;

import Kizuna_core_service.inventory.domain.Inventory;
import Kizuna_core_service.inventory.repository.InventoryRepository;
import Kizuna_core_service.recipe.domain.Recipe;
import Kizuna_core_service.recipe.domain.RecipeItem;
import Kizuna_core_service.recipe.dto.*;
import Kizuna_core_service.recipe.repository.RecipeRepository;
import Kizuna_core_service.shared.exception.BusinessException;
import Kizuna_core_service.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final InventoryRepository inventoryRepository;

    public RecipeService(RecipeRepository recipeRepository,InventoryRepository inventoryRepository) {
        this.recipeRepository = recipeRepository;
        this.inventoryRepository = inventoryRepository;
    }
    public Set<RecipeResponseDto> findAll(){
        return recipeRepository.findByActiveTrue().stream().map(this::recipeResponseDto).collect(Collectors.toSet());
    }
    public RecipeResponseDto findById(Long id){
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new NotFoundException("Recipe not found"));
        return recipeResponseDto(recipe);
    }

    @Transactional
    public RecipeResponseDto create(RecipeRequestDto requestDto){
        Inventory product=inventoryRepository.findById(requestDto.productId()).orElseThrow(() -> new NotFoundException("Product not found"));
        Recipe recipe=new Recipe();
        recipe.setName(requestDto.name());
        recipe.setDescription(requestDto.description());
        recipe.setProduct(product);
        recipe.setEstimatedProductionTime(requestDto.estimatedProductionTime());
        recipe.setActive(true);

        if(requestDto.items().isEmpty()){
            throw new BusinessException("Items cannot be empty");
        }
        for(RecipeItemRequestDto itemDto:requestDto.items()){

            Inventory inventory=inventoryRepository.findById(itemDto.inventoryId()).orElseThrow(() -> new NotFoundException("Inventory not found"));
            RecipeItem recipeItem=new RecipeItem();
            recipeItem.setQuantity(itemDto.quantity());
            recipeItem.setInventory(inventory);

           recipe.addItem(recipeItem);
        }
        recipeRepository.save(recipe);
        return recipeResponseDto(recipe);
    }

    @Transactional
    public RecipeResponseDto update(Long id, RecipeUpdateDto updateDto){
        Inventory product=inventoryRepository.findById(updateDto.productId()).orElseThrow(() -> new NotFoundException("Product not found"));
        Recipe recipe=recipeRepository.findById(id).orElseThrow(() -> new NotFoundException("Recipe not found"));

        recipe.setName(updateDto.name());
        recipe.setDescription(updateDto.description());
        recipe.setProduct(product);
        recipe.setEstimatedProductionTime(updateDto.estimatedProductionTime());

        recipe.getItems().clear();

        for(RecipeItemRequestDto itemDto: updateDto.items()){
            Inventory inventory=inventoryRepository.findById(itemDto.inventoryId()).orElseThrow(() -> new NotFoundException("Inventory not found"));
            RecipeItem recipeItem=new RecipeItem();
            recipeItem.setQuantity(itemDto.quantity());
            recipeItem.setInventory(inventory);
            recipe.addItem(recipeItem);
        }
        recipeRepository.save(recipe);
        return recipeResponseDto(recipe);

    }
@Transactional
    public RecipeResponseDto disable(Long id){
        Recipe recipe=recipeRepository.findById(id).orElseThrow(()->new NotFoundException("Recipe not found"));
        recipe.setActive(false);
        recipeRepository.save(recipe);
        return recipeResponseDto(recipe);
    }

    private final RecipeResponseDto recipeResponseDto(Recipe recipe){
        return new RecipeResponseDto(recipe.getId(),recipe.getProduct().getName(),recipe.getName(),recipe.getDescription(),recipe.getItems().stream().map(this::recipeItemResponseDto).collect(Collectors.toSet()),recipe.getEstimatedProductionTime());
    }
    private final RecipeItemResponseDto recipeItemResponseDto(RecipeItem recipeItem){
        return new RecipeItemResponseDto(recipeItem.getInventory().getName(),recipeItem.getInventory().getId(),recipeItem.getQuantity());
    }

}
