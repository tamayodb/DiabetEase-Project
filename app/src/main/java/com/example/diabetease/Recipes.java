package com.example.diabetease;

import java.util.List;
import java.util.Map;

public class Recipes {
    private String category;
    private int cook_time;
    private String description;
    private String image_url;
    private List<String> ingredients;
    private List<Map<String, Object>> instructions;
    private String name;
    private int servings;

    // Required empty constructor for Firestore
    public Recipes() {}

    // Getters
    public String getCategory() {
        return category;
    }

    public int getCook_time() {
        return cook_time;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<Map<String, Object>> getInstructions() {
        return instructions;
    }

    public String getName() {
        return name;
    }

    public int getServings() {
        return servings;
    }
}
