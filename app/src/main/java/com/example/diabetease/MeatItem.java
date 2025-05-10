package com.example.diabetease;

public class MeatItem {
    private String name;
    private int iconResource;
    private boolean isSelected;

    public MeatItem(String name, int iconResource) {
        this.name = name;
        this.iconResource = iconResource;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public int getIconResource() {
        return iconResource;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}