package com.example.diabetease;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private final List<Recipes> matchedRecipes = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference recipesRef = db.collection("recipes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_result);


        recyclerView = findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RecipeAdapter(this, matchedRecipes);
        recyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }


        ArrayList<String> selectedFruits = getIntent().getStringArrayListExtra("selected_fruits");
        ArrayList<String> selectedVegetables = getIntent().getStringArrayListExtra("selected_vegetables");
        ArrayList<String> selectedMeats = getIntent().getStringArrayListExtra("selected_meat");

        List<String> selectedIngredients = new ArrayList<>();
        if (selectedFruits != null) selectedIngredients.addAll(selectedFruits);
        if (selectedVegetables != null) selectedIngredients.addAll(selectedVegetables);
        if (selectedMeats != null) selectedIngredients.addAll(selectedMeats);

        fetchMatchingRecipes(selectedIngredients);

    }

    private void fetchMatchingRecipes(List<String> selectedIngredients) {
        recipesRef.get().addOnSuccessListener(querySnapshot -> {
            matchedRecipes.clear();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                Recipes recipe = doc.toObject(Recipes.class);
                List<String> recipeIngredients = recipe.getIngredients();

                int matchCount = 0;
                for (String ing : selectedIngredients) {
                    if (recipeIngredients.contains(ing)) {
                        matchCount++;
                    }
                }

                // Only show if 3 or more ingredients match
                if (matchCount >= 2) {
                    matchedRecipes.add(recipe);
                }
            }

            adapter.notifyDataSetChanged();
        });
    }
}