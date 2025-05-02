package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class RecipeChooseMeat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_meat);

        // Initialize the back button ImageView
        ImageView backButton = findViewById(R.id.back_button_recipe);

        // Set click listener for the back button
        backButton.setOnClickListener(view -> {
            // Option 1: Simply finish to go back to previous screen
            finish();

            // Option 2: Explicitly navigate to RecipeActivity
            // Uncomment the lines below if you prefer navigation instead
            /*
            Intent intent = new Intent(RecipeChooseVegetable.this, RecipeActivity.class);
            startActivity(intent);
            */
        });
    }
}
