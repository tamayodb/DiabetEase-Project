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

public class RecipeChooseMeat extends AppCompatActivity {

    private static final String TAG = "RecipeChooseMeat";
    private RecyclerView recyclerView;
    private MeatAdapter adapter;
    private List<MeatItem> meatList;
    private ArrayList<String> selectedVegetables;
    private ArrayList<String> selectedFruits;
    private ArrayList<String> selectedMeats;


    private Button confirmButton;
    private ActivityResultLauncher<Intent> vegetableSelectionLauncher;
    private ActivityResultLauncher<Intent> fruitSelectionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_meat);

        Log.d(TAG, "Starting RecipeChooseMeat");

        ImageView backButton = findViewById(R.id.back_button_recipe);
        ImageButton confirmButton = findViewById(R.id.confirm_meats_button);
        ImageButton nextVegetableButton = findViewById(R.id.next_vegetable);
        ImageButton nextFruitButton = findViewById(R.id.next_fruit);
        recyclerView = findViewById(R.id.meats_recycler_view);

        selectedMeats = new ArrayList<>();
        meatList = new ArrayList<>();

        selectedFruits = new ArrayList<>();
        selectedVegetables = new ArrayList<>();


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

        loadMeatsFromFirebase();

        // Back button click
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeChooseMeat.this, RecipeActivity.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Confirm button click
        confirmButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeChooseMeat.this, RecipeActivity.class);
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

        fruitSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> selectedFruits = result.getData().getStringArrayListExtra("selected_fruits");
                        Log.d(TAG, "Received selected fruits: " + selectedFruits);
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

        nextFruitButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipeChooseFruit.class);
            intent.putStringArrayListExtra("selected_fruits", selectedFruits);
            intent.putStringArrayListExtra("selected_vegetables", selectedVegetables);
            intent.putStringArrayListExtra("selected_meats", selectedMeats); //
            fruitSelectionLauncher.launch(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new MeatAdapter(meatList, this, selectedMeats);
        recyclerView.setAdapter(adapter);
    }

    private void loadMeatsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ingredients")
                .whereEqualTo("category", "Meat")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    meatList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String imageUrl = document.getString("ingred_image_url");
                        String docId = document.getId(); // 🔥 Document ID

                        if (name != null && !name.isEmpty() && imageUrl != null && !imageUrl.isEmpty()) {
                            meatList.add(new MeatItem(name, imageUrl, docId)); // Pass document ID
                        }
                    }

                    // Restore previous selections
                    for (int i = 0; i < meatList.size(); i++) {
                        if (selectedMeats.contains(meatList.get(i).getDocumentId())) {
                            meatList.get(i).setSelected(true);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load meats", e);
                    Toast.makeText(this, "Failed to load proteins", Toast.LENGTH_SHORT).show();
                });
    }
}