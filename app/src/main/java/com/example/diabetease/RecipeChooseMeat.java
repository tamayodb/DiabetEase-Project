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

public class RecipeChooseMeat extends AppCompatActivity {

    private static final String TAG = "RecipeChooseMeat";
    private RecyclerView recyclerView;
    private MeatAdapter adapter;
    private List<MeatItem> meatList;
    private ArrayList<String> selectedMeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_choose_meat);

        Log.d(TAG, "Starting RecipeChooseMeat");

        ImageView backButton = findViewById(R.id.back_button_recipe);
        Button confirmButton = findViewById(R.id.confirm_meats_button);
        recyclerView = findViewById(R.id.meats_recycler_view);

        selectedMeats = new ArrayList<>();
        meatList = new ArrayList<>();

        if (getIntent().hasExtra("selected_meats")) {
            selectedMeats = getIntent().getStringArrayListExtra("selected_meats");
        }

        setupRecyclerView();

        loadMeatsFromFirebase();

        // Back button click
        backButton.setOnClickListener(v -> finish());

        // Confirm button click
        confirmButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selected_meats", selectedMeats);
            setResult(RESULT_OK, resultIntent);
            finish();
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