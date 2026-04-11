package Kizuna_core_service.recipe.controller;

import Kizuna_core_service.recipe.dto.RecipeRequestDto;
import Kizuna_core_service.recipe.dto.RecipeResponseDto;
import Kizuna_core_service.recipe.dto.RecipeUpdateDto;
import Kizuna_core_service.recipe.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public Set<RecipeResponseDto> findAll() {
        return recipeService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponseDto> findById(@PathVariable Long id) {
        RecipeResponseDto recipeResponseDto = recipeService.findById(id);
        return ResponseEntity.ok(recipeResponseDto);
    }
    @PostMapping
    public ResponseEntity<RecipeResponseDto> create(@Valid @RequestBody RecipeRequestDto recipeRequestDto) {
        RecipeResponseDto recipeResponseDto = recipeService.create(recipeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeResponseDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponseDto> update(@PathVariable Long id, @Valid @RequestBody RecipeUpdateDto recipeUpdateDto) {
        RecipeResponseDto recipeResponseDto = recipeService.update(id, recipeUpdateDto);
        return ResponseEntity.ok(recipeResponseDto);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<RecipeResponseDto> disable(@PathVariable Long id) {
        RecipeResponseDto recipeResponseDto = recipeService.disable(id);
        return ResponseEntity.ok(recipeResponseDto);
    }

}
