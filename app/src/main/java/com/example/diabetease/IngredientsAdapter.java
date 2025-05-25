package com.example.diabetease;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    private List<Ingredients> ingredients;

    public IngredientsAdapter(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredients ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private ImageView ingredientIcon;
        private TextView ingredientName;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientIcon = itemView.findViewById(R.id.ingredient_icon);
            ingredientName = itemView.findViewById(R.id.ingredient_name);
        }

        public void bind(Ingredients ingredient) {
            ingredientName.setText(ingredient.getName());

            // Load ingredient image using Glide
            Glide.with(itemView.getContext())
                    .load(ingredient.getIngred_image_url())
                    .placeholder(R.drawable.ic_ingredient_default)
                    .error(R.drawable.ic_ingredient_default)
                    .into(ingredientIcon);

        }
    }
}