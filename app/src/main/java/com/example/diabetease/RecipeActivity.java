package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeActivity extends BaseActivity {

    private CardView fruitButton, vegetableButton, meatButton;
    private Button continueButton;

    // Store selected ingredients by category
    private Map<String, ArrayList<String>> selectedIngredients;

    // Activity result launchers for each ingredient category
    private ActivityResultLauncher<Intent> fruitSelectionLauncher;
    private ActivityResultLauncher<Intent> vegetableSelectionLauncher;
    private ActivityResultLauncher<Intent> meatSelectionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup bottom navigation bar
        setupNavigationBar();

        // Initialize selected ingredients map
        selectedIngredients = new HashMap<>();
        selectedIngredients.put("fruits", new ArrayList<>());
        selectedIngredients.put("vegetables", new ArrayList<>());
        selectedIngredients.put("meat", new ArrayList<>());

        // Initialize UI components
        fruitButton = findViewById(R.id.fruit_button);
        vegetableButton = findViewById(R.id.vegetable_button);
        meatButton = findViewById(R.id.meat_button);
        continueButton = findViewById(R.id.continue_button);

        // Set up result launchers
        setupResultLaunchers();

        // Set up custom buttons
        setupCustomButton(fruitButton, R.drawable.fruit_icon, R.string.fruits);
        setupCustomButton(vegetableButton, R.drawable.vegetables_icon, R.string.vegetables);
        setupCustomButton(meatButton, R.drawable.meat_icon, R.string.meat_proteins);

        // Set up button click listeners
        setupButtonListeners();
    }

    private void setupResultLaunchers() {
        // Fruit launcher
        fruitSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> selectedFruits = result.getData().getStringArrayListExtra("selected_fruits");
                        if (selectedFruits != null) {
                            selectedIngredients.put("fruits", selectedFruits);
                        }
                    }
                }
        );

        // Vegetable launcher
        vegetableSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> selectedVegetables = result.getData().getStringArrayListExtra("selected_vegetables");
                        if (selectedVegetables != null) {
                            selectedIngredients.put("vegetables", selectedVegetables);
                        }
                    }
                }
        );

        // Meat launcher
        meatSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> selectedMeats = result.getData().getStringArrayListExtra("selected_meats");
                        if (selectedMeats != null) {
                            selectedIngredients.put("meat", selectedMeats);
                        }
                    }
                }
        );
    }

    private void setupButtonListeners() {
        fruitButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecipeChooseFruit.class);
            intent.putStringArrayListExtra("selected_fruits", selectedIngredients.get("fruits"));
            fruitSelectionLauncher.launch(intent);
        });

        vegetableButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecipeChooseVegetable.class);
            intent.putStringArrayListExtra("selected_vegetables", selectedIngredients.get("vegetables"));
            vegetableSelectionLauncher.launch(intent);
        });

        meatButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecipeChooseMeat.class);
            intent.putStringArrayListExtra("selected_meats", selectedIngredients.get("meat"));
            meatSelectionLauncher.launch(intent);
        });

        continueButton.setOnClickListener(view -> {
            boolean hasIngredients = false;
            for (ArrayList<String> items : selectedIngredients.values()) {
                if (!items.isEmpty()) {
                    hasIngredients = true;
                    break;
                }
            }

            if (!hasIngredients) {
                Toast.makeText(this, "Please select at least one ingredient", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, RecipeResultActivity.class);
            for (Map.Entry<String, ArrayList<String>> entry : selectedIngredients.entrySet()) {
                intent.putStringArrayListExtra("selected_" + entry.getKey(), entry.getValue());
            }

            startActivity(intent);
        });
    }

    private void setupCustomButton(CardView button, int iconResId, int textResId) {
        ImageView icon = button.findViewById(R.id.button_icon);
        TextView text = button.findViewById(R.id.button_text);
        if (icon != null) icon.setImageResource(iconResId);
        if (text != null) text.setText(textResId);
    }
}