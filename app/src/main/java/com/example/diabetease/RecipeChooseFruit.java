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
import java.util.List;

public class RecipeChooseFruit extends AppCompatActivity {

    private RecyclerView fruitsRecyclerView;
    private FruitAdapter fruitAdapter;
    private List<FruitItem> fruitItems;
    private ArrayList<String> selectedFruits;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_fruit);

        // Initialize UI components
        ImageView backButton = findViewById(R.id.back_button_recipe_fruit);
        fruitsRecyclerView = findViewById(R.id.fruits_recycler_view);
        confirmButton = findViewById(R.id.confirm_fruits_button);

        // Initialize selected fruits list
        selectedFruits = new ArrayList<>();

        // Get previously selected fruits if returning to this screen
        if (getIntent().hasExtra("selected_fruits")) {
            selectedFruits = getIntent().getStringArrayListExtra("selected_fruits");
        }

        // Set up fruit items
        setupFruitItems();

        // Setup RecyclerView with grid layout (3 columns)
        fruitsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        fruitAdapter = new FruitAdapter(fruitItems, this, selectedFruits);
        fruitsRecyclerView.setAdapter(fruitAdapter);

        // Back button click listener
        backButton.setOnClickListener(view -> finish());

        // Confirm button click listener
        confirmButton.setOnClickListener(view -> {
            if (selectedFruits.isEmpty()) {
                Toast.makeText(this, "Please select at least one fruit", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selected_fruits", selectedFruits);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupFruitItems() {
        fruitItems = new ArrayList<>();
        fruitItems.add(new FruitItem("Apple", R.drawable.ic_carrot));
        fruitItems.add(new FruitItem("Banana", R.drawable.ic_carrot));
        fruitItems.add(new FruitItem("Strawberry", R.drawable.ic_carrot));
        fruitItems.add(new FruitItem("Blueberry", R.drawable.ic_carrot));
        fruitItems.add(new FruitItem("Orange", R.drawable.ic_carrot));
        fruitItems.add(new FruitItem("Grapes", R.drawable.ic_carrot));
        // Add more fruits as needed
    }
}