package com.example.diabetease;

public class FruitItem {
    private String name;
    private String imageUrl;
    private String documentId; // New field
    private boolean isSelected;

    public FruitItem(String name, String imageUrl, String documentId) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.documentId = documentId;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}