package com.example.diabetease;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecipeResultActivity extends BaseActivity {

    private TextView ingredientsList;
    private TextView recipeTitle;
    private TextView recipeDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup bottom navigation bar
        setupNavigationBar();

        // Initialize UI components
        initializeUI();

        // Get selected ingredients from intent
        ArrayList<String> selectedFruits = getIntent().getStringArrayListExtra("selected_fruits");
        ArrayList<String> selectedVegetables = getIntent().getStringArrayListExtra("selected_vegetables");
        ArrayList<String> selectedMeats = getIntent().getStringArrayListExtra("selected_meat");

        // Display ingredients
        displaySelectedIngredients(selectedFruits, selectedVegetables, selectedMeats);

        // Generate and display recipe
        generateRecipe(selectedFruits, selectedVegetables, selectedMeats);

        // Set up back button
        ImageView backButton = findViewById(R.id.back_button_recipe_result);
        backButton.setOnClickListener(view -> finish());
    }

    private void initializeUI() {
        ingredientsList = findViewById(R.id.ingredients_list);
        recipeTitle = findViewById(R.id.recipe_title);
        recipeDescription = findViewById(R.id.recipe_description);
    }

    private void displaySelectedIngredients(ArrayList<String> fruits, ArrayList<String> vegetables, ArrayList<String> meats) {
        StringBuilder sb = new StringBuilder();

        // Fruits
        if (fruits != null && !fruits.isEmpty()) {
            sb.append("Fruits:\n");
            for (String fruit : fruits) {
                sb.append("• ").append(fruit).append("\n");
            }
            sb.append("\n");
        }

        // Vegetables
        if (vegetables != null && !vegetables.isEmpty()) {
            sb.append("Vegetables:\n");
            for (String vegetable : vegetables) {
                sb.append("• ").append(vegetable).append("\n");
            }
            sb.append("\n");
        }

        // Meats
        if (meats != null && !meats.isEmpty()) {
            sb.append("Proteins:\n");
            for (String meat : meats) {
                sb.append("• ").append(meat).append("\n");
            }
        }

        ingredientsList.setText(sb.toString());
    }

    private void processCategory(StringBuilder sb, String categoryTitle, ArrayList<String> ids, CollectionReference ref, String extraKey) {
        if (ids == null || ids.isEmpty()) return;

        sb.append(categoryTitle).append(":\n");

        for (String id : ids) {
            ref.document(id).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    sb.append("• ").append(id).append(" (").append(name).append(")\n");

                    // Update UI after all data is fetched
                    ingredientsList.post(() -> ingredientsList.setText(sb.toString()));
                }
            });
        }

        sb.append("\n");
    }

    private void generateRecipe(ArrayList<String> fruits, ArrayList<String> vegetables, ArrayList<String> meats) {
        // This is a simple placeholder recipe generator
        // In a real app, you would use a more sophisticated algorithm or API

        // Create recipe title based on main ingredients
        StringBuilder titleBuilder = new StringBuilder();

        // Add main protein if available
        if (meats != null && !meats.isEmpty()) {
            titleBuilder.append(meats.get(0));
            if (vegetables != null && !vegetables.isEmpty()) {
                titleBuilder.append(" with ");
                titleBuilder.append(vegetables.get(0));
            }
        } else if (vegetables != null && !vegetables.isEmpty()) {
            titleBuilder.append(vegetables.get(0));
            if (vegetables.size() > 1) {
                titleBuilder.append(" and ");
                titleBuilder.append(vegetables.get(1));
            }
        }

        // Add "Salad" if it's mostly vegetables and fruits
        if ((meats == null || meats.isEmpty()) &&
                ((vegetables != null && !vegetables.isEmpty()) ||
                        (fruits != null && !fruits.isEmpty()))) {
            titleBuilder.append(" Salad");
        }
        // Add "Stir Fry" if it has meat and vegetables
        else if ((meats != null && !meats.isEmpty()) &&
                (vegetables != null && !vegetables.isEmpty())) {
            titleBuilder.append(" Stir Fry");
        }

        // Set the recipe title
        String title = titleBuilder.toString().trim();
        title = title.substring(0, 1).toUpperCase() + title.substring(1); // Capitalize first letter
        recipeTitle.setText(title);

        // Create a simple recipe description
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("Diabetes-Friendly Recipe\n\n");
        descriptionBuilder.append("Instructions:\n\n");

        // Add preparation steps
        if (vegetables != null && !vegetables.isEmpty()) {
            descriptionBuilder.append("1. Wash and chop all vegetables.\n");
        }
        if (fruits != null && !fruits.isEmpty()) {
            descriptionBuilder.append("2. Prepare fruits by washing and cutting into bite-sized pieces.\n");
        }
        if (meats != null && !meats.isEmpty()) {
            descriptionBuilder.append("3. Cook ").append(meats.get(0)).append(" in a non-stick pan until done.\n");
        }

        // Add cooking method based on ingredients
        if ((meats != null && !meats.isEmpty()) && (vegetables != null && !vegetables.isEmpty())) {
            descriptionBuilder.append("4. Sauté vegetables in olive oil until tender.\n");
            descriptionBuilder.append("5. Combine with cooked protein and season to taste.\n");
        } else if (vegetables != null && !vegetables.isEmpty()) {
            descriptionBuilder.append("4. Toss vegetables together and dress with olive oil and lemon juice.\n");
        }

        // Add nutritional note
        descriptionBuilder.append("\nThis recipe is low in carbohydrates and suitable for people managing diabetes. Adjust portion sizes according to your dietary needs.");

        recipeDescription.setText(descriptionBuilder.toString());
    }
}