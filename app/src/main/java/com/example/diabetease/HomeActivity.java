package com.example.diabetease;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends BaseActivity {

    RecyclerView horizontalRecycler, verticalRecycler;
    BlogAdapter horizontalAdapter, verticalAdapter;
    List<Blog> blogs = new ArrayList<>();

    ProgressBar progressBar;
    TextView resultsCountTextView, moreBlogs;
    LinearLayout emptyStateLayout;
    TextView emptyStateTextView;
    ConstraintLayout mainContentArea;

    EditText searchBar;
    ImageView searchIcon, emptyStateImage;
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
        progressBar = findViewById(R.id.progressBar);
        resultsCountTextView = findViewById(R.id.resultsCountTextView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        mainContentArea = findViewById(R.id.mainContent);
        moreBlogs = findViewById(R.id.moreBlogs);
        emptyStateImage = findViewById(R.id.emptyStateImage);

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.search_rotate);
        emptyStateImage.startAnimation(rotateAnimation);



        horizontalRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        verticalRecycler.setLayoutManager(new LinearLayoutManager(this));

        horizontalAdapter = new BlogAdapter(this, blogs, false);
        horizontalRecycler.setAdapter(horizontalAdapter);

        verticalAdapter = new BlogAdapter(this, blogs, true);
        verticalRecycler.setAdapter(verticalAdapter);

        fetchBlogsFromFirestore();

        searchIcon.setOnClickListener(v -> openSearchBar());

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                triggerSearch();
                return true;
            }
            return false;
        });

        searchBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable rightDrawable = searchBar.getCompoundDrawables()[2]; // right drawable
                if (rightDrawable != null) {
                    int drawableRightWidth = rightDrawable.getBounds().width();
                    int drawableRightStart = searchBar.getRight() - drawableRightWidth - searchBar.getPaddingEnd();
                    if (event.getRawX() >= drawableRightStart) {
                        triggerSearch();
                        return true;
                    }
                }
            }
            return false;
        });
    }
        private void openSearchBar() {
        title.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        searchBar.setAlpha(0f);
        searchBar.setTranslationX(50f);
        searchBar.animate().alpha(1f).translationX(0f).setDuration(300).start();

        isSearchOpen[0] = true;
        searchBar.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
    }

    private void closeSearchBar() {
        searchBar.animate()
                .alpha(0f)
                .translationX(50f)
                .setDuration(300)
                .withEndAction(() -> {
                    searchBar.setText(""); // clear input
                    searchBar.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    searchIcon.setVisibility(View.VISIBLE);
                    resultsCountTextView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.GONE);
                    emptyStateImage.clearAnimation();
                    mainContentArea.setVisibility(View.VISIBLE);
                    horizontalRecycler.setVisibility(View.VISIBLE);
                    moreBlogs.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                })
                .start();

        isSearchOpen[0] = false;

        fetchBlogsFromFirestore(); // Reload full list
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && isSearchOpen[0]) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null && currentFocus == searchBar) {
                Rect outRect = new Rect();
                searchBar.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    closeSearchBar();
                    return super.dispatchTouchEvent(ev);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }



    private void triggerSearch() {
        String query = searchBar.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Convert query to lowercase for case-insensitive search
        String lowercaseQuery = query.toLowerCase();

        // Create a cached reference to reduce connection overhead on repeated searches
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Set batch size limit for pagination
        int RESULTS_PER_PAGE = 20;

        db.collection("blogs")
                .limit(100) // Prevent performance issues on large collections
                .get()
                .addOnSuccessListener(docs -> {
                    List<Blog> filteredResults = new ArrayList<>();

                    for (DocumentSnapshot doc : docs) {
                        Blog blog = doc.toObject(Blog.class);
                        if (blog != null) {
                            String title = blog.getTitle() != null ? blog.getTitle().toLowerCase() : "";
                            String content = blog.getContent() != null ? blog.getContent().toLowerCase() : "";
                            String category = blog.getCategory() != null ? blog.getCategory().toLowerCase() : "";

                            if (title.contains(lowercaseQuery) || content.contains(lowercaseQuery) || category.contains(lowercaseQuery)) {
                                blog.setId(doc.getId()); // Optional: if you need the document ID
                                filteredResults.add(blog);
                            }
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                    if (filteredResults.isEmpty()) {
                        performPrefixSearch(lowercaseQuery); // fallback
                    } else {
                        updateSearchResults(filteredResults);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
        // Fallback method for prefix matching if exact keyword match fails
    private void performPrefixSearch(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("blogs")
                .orderBy("title_lowercase") // Requires a lowercase title field + index
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Blog> searchResults = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Blog blog = doc.toObject(Blog.class);
                        if (blog != null) {
                            blog.setId(doc.getId());
                            searchResults.add(blog);
                        }
                    }

                    if (searchResults.isEmpty()) {
                        // Show empty state in UI
                        showEmptyState(query);
                    } else {
                        updateSearchResults(searchResults);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Update UI with search results
    private void updateSearchResults(List<Blog> results) {
        blogs.clear();
        blogs.addAll(results);

        verticalAdapter.notifyDataSetChanged(); // only update the vertical list
        horizontalRecycler.setVisibility(View.GONE);

        // Show result count
        String resultText = results.size() + " results found";
        resultsCountTextView.setText(resultText);
        resultsCountTextView.setVisibility(View.VISIBLE);
        moreBlogs.setVisibility(View.GONE);

        // Show main content area in case it was hidden
        mainContentArea.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }


    // Show empty state with suggestion to try another query
    private void showEmptyState(String query) {
        blogs.clear();
        horizontalAdapter.notifyDataSetChanged();
        resultsCountTextView.setVisibility(View.GONE);
        moreBlogs.setVisibility(View.GONE);

        emptyStateLayout.setVisibility(View.VISIBLE);

        emptyStateImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.search_rotate));

        mainContentArea.setVisibility(View.GONE);

        TextView title = findViewById(R.id.emptyStateTitle);
        TextView subtitle = findViewById(R.id.emptyStateSubtitle);

        title.setText("We couldn’t find a match");
        subtitle.setText("Let’s try another term");
    }

    private void fetchBlogsFromFirestore() {
        FirebaseFirestore.getInstance().collection("blogs")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    blogs.clear();
                    List<Blog> shuffledList = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Blog blog = doc.toObject(Blog.class);
                        if (blog != null) {
                            blogs.add(blog); // ordered by date for vertical
                            shuffledList.add(blog); // same data, will shuffle later
                        }
                    }

                    // Shuffle horizontal list
                    Collections.shuffle(shuffledList);

                    horizontalAdapter.setBlogs(shuffledList);
                    verticalAdapter.setBlogs(blogs);

                    horizontalAdapter.notifyDataSetChanged();
                    verticalAdapter.notifyDataSetChanged();

                    resultsCountTextView.setVisibility(View.GONE);
                });
    }
}
