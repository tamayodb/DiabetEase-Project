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

public class RecipeChooseVegetable extends AppCompatActivity {

    private static final String TAG = "RecipeChooseVegetable";
    private RecyclerView recyclerView;
    private VegetableAdapter adapter;
    private List<VegetableItem> vegetableList;
    private ArrayList<String> selectedVegetables;
    private ArrayList<String> selectedFruits;
    private ArrayList<String> selectedMeats;
    private Button confirmButton;

    private ActivityResultLauncher<Intent> fruitSelectionLauncher;
    private ActivityResultLauncher<Intent> meatSelectionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_vegetable);

        Log.d(TAG, "Starting RecipeChooseVegetable");

        ImageView backButton = findViewById(R.id.back_button_recipe);
        ImageButton confirmButton = findViewById(R.id.confirm_vegetables_button);
        ImageButton nextFruitButton = findViewById(R.id.next_fruit);
        ImageButton nextMeatButton = findViewById(R.id.next_meat);

        recyclerView = findViewById(R.id.vegetables_recycler_view);

        selectedVegetables = new ArrayList<>();
        vegetableList = new ArrayList<>();

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
        loadVegetablesFromFirebase();


        // Back button click
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeChooseVegetable.this, RecipeActivity.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Confirm button click
        confirmButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeChooseVegetable.this, RecipeActivity.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        meatSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> selectedMeats = result.getData().getStringArrayListExtra("selected_meats");
                        Log.d(TAG, "Received selected meats: " + selectedMeats);
                    }
                }
        );

        fruitSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> selectedFruits = result.getData().getStringArrayListExtra("selected_fruits");
                        Log.d(TAG, "Received selected fruits: " + selectedFruits);
                    }
                }
        );

        nextMeatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipeChooseMeat.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats);
            meatSelectionLauncher.launch(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        nextFruitButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipeChooseFruit.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats);
            fruitSelectionLauncher.launch(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                        String docId = document.getId(); // 🔥 Get document ID

                        if (name != null && !name.isEmpty() && imageUrl != null && !imageUrl.isEmpty()) {
                            VegetableItem item = new VegetableItem(name, imageUrl, docId); // Pass ID
                            vegetableList.add(item);
                        }
                    }

                    // Restore selection state
                    for (int i = 0; i < vegetableList.size(); i++) {
                        if (selectedVegetables.contains(vegetableList.get(i).getDocumentId())) {
                            vegetableList.get(i).setSelected(true);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load vegetables", e);
                    Toast.makeText(this, "Failed to load vegetables", Toast.LENGTH_SHORT).show();
                });
    }
}