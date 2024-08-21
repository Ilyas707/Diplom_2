package data;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private List<String> ingredients;

    public Order setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}