package com.example.diabetease;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private final List<Recipes> matchedRecipes = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference recipesRef = db.collection("recipes");
    private List<String> selectedIngredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_result);

        // Back button setup
        ImageView backButton = findViewById(R.id.back_button_recipe_result);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Spinner setup for category filter
        Spinner categorySpinner = findViewById(R.id.category_spinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"All", "Breakfast", "Lunch", "Dinner", "Appetizer"});
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // RecyclerView setup
        recyclerView = findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RecipeAdapter(this, matchedRecipes);
        recyclerView.setAdapter(adapter);

        // Get selected ingredients from Intent
        ArrayList<String> selectedFruits = getIntent().getStringArrayListExtra("selected_fruits");
        ArrayList<String> selectedVegetables = getIntent().getStringArrayListExtra("selected_vegetables");
        ArrayList<String> selectedMeats = getIntent().getStringArrayListExtra("selected_meat");

        if (selectedFruits != null) selectedIngredients.addAll(selectedFruits);
        if (selectedVegetables != null) selectedIngredients.addAll(selectedVegetables);
        if (selectedMeats != null) selectedIngredients.addAll(selectedMeats);

        // Initial fetch with "All" as default
        fetchMatchingRecipes(selectedIngredients, "All");

        // Spinner filtering action
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                fetchMatchingRecipes(selectedIngredients, selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchMatchingRecipes(List<String> selectedIngredients, String selectedCategory) {
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

                // Check if category matches
                boolean categoryMatch = selectedCategory.equals("All") ||
                        recipe.getCategory().equalsIgnoreCase(selectedCategory);

                // Only show if 2 or more ingredients match and category fits
                if (matchCount >= 2 && categoryMatch) {
                    matchedRecipes.add(recipe);
                }
            }

            adapter.notifyDataSetChanged();
        });
    }
}
