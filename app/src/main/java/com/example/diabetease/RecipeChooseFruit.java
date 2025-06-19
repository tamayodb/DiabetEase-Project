package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class RecipeChooseFruit extends AppCompatActivity {

    private static final String TAG = "RecipeChooseFruit";
    private RecyclerView recyclerView;
    private Button confirmButton;
    private FruitAdapter adapter;
    private List<FruitItem> fruitList;
    private ArrayList<String> selectedVegetables;
    private ArrayList<String> selectedFruits;
    private ArrayList<String> selectedMeats;


    private ActivityResultLauncher<Intent> vegetableSelectionLauncher;
    private ActivityResultLauncher<Intent> meatSelectionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_fruit);

        Log.d(TAG, "Starting RecipeChooseFruit");

        ImageView backButton = findViewById(R.id.back_button_recipe);
        ImageButton confirmButton = findViewById(R.id.confirm_fruits_button);
        ImageButton nextVegetableButton = findViewById(R.id.next_vegetable);
        ImageButton nextMeatButton = findViewById(R.id.next_meat);

        recyclerView = findViewById(R.id.fruits_recycler_view);

        selectedFruits = new ArrayList<>();
        fruitList = new ArrayList<>();


        if (getIntent().hasExtra("selected_fruits")) {
            selectedFruits = getIntent().getStringArrayListExtra("selected_fruits");
        }
        if (getIntent().hasExtra("selected_vegetables")) {
            selectedVegetables = getIntent().getStringArrayListExtra("selected_vegetables");
        }
        if (getIntent().hasExtra("selected_meats")) {
            selectedMeats = getIntent().getStringArrayListExtra("selected_meats");
        }


        setupRecyclerView();

        loadFruitsFromFirebase();

        // Back button click
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeChooseFruit.this, RecipeActivity.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Confirm button click
        confirmButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeChooseFruit.this, RecipeActivity.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });



        vegetableSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> selectedVegetables = result.getData().getStringArrayListExtra("selected_vegetables");
                        Log.d(TAG, "Received selected vegetables: " + selectedVegetables);
                    }
                }
        );

        meatSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> selectedMeats = result.getData().getStringArrayListExtra("selected_meats");
                        Log.d(TAG, "Received selected meats: " + selectedMeats);
                    }
                }
        );

        nextVegetableButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipeChooseVegetable.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats); //
            vegetableSelectionLauncher.launch(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        nextMeatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipeChooseMeat.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats); //
            meatSelectionLauncher.launch(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                        String docId = document.getId(); // 🔥 Get document ID

                        if (name != null && !name.isEmpty() && imageUrl != null && !imageUrl.isEmpty()) {
                            fruitList.add(new FruitItem(name, imageUrl, docId)); // Pass ID
                        }
                    }

                    // Restore selection state
                    for (int i = 0; i < fruitList.size(); i++) {
                        if (selectedFruits.contains(fruitList.get(i).getDocumentId())) {
                            fruitList.get(i).setSelected(true);
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