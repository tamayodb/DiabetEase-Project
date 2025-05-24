package com.example.diabetease;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Recipes implements Parcelable {
    private String name;
    private String description;
    private String image_url;
    private String category;
    private int calories;
    private int servings;
    private int cook_time;
    private List<String> ingredients;
    private List<String> nutri_info;
    private List<Map<String, Object>> instructions;

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

    protected Recipes(Parcel in) {
        name = in.readString();
        description = in.readString();
        image_url = in.readString();
        category = in.readString();
        calories = in.readInt();
        servings = in.readInt();
        cook_time = in.readInt();
        ingredients = in.createStringArrayList();
        nutri_info = in.createStringArrayList();
        instructions = (List<Map<String, Object>>) in.readSerializable();
    }

    public static final Creator<Recipes> CREATOR = new Creator<Recipes>() {
        @Override
        public Recipes createFromParcel(Parcel in) {
            return new Recipes(in);
        }

        @Override
        public Recipes[] newArray(int size) {
            return new Recipes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(image_url);
        dest.writeString(category);
        dest.writeInt(calories);
        dest.writeInt(servings);
        dest.writeInt(cook_time);
        dest.writeStringList(ingredients);
        dest.writeStringList(nutri_info);
        dest.writeSerializable((Serializable) instructions);
    }
}
