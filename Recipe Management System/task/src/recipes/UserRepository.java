package recipes;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<RecipesUser, String> {
}
