package com.example.diabetease;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private static final int VIEW_TYPE_HORIZONTAL = 0;
    private static final int VIEW_TYPE_VERTICAL = 1;

    private List<Blog> blogList;
    private Context context;
    private boolean isVertical;

    public BlogAdapter(Context context, List<Blog> blogs, boolean isVertical) {
        this.context = context;
        this.blogList = blogs;
        this.isVertical = isVertical;
    }

    @Override
    public int getItemViewType(int position) {
        return isVertical ? VIEW_TYPE_VERTICAL : VIEW_TYPE_HORIZONTAL;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_VERTICAL) {
            view = LayoutInflater.from(context).inflate(R.layout.item_blog_card2, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_blog_card, parent, false);
        }
        return new BlogViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogList.get(position);

        if (holder.blogTitle != null) {
            holder.blogTitle.setText(blog.getTitle());
        }
        if (holder.blogCategory != null) {
            holder.blogCategory.setText(blog.getCategory());
        }

        if (holder.blogImage != null) {
            Glide.with(context)
                    .load(blog.getCover_image_url())
                    .into(holder.blogImage);
        }
        
        if (holder.blogReadingTime != null) {
            holder.blogReadingTime.setText(String.valueOf(blog.getReading_time()) + " min read");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SpecificBlogActivity.class);
            intent.putExtra("title", blog.getTitle());
            intent.putExtra("category", blog.getCategory());
            intent.putExtra("image", blog.getCover_image_url());
            intent.putExtra("author", blog.getAuthor());
            intent.putExtra("createdAt", blog.getCreated_at());
            intent.putExtra("content", blog.getContent());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        ImageView blogImage;
        TextView blogTitle, blogCategory, blogReadingTime;

        public BlogViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            blogImage = itemView.findViewById(R.id.blogImage);
            blogTitle = itemView.findViewById(R.id.blogTitle);
            blogCategory = itemView.findViewById(R.id.blogCategory);
            blogReadingTime = itemView.findViewById(R.id.blogReadingTime);
        }
    }
}

