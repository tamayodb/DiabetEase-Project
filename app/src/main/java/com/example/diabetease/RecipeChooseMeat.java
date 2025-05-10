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

public class RecipeChooseMeat extends AppCompatActivity {

    private RecyclerView meatsRecyclerView;
    private MeatAdapter meatAdapter;
    private List<MeatItem> meatItems;
    private ArrayList<String> selectedMeats;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_meat);

        ImageView backButton = findViewById(R.id.back_button_recipe_meat);
        meatsRecyclerView = findViewById(R.id.meats_recycler_view);
        confirmButton = findViewById(R.id.confirm_meats_button);

        selectedMeats = new ArrayList<>();

        if (getIntent().hasExtra("selected_meats")) {
            selectedMeats = getIntent().getStringArrayListExtra("selected_meats");
        }

        setupMeatItems();

        meatsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        meatAdapter = new MeatAdapter(meatItems, this, selectedMeats);
        meatsRecyclerView.setAdapter(meatAdapter);

        backButton.setOnClickListener(view -> finish());

        confirmButton.setOnClickListener(view -> {
            if (selectedMeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one protein", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selected_meats", selectedMeats);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupMeatItems() {
        meatItems = new ArrayList<>();
        meatItems.add(new MeatItem("Chicken", R.drawable.ic_carrot));
        meatItems.add(new MeatItem("Beef", R.drawable.ic_carrot));
        meatItems.add(new MeatItem("Fish", R.drawable.ic_carrot));
        meatItems.add(new MeatItem("Tofu", R.drawable.ic_carrot));
        meatItems.add(new MeatItem("Egg", R.drawable.ic_carrot));
        meatItems.add(new MeatItem("Shrimp", R.drawable.ic_carrot));
        // Add more as needed
    }
}