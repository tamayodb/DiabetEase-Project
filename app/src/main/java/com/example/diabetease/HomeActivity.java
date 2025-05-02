package com.example.diabetease;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    RecyclerView horizontalRecycler, verticalRecycler;
    BlogAdapter horizontalAdapter, verticalAdapter;
    List<Blog> blogs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupNavigationBar();

        horizontalRecycler = findViewById(R.id.horizontalRecyclerView);
        verticalRecycler = findViewById(R.id.verticalRecyclerView);

        horizontalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        verticalRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Create separate adapters for horizontal and vertical RecyclerViews
        horizontalAdapter = new BlogAdapter(this, blogs, false); // for horizontal
        horizontalRecycler.setAdapter(horizontalAdapter);

        verticalAdapter = new BlogAdapter(this, blogs, true); // for vertical
        verticalRecycler.setAdapter(verticalAdapter);

        // Fetch blogs from Firestore and update both adapters
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
                    // Notify both adapters about the data change
                    horizontalAdapter.notifyDataSetChanged();
                    verticalAdapter.notifyDataSetChanged();
                });
    }
}
