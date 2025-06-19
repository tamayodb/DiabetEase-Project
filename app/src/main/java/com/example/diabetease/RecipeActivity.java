package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

    private ImageButton fruitButton, vegetableButton, meatButton, continueButton;

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
        continueButton.setVisibility(View.GONE);

        setupResultLaunchers();
        setupButtonListeners();

        if (getIntent().hasExtra("selected_fruits")) {
            selectedIngredients.put("fruits", getIntent().getStringArrayListExtra("selected_fruits"));
        }
        if (getIntent().hasExtra("selected_vegetables")) {
            selectedIngredients.put("vegetables", getIntent().getStringArrayListExtra("selected_vegetables"));
        }
        if (getIntent().hasExtra("selected_meats")) {
            selectedIngredients.put("meat", getIntent().getStringArrayListExtra("selected_meats"));
        }
        updateContinueButtonVisibility();

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
                            updateContinueButtonVisibility();
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
                            updateContinueButtonVisibility();
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
                            updateContinueButtonVisibility();
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        vegetableButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecipeChooseVegetable.class);
            intent.putStringArrayListExtra("selected_vegetables", selectedIngredients.get("vegetables"));
            vegetableSelectionLauncher.launch(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        meatButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecipeChooseMeat.class);
            intent.putStringArrayListExtra("selected_meats", selectedIngredients.get("meat"));
            meatSelectionLauncher.launch(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void updateContinueButtonVisibility() {
        boolean hasIngredients = false;

        for (ArrayList<String> items : selectedIngredients.values()) {
            if (!items.isEmpty()) {
                hasIngredients = true;
                break;
            }
        }

        if (hasIngredients) {
            continueButton.setVisibility(View.VISIBLE);
        } else {
            continueButton.setVisibility(View.GONE); // or INVISIBLE
        }
    }

}