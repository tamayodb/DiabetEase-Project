package com.example.diabetease;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SpecificRecipeActivity extends AppCompatActivity {
    private ImageView recipeImage;
    private TextView recipeName, recipeDescription, recipeCookTime;
    private TextView carbsValue, proteinValue, sweetenerValue, fatsValue;
    private TextView ingredientsCount, recipeInstructions;
    private Button ingredientsTab, instructionTab;
    private LinearLayout ingredientsSection;
    private RecyclerView ingredientsRecycler;
    private ImageButton backButton;

    private FirebaseFirestore db;
    private List<Ingredients> ingredientsList = new ArrayList<>();
    private IngredientsAdapter ingredientsAdapter;
    private RecyclerView instructionRecycler;
    private InstructionAdapter instructionAdapter;
    private List<Instruction> instructionList;
    private TextView instructionCount;
    private LinearLayout instructionsSection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_specific_recipe);

        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupTabNavigation();

        Recipes recipe = getIntent().getParcelableExtra("recipe");

        if (recipe != null) {
            populateRecipeData(recipe);
        }

        View scrollToTopBtn = findViewById(R.id.scroll_to_top_button);
        NestedScrollView scrollView = findViewById(R.id.main_scroll);

        scrollToTopBtn.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));
        loadInstructionsFromRecipe(recipe);

    }

    private void initializeViews() {
        recipeImage = findViewById(R.id.recipe_image);
        recipeName = findViewById(R.id.recipe_name);
        recipeDescription = findViewById(R.id.recipe_description);
        recipeCookTime = findViewById(R.id.recipe_cook_time);

        carbsValue = findViewById(R.id.carbs_value);
        proteinValue = findViewById(R.id.protein_value);
        sweetenerValue = findViewById(R.id.sweetener_value);
        fatsValue = findViewById(R.id.fats_value);

        ingredientsCount = findViewById(R.id.ingredients_count);

        ingredientsTab = findViewById(R.id.ingredients_tab);
        instructionTab = findViewById(R.id.instruction_tab);

        ingredientsSection = findViewById(R.id.ingredients_section);
        instructionsSection = findViewById(R.id.instructions_section);

        ingredientsRecycler = findViewById(R.id.ingredients_recycler);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        instructionRecycler = findViewById(R.id.instruction_recycler);

        instructionList = new ArrayList<>();
        instructionAdapter = new InstructionAdapter(instructionList);
        instructionRecycler.setLayoutManager(new LinearLayoutManager(this));
        instructionRecycler.setAdapter(instructionAdapter);
    }


    private void setupTabNavigation() {
        ingredientsTab.setOnClickListener(v -> showIngredientsTab());
        instructionTab.setOnClickListener(v -> showInstructionsTab());

        // Set initial state to ingredients tab
        showIngredientsTab();
    }

    private void showIngredientsTab() {
        // Update tab appearance
        ingredientsTab.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_tab_background));

        instructionTab.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));

        // Show/hide sections
        ingredientsSection.setVisibility(View.VISIBLE);
        instructionsSection.setVisibility(View.GONE);
    }

    private void showInstructionsTab() {
        // Update tab appearance
        instructionTab.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_tab_background));

        ingredientsTab.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));

        // Show/hide sections
        ingredientsSection.setVisibility(View.GONE);
        instructionsSection.setVisibility(View.VISIBLE);
    }

    private void populateRecipeData(Recipes recipe) {
        // Load image with error handling
        Glide.with(this)
                .load(recipe.getImage_url())
                .placeholder(R.drawable.placeholder_recipe)
                .error(R.drawable.placeholder_recipe)
                .into(recipeImage);

        // Basic info
        recipeName.setText(recipe.getName() != null ? recipe.getName() : "Unnamed Recipe");
        recipeDescription.setText(recipe.getDescription() != null ? recipe.getDescription() : "No description available");
        String cookTime = String.valueOf(recipe.getCook_time());
        if (cookTime == null || cookTime.isEmpty()) {
            cookTime = "0";
        }
        recipeCookTime.setText(String.format("%s Min", cookTime));

        // Nutrition info
        populateNutritionInfo(recipe);

        loadInstructionsFromRecipe(recipe);

        // Setup ingredients
        setupIngredientsRecycler(recipe);
    }

    private void populateNutritionInfo(Recipes recipe) {
        // Array of TextViews to populate
        TextView[] nutritionTextViews = {carbsValue, proteinValue, fatsValue, sweetenerValue};

        // Default values
        for (TextView tv : nutritionTextViews) {
            tv.setText("0g");
        }

        if (recipe.getNutri_info() != null && !recipe.getNutri_info().isEmpty()) {
            List<String> nutriInfo = recipe.getNutri_info();

            // Display up to 4 nutrition values, whatever they are
            for (int i = 0; i < Math.min(nutriInfo.size(), nutritionTextViews.length); i++) {
                String nutrient = nutriInfo.get(i);
                if (nutrient != null && !nutrient.trim().isEmpty()) {
                    nutritionTextViews[i].setText(nutrient);
                }
            }
        }
    }

    private void setupIngredientsRecycler(Recipes recipe) {
        // Initialize RecyclerView first
        ingredientsAdapter = new IngredientsAdapter(ingredientsList);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRecycler.setAdapter(ingredientsAdapter);
        ingredientsRecycler.setNestedScrollingEnabled(false);

        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            int count = recipe.getIngredients().size();
            ingredientsCount.setText(count + (count == 1 ? " Item" : " Items"));

            // Add logging to debug
            Log.d("SpecificRecipe", "Total ingredients to fetch: " + count);
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                Log.d("SpecificRecipe", "Ingredient ID " + i + ": " + recipe.getIngredients().get(i));
            }

            // Fetch ingredient details
            fetchIngredientDetails(recipe.getIngredients());
        } else {
            ingredientsCount.setText("0 Items");
        }
    }

    private void fetchIngredientDetails(List<String> ingredientIds) {
        ingredientsList.clear();
        ingredientsAdapter.notifyDataSetChanged();

        if (ingredientIds.isEmpty()) {
            return;
        }

        // Filter out null/empty ingredient IDs first
        List<String> validIngredientIds = new ArrayList<>();
        for (String id : ingredientIds) {
            if (id != null && !id.trim().isEmpty()) {
                validIngredientIds.add(id.trim());
            }
        }

        if (validIngredientIds.isEmpty()) {
            Log.w("SpecificRecipe", "No valid ingredient IDs found");
            return;
        }

        Log.d("SpecificRecipe", "Fetching " + validIngredientIds.size() + " valid ingredients");

        AtomicInteger loadedCount = new AtomicInteger(0);
        final int totalToLoad = validIngredientIds.size();

        for (String ingredientId : validIngredientIds) {
            Log.d("SpecificRecipe", "Fetching ingredient: " + ingredientId);

            db.collection("ingredients").document(ingredientId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        int currentCount = loadedCount.incrementAndGet();
                        Log.d("SpecificRecipe", "Loaded " + currentCount + "/" + totalToLoad + " ingredients");

                        if (documentSnapshot.exists()) {
                            try {
                                Ingredients ingredient = documentSnapshot.toObject(Ingredients.class);
                                if (ingredient != null) {
                                    Log.d("SpecificRecipe", "Successfully parsed ingredient: " + ingredient.getName());

                                    // Add to list and notify adapter immediately
                                    ingredientsList.add(ingredient);

                                    // Run on UI thread to update adapter
                                    runOnUiThread(() -> {
                                        ingredientsAdapter.notifyItemInserted(ingredientsList.size() - 1);
                                    });
                                } else {
                                    Log.w("SpecificRecipe", "Failed to parse ingredient: " + ingredientId);
                                }
                            } catch (Exception e) {
                                Log.e("SpecificRecipe", "Error parsing ingredient: " + ingredientId, e);
                            }
                        } else {
                            Log.w("SpecificRecipe", "Ingredient document not found: " + ingredientId);
                        }

                        // Final update when all are loaded
                        if (currentCount == totalToLoad) {
                            Log.d("SpecificRecipe", "All ingredients loaded. Final list size: " + ingredientsList.size());
                            runOnUiThread(() -> {
                                ingredientsAdapter.notifyDataSetChanged();
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        int currentCount = loadedCount.incrementAndGet();
                        Log.e("SpecificRecipe", "Error fetching ingredient: " + ingredientId, e);

                        // Still update count to prevent hanging
                        if (currentCount == totalToLoad) {
                            Log.d("SpecificRecipe", "All ingredients processed (with errors). Final list size: " + ingredientsList.size());
                            runOnUiThread(() -> {
                                ingredientsAdapter.notifyDataSetChanged();
                            });
                        }
                    });
        }
    }

    private void loadInstructionsFromRecipe(Recipes recipe) {
        instructionList.clear();

        List<Map<String, Object>> rawInstructions = recipe.getInstructions();
        if (rawInstructions != null) {
            for (Map<String, Object> map : rawInstructions) {
                // Safely cast Firestore values
                Long stepNumberLong = (Long) map.get("number");
                String stepText = (String) map.get("text");

                if (stepNumberLong != null && stepText != null) {
                    int stepNumber = stepNumberLong.intValue();
                    instructionList.add(new Instruction(stepNumber, stepText));
                }
            }
        }
    }


}