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
import java.util.ArrayList;
import java.util.List;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.FruitViewHolder> {

    private List<FruitItem> fruitItems;
    private Context context;
    private ArrayList<String> selectedFruits;

    public FruitAdapter(List<FruitItem> fruitItems, Context context, ArrayList<String> selectedFruits) {
        this.fruitItems = fruitItems;
        this.context = context;
        this.selectedFruits = selectedFruits;

        // Mark pre-selected items
        for (FruitItem item : fruitItems) {
            if (selectedFruits.contains(item.getName())) {
                item.setSelected(true);
            }
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
        FruitItem item = fruitItems.get(position);
        holder.fruitName.setText(item.getName());
        holder.fruitIcon.setImageResource(item.getIconResource());

        updateSelectionState(holder, item.isSelected());

        holder.itemCard.setOnClickListener(v -> {
            boolean isCurrentlySelected = item.isSelected();
            item.setSelected(!isCurrentlySelected);

            if (item.isSelected()) {
                if (!selectedFruits.contains(item.getName())) {
                    selectedFruits.add(item.getName());
                }
            } else {
                selectedFruits.remove(item.getName());
            }

            updateSelectionState(holder, item.isSelected());
        });
    }

    private void updateSelectionState(FruitViewHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            holder.fruitName.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.fruitName.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return fruitItems.size();
    }

    public static class FruitViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        ImageView fruitIcon;
        TextView fruitName;

        public FruitViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.fruit_item_card);
            fruitIcon = itemView.findViewById(R.id.fruit_icon);
            fruitName = itemView.findViewById(R.id.fruit_name);
        }
    }
}