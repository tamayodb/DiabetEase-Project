package com.example.diabetease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class VegetableAdapter extends RecyclerView.Adapter<VegetableAdapter.VegetableViewHolder> {

    private List<VegetableItem> items;
    private Context context;
    private ArrayList<String> selectedItems;

    public VegetableAdapter(List<VegetableItem> items, Context context, ArrayList<String> selectedItems) {
        this.items = items;
        this.context = context;
        this.selectedItems = selectedItems;

        // Restore selection state
        for (VegetableItem item : items) {
            if (selectedItems.contains(item.getName())) {
                item.setSelected(true);
            }
        }
    }

    @NonNull
    @Override
    public VegetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vegetable, parent, false);
        return new VegetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VegetableViewHolder holder, int position) {
        VegetableItem item = items.get(position);

        holder.vegetableName.setText(item.getName());

        // Load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // Optional: Show placeholder while loading
                .error(R.drawable.error_image)           // Optional: Show error image if loading fails
                .into(holder.vegetableImage);

        updateSelectionState(holder, item.isSelected());

        holder.itemView.setOnClickListener(v -> {
            boolean currentlySelected = item.isSelected();
            item.setSelected(!currentlySelected);

            if (item.isSelected()) {
                if (!selectedItems.contains(item.getName())) {
                    selectedItems.add(item.getName());
                }
            } else {
                selectedItems.remove(item.getName());
            }

            updateSelectionState(holder, item.isSelected());
        });
    }

    private void updateSelectionState(VegetableViewHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.itemCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
            holder.vegetableName.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.itemCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.vegetableName.setTextColor(context.getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VegetableViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        TextView vegetableName;
        ImageView vegetableImage; // New field for image view

        public VegetableViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.vegetable_item_card);
            vegetableName = itemView.findViewById(R.id.vegetable_name);
            vegetableImage = itemView.findViewById(R.id.vegetable_image); // New image view
        }
    }
}