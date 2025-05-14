package com.example.diabetease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.FruitViewHolder> {

    private List<FruitItem> items;
    private Context context;
    private ArrayList<String> selectedItems;

    public FruitAdapter(List<FruitItem> items, Context context, ArrayList<String> selectedItems) {
        this.items = items;
        this.context = context;
        this.selectedItems = selectedItems;

        // Restore selection state from previous selections
        for (FruitItem item : items) {
            item.setSelected(selectedItems.contains(item.getDocumentId()));
        }
    }

    @NonNull
    @Override
    public FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fruit, parent, false);
        return new FruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitViewHolder holder, int position) {
        FruitItem item = items.get(position);

        holder.fruitName.setText(item.getName());

        // Load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.fruitImage);

        updateSelectionState(holder, item.isSelected());

        holder.itemView.setOnClickListener(v -> {
            boolean currentlySelected = item.isSelected();
            item.setSelected(!currentlySelected);

            if (item.isSelected()) {
                if (!selectedItems.contains(item.getDocumentId())) {
                    selectedItems.add(item.getDocumentId());
                }
            } else {
                selectedItems.remove(item.getDocumentId());
            }

            updateSelectionState(holder, item.isSelected());
        });
    }

    private void updateSelectionState(FruitViewHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            holder.fruitName.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.fruitName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class FruitViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        TextView fruitName;
        ImageView fruitImage;

        public FruitViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.fruit_item_card);
            fruitName = itemView.findViewById(R.id.fruit_name);
            fruitImage = itemView.findViewById(R.id.fruit_icon);
        }
    }
}