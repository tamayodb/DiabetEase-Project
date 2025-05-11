package com.example.diabetease;

public class VegetableItem {
    private String name;
    private String imageUrl; // New field for image URL
    private boolean isSelected;

    public VegetableItem(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}