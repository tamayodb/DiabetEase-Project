package com.example.diabetease;

import java.util.List;
import java.util.Map;

public class Recipes {
    private String category;
    private int cook_time;
    private int calories;
    private String description;
    private String image_url;
    private List<String> ingredients;
    private List<Map<String, Object>> instructions;
    private String name;
    private int servings;
    private List<String> nutri_info;

    // Required empty constructor for Firestore
    public Recipes() {}

    // Getters
    public String getCategory() {
        return category;
    }

    public int getCook_time() {
        return cook_time;
    }

    public int getCalories() {
        return calories;
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

    public List<String> getNutri_info() {
        return nutri_info;
    }

    // Setters
    public void setCategory(String category) {
        this.category = category;
    }

    public void setCook_time(int cook_time) {
        this.cook_time = cook_time;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(List<Map<String, Object>> instructions) {
        this.instructions = instructions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setNutri_info(List<String> nutri_info) {
        this.nutri_info = nutri_info;
    }
}
