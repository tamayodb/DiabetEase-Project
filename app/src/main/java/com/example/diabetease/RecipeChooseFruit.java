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

public class RecipeChooseFruit extends AppCompatActivity {

    private static final String TAG = "RecipeChooseFruit";
    private RecyclerView recyclerView;
    private FruitAdapter adapter;
    private List<FruitItem> fruitList;
    private ArrayList<String> selectedFruits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_fruit);

        Log.d(TAG, "Starting RecipeChooseFruit");

        ImageView backButton = findViewById(R.id.back_button_recipe_fruit);
        Button confirmButton = findViewById(R.id.confirm_fruits_button);
        recyclerView = findViewById(R.id.fruits_recycler_view);

        selectedFruits = new ArrayList<>();
        fruitList = new ArrayList<>();

        // Get previously selected fruits
        if (getIntent().hasExtra("selected_fruits")) {
            selectedFruits = getIntent().getStringArrayListExtra("selected_fruits");
        }

        setupRecyclerView();

        loadFruitsFromFirebase();

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Confirm button
        confirmButton.setOnClickListener(v -> {
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

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new FruitAdapter(fruitList, this, selectedFruits);
        recyclerView.setAdapter(adapter);
    }

    private void loadFruitsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ingredients")
                .whereEqualTo("category", "Fruit")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fruitList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String imageUrl = document.getString("ingred_image_url");
                        String docId = document.getId(); // 🔥 Document ID

                        if (name != null && !name.isEmpty() && imageUrl != null && !imageUrl.isEmpty()) {
                            fruitList.add(new FruitItem(name, imageUrl, docId)); // Pass document ID
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load fruits", e);
                    Toast.makeText(this, "Failed to load fruits", Toast.LENGTH_SHORT).show();
                });
    }
}