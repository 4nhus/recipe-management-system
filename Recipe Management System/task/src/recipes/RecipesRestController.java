package recipes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RestController
public class RecipesRestController {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RecipesRestController(RecipeRepository repository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.recipeRepository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/recipe/new")
    public ResponseEntity<RecipeId> addRecipe(@Valid @RequestBody Recipe recipe, Authentication auth) {
        recipe.setUser(auth.getName());
        recipe.setDate(LocalDateTime.now());
        recipe = recipeRepository.save(recipe);

        return ResponseEntity.ok(new RecipeId(recipe.getId()));
    }

    @GetMapping("/api/recipe/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable long id) {
        if (!recipeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Recipe recipe = recipeRepository.findById(id).get();

        return ResponseEntity.ok(new RecipeResponse(recipe.getName(), recipe.getCategory(), recipe.getDate(), recipe.getDescription(), recipe.getIngredients(), recipe.getDirections()));
    }

    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id, Authentication auth) {
        if (!recipeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Recipe recipe = recipeRepository.findById(id).get();

        if (!recipe.getUser().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        recipeRepository.delete(recipe);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<Void> updateRecipe(@PathVariable long id, @Valid @RequestBody Recipe recipe, Authentication auth) {
        if (!recipeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Recipe updatedRecipe = recipeRepository.findById(id).get();

        if (!updatedRecipe.getUser().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        updatedRecipe.setName(recipe.getName());
        updatedRecipe.setCategory(recipe.getCategory());
        updatedRecipe.setDate(LocalDateTime.now());
        updatedRecipe.setDescription(recipe.getDescription());
        updatedRecipe.setIngredients(recipe.getIngredients());
        updatedRecipe.setDirections(recipe.getDirections());

        recipeRepository.save(updatedRecipe);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/recipe/search")
    public ResponseEntity<List<RecipeResponse>> searchRecipes(@RequestParam(required = false) String category, @RequestParam(required = false) String name) {
        if ((category == null && name == null) || (category != null && name != null)) {
            return ResponseEntity.badRequest().build();
        }

        Collection<Recipe> recipes;

        if (category != null) {
            recipes = recipeRepository.findRecipesByCategoryIgnoreCaseOrderByDateDesc(category);
        } else {
            recipes = recipeRepository.findRecipesByNameContainsIgnoreCaseOrderByDateDesc(name);
        }

        return ResponseEntity.ok(recipes.stream().map(r -> new RecipeResponse(r.getName(), r.getCategory(), r.getDate(), r.getDescription(), r.getIngredients(), r.getDirections())).toList());
    }

    @PostMapping("/api/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegistrationRequest registration) {
        if (Arrays.stream(registration.password.split("")).filter(s -> !s.isBlank()).count() < 8 || userRepository.existsById(registration.email)) {
            return ResponseEntity.badRequest().build();
        }

        userRepository.save(new RecipesUser(registration.email, passwordEncoder.encode(registration.password)));

        return ResponseEntity.ok().build();
    }

    public record RecipeId(long id) {
    }

    public record RecipeResponse(String name, String category, LocalDateTime date, String description,
                                 List<String> ingredients, List<String> directions) {
    }

    public record RegistrationRequest(@NotNull @Email(regexp = "\\w+@\\w+\\.\\w+") String email,
                                      @NotBlank String password) {
    }
}
