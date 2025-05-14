package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeChooseVegetable extends AppCompatActivity {

    private static final String TAG = "RecipeChooseVegetable";
    private RecyclerView recyclerView;
    private VegetableAdapter adapter;
    private List<VegetableItem> vegetableList;
    private ArrayList<String> selectedVegetables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_vegetable);

        Log.d(TAG, "Starting RecipeChooseVegetable");

        ImageView backButton = findViewById(R.id.back_button_recipe);
        Button confirmButton = findViewById(R.id.confirm_vegetables_button);
        recyclerView = findViewById(R.id.vegetables_recycler_view);

        selectedVegetables = new ArrayList<>();
        vegetableList = new ArrayList<>();

        if (getIntent().hasExtra("selected_vegetables")) {
            selectedVegetables = getIntent().getStringArrayListExtra("selected_vegetables");
        }

        setupRecyclerView();
        loadVegetablesFromFirebase();

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Confirm button
        confirmButton.setOnClickListener(v -> {
            if (selectedVegetables.isEmpty()) {
                Toast.makeText(this, "Please select at least one vegetable", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new VegetableAdapter(vegetableList, this, selectedVegetables);
        recyclerView.setAdapter(adapter);
    }

    private void loadVegetablesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ingredients")
                .whereEqualTo("category", "Vegetable")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    vegetableList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String imageUrl = document.getString("ingred_image_url");
                        String docId = document.getId(); // 🔥 Get document ID here

                        if (name != null && !name.isEmpty() && imageUrl != null && !imageUrl.isEmpty()) {
                            vegetableList.add(new VegetableItem(name, imageUrl, docId)); // Pass ID
                        }
                    }
                    Log.d(TAG, "Loaded " + vegetableList.size() + " vegetables from Firebase");
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load vegetables", e);
                    Toast.makeText(this, "Failed to load vegetables", Toast.LENGTH_SHORT).show();
                });
    }
}