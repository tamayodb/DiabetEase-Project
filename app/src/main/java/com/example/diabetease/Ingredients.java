package com.example.diabetease;

public class Ingredients {
    private String category;
    private String ingred_image_url;
    private String name;

    // Required empty constructor for Firestore
    public Ingredients() {}

    // Getters
    public String getCategory() {
        return category;
    }

    public String getIngred_image_url() {
        return ingred_image_url;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String ingred_image_url) {
        this.ingred_image_url = ingred_image_url;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
