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

public class MeatAdapter extends RecyclerView.Adapter<MeatAdapter.MeatViewHolder> {

    private List<MeatItem> items;
    private Context context;
    private ArrayList<String> selectedItems;

    public MeatAdapter(List<MeatItem> items, Context context, ArrayList<String> selectedItems) {
        this.items = items;
        this.context = context;
        this.selectedItems = selectedItems;

        // Restore selection state from previous selections
        for (MeatItem item : items) {
            item.setSelected(selectedItems.contains(item.getDocumentId()));
        }
    }

    @NonNull
    @Override
    public MeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meat, parent, false);
        return new MeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeatViewHolder holder, int position) {
        MeatItem item = items.get(position);

        holder.meatName.setText(item.getName());

        // Load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.meatImage);

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

    private void updateSelectionState(MeatViewHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            holder.meatName.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.meatName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MeatViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        TextView meatName;
        ImageView meatImage;

        public MeatViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.meat_item_card);
            meatName = itemView.findViewById(R.id.meat_name);
            meatImage = itemView.findViewById(R.id.meat_icon);
        }
    }
}