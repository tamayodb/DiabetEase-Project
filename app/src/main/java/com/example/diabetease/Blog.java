package com.example.diabetease;

import java.util.List;

public class Blog {
    private String id;
    private String author;
    private String category;
    private String content;
    private String cover_image_url;
    private String created_at;
    private String excerpt;
    private boolean like_enabled;
    private int likes_count;
    private int reading_time;
    private List<String> tags;
    private String title;

    // Required empty constructor for Firestore
    public Blog() {}

    // Getters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getContent() { return content; }
    public String getCover_image_url() { return cover_image_url; }
    public String getCreated_at() { return created_at; }
    public String getExcerpt() { return excerpt; }
    public boolean isLike_enabled() { return like_enabled; }
    public int getLikes_count() { return likes_count; }
    public int getReading_time() { return reading_time; }
    public List<String> getTags() { return tags; }
    public String getTitle() { return title; }
}
