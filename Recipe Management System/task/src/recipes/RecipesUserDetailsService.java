package recipes;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RecipesUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    public RecipesUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RecipesUser user = repository.findById(username).orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        return User.withUsername(user.getEmail()).password(user.getPassword()).roles().build();
    }
}
