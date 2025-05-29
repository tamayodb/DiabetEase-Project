package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SpecificBlogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_blog);

        // Get data from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");
        String imageUrl = intent.getStringExtra("image");
        String author = intent.getStringExtra("author");
        String createdAt = intent.getStringExtra("createdAt");
        String content = intent.getStringExtra("content");

        // Bind views
        TextView blogTitle = findViewById(R.id.blogTitle);
        TextView blogCategory = findViewById(R.id.blogCategory);
        TextView blogAuthor = findViewById(R.id.blogAuthor);
        TextView blogCreated = findViewById(R.id.blogCreated);
        TextView blogContent = findViewById(R.id.blogContent);
        ImageView blogImage = findViewById(R.id.blogImage);
        ImageButton backButton = findViewById(R.id.back_button);

        // Set data to views
        blogTitle.setText(title);
        blogCategory.setText(category);
        blogAuthor.setText("By " + author);
        blogCreated.setText(createdAt);
        blogContent.setText(content);

        Glide.with(this).load(imageUrl).into(blogImage);

        // Handle back button
        backButton.setOnClickListener(v -> finish());
    }

}