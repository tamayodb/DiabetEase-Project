package com.example.diabetease;

public class VegetableItem {
    private String name;
    private String imageUrl; // New field for image URL
    private boolean isSelected;

    private String documentId;

    public VegetableItem(String name, String imageUrl, String documentId) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.isSelected = false;
        this.documentId = documentId;
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

    public String getDocumentId() {
        return documentId;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}