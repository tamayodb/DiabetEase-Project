package com.example.diabetease;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

    EditText searchBar;
    ImageView searchIcon;
    TextView title;

    final boolean[] isSearchOpen = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupNavigationBar();

        // Initialize views
        horizontalRecycler = findViewById(R.id.horizontalRecyclerView);
        verticalRecycler = findViewById(R.id.verticalRecyclerView);
        searchBar = findViewById(R.id.searchBar);
        searchIcon = findViewById(R.id.search);
        title = findViewById(R.id.title);

        // Setup recyclers
        horizontalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        verticalRecycler.setLayoutManager(new LinearLayoutManager(this));

        horizontalAdapter = new BlogAdapter(this, blogs, false);
        horizontalRecycler.setAdapter(horizontalAdapter);

        verticalAdapter = new BlogAdapter(this, blogs, true);
        verticalRecycler.setAdapter(verticalAdapter);

        // Fetch blog data
        fetchBlogsFromFirestore();

        // Setup search icon toggle logic
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearchOpen[0]) {
                    // Open search bar
                    title.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.GONE);
                    searchBar.setVisibility(View.VISIBLE);
                    searchBar.setAlpha(0f);
                    searchBar.setTranslationX(50f);
                    searchBar.animate()
                            .alpha(1f)
                            .translationX(0f)
                            .setDuration(300)
                            .start();

                    searchIcon.animate()
                            .alpha(0f)
                            .setDuration(200)
                            .withEndAction(() -> searchIcon.setVisibility(View.GONE))
                            .start();

                    isSearchOpen[0] = true;
                } else {
                    // Close search bar
                    searchBar.animate()
                            .alpha(0f)
                            .translationX(50f)
                            .setDuration(300)
                            .withEndAction(() -> {
                                searchBar.setVisibility(View.GONE);
                                title.setVisibility(View.VISIBLE);
                                searchIcon.setVisibility(View.VISIBLE);
                            })
                            .start();

                    searchIcon.setVisibility(View.VISIBLE);
                    searchIcon.setAlpha(0f);
                    searchIcon.animate()
                            .alpha(1f)
                            .setDuration(200)
                            .start();

                    isSearchOpen[0] = false;
                }
            }
        });
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
                    horizontalAdapter.notifyDataSetChanged();
                    verticalAdapter.notifyDataSetChanged();
                });
    }
}
