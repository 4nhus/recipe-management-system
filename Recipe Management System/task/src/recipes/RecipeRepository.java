package recipes;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    Collection<Recipe> findRecipesByCategoryIgnoreCaseOrderByDateDesc(String category);

    Collection<Recipe> findRecipesByNameContainsIgnoreCaseOrderByDateDesc(String name);
}
