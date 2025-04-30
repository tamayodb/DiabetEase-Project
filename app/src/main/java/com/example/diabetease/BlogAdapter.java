package com.example.diabetease;

import android.content.Context;
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
    private List<Blog> blogList;
    public Context context;

    public BlogAdapter(HomeActivity context, List<Blog> blogs) {
        this.context = context;
        this.blogList = blogs;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blog_card, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        holder.blogTitle.setText(blog.getTitle());

        Glide.with(context)
                .load(blog.getCover_image_url())
                .into(holder.blogImage);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        ImageView blogImage;
        TextView blogTitle;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            blogImage = itemView.findViewById(R.id.blogImage);
            blogTitle = itemView.findViewById(R.id.blogTitle);
        }
    }
}
