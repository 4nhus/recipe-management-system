package recipes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String user;
    @NotBlank
    private String name;
    @NotBlank
    private String category;
    private LocalDateTime date;
    @NotBlank
    private String description;
    @ElementCollection
    @NotNull
    @Size(min = 1)
    private List<String> ingredients;
    @ElementCollection
    @NotNull
    @Size(min = 1)
    private List<String> directions;
}
