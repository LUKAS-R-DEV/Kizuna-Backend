package Kizuna_core_service.recipe.repository;

import Kizuna_core_service.recipe.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByActiveTrue();

}
