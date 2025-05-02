package com.example.diabetease;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecipeActivity extends BaseActivity {

    private CardView fruitButton, vegetableButton, meatButton;

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

        // Initialize buttons
        fruitButton = findViewById(R.id.fruit_button);
        vegetableButton = findViewById(R.id.vegetable_button);
        meatButton = findViewById(R.id.meat_button);

        // Set up custom buttons
        setupCustomButton(fruitButton, R.drawable.fruit_icon, R.string.fruits);
        setupCustomButton(vegetableButton, R.drawable.vegetables_icon, R.string.vegetables);
        setupCustomButton(meatButton, R.drawable.meat_icon, R.string.meat_proteins);

        // Button listeners
        fruitButton.setOnClickListener(view -> navigateToIngredientSelection("fruits"));
        vegetableButton.setOnClickListener(view -> navigateToIngredientSelection("vegetables"));
        meatButton.setOnClickListener(view -> navigateToIngredientSelection("meat"));
    }

    private void setupCustomButton(CardView button, int iconResId, int textResId) {
        ImageView icon = button.findViewById(R.id.button_icon);
        TextView text = button.findViewById(R.id.button_text);

        if (icon != null) icon.setImageResource(iconResId);
        if (text != null) text.setText(textResId);
    }

    private void navigateToIngredientSelection(String category) {
        System.out.println("Navigating to " + category + " selection");
    }
}
