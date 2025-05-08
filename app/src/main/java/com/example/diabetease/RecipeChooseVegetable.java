package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeChooseVegetable extends AppCompatActivity {

    private RecyclerView vegetablesRecyclerView;
    private VegetableAdapter vegetableAdapter;
    private List<VegetableItem> vegetableItems;
    private ArrayList<String> selectedVegetables;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_vegetable);

        // Initialize UI components
        ImageView backButton = findViewById(R.id.back_button_recipe);
        vegetablesRecyclerView = findViewById(R.id.vegetables_recycler_view);
        confirmButton = findViewById(R.id.confirm_vegetables_button);

        // Initialize the selected vegetables list
        selectedVegetables = new ArrayList<>();

        // Get any previously selected vegetables if coming back to this screen
        if (getIntent().hasExtra("selected_vegetables")) {
            selectedVegetables = getIntent().getStringArrayListExtra("selected_vegetables");
        }

        // Set up the vegetable items
        setupVegetableItems();

        // Set up the RecyclerView with grid layout (3 columns)
        vegetablesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        vegetableAdapter = new VegetableAdapter(vegetableItems, this, selectedVegetables);
        vegetablesRecyclerView.setAdapter(vegetableAdapter);

        // Set click listener for the back button
        backButton.setOnClickListener(view -> {
            // Return to RecipeActivity without saving
            finish();
        });

        // Set click listener for the confirm button
        confirmButton.setOnClickListener(view -> {
            // Check if at least one vegetable is selected
            if (selectedVegetables.isEmpty()) {
                Toast.makeText(this, "Please select at least one vegetable", Toast.LENGTH_SHORT).show();
                return;
            }

            // Return to RecipeActivity with selected vegetables
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupVegetableItems() {
        vegetableItems = new ArrayList<>();

        // Add common vegetables with their icons
        // You'll need to add these drawable resources to your project
        vegetableItems.add(new VegetableItem("Carrot", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Broccoli", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Spinach", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Tomato", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Potato", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Onion", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Bell Pepper", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Cucumber", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Lettuce", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Cabbage", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Eggplant", R.drawable.ic_carrot));
        vegetableItems.add(new VegetableItem("Zucchini", R.drawable.ic_carrot));
        // Add more vegetables as needed
    }
}