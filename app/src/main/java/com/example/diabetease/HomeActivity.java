package com.example.diabetease;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

        RecyclerView horizontalRecycler, verticalRecycler;
        BlogAdapter adapter;
        List<Blog> blogs = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            setupNavigationBar();

            horizontalRecycler = findViewById(R.id.horizontalRecyclerView);
            verticalRecycler = findViewById(R.id.verticalRecyclerView);

            // Layout managers
            horizontalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            verticalRecycler.setLayoutManager(new LinearLayoutManager(this));

            adapter = new BlogAdapter(this, blogs);
            horizontalRecycler.setAdapter(adapter);
            verticalRecycler.setAdapter(adapter);

            fetchBlogsFromFirestore();
        }

        private void fetchBlogsFromFirestore() {
            FirebaseFirestore.getInstance().collection("blogs")
                    .orderBy("created_at", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        blogs.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Blog blog = doc.toObject(Blog.class);
                            blogs.add(blog);
                        }
                        adapter.notifyDataSetChanged();
                    });
        }
    }

