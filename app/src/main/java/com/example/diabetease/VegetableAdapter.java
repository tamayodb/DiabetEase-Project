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

public class VegetableAdapter extends RecyclerView.Adapter<VegetableAdapter.VegetableViewHolder> {

    private List<VegetableItem> vegetableItems;
    private Context context;
    private ArrayList<String> selectedVegetables;

    public VegetableAdapter(List<VegetableItem> vegetableItems, Context context, ArrayList<String> selectedVegetables) {
        this.vegetableItems = vegetableItems;
        this.context = context;
        this.selectedVegetables = selectedVegetables;

        // Mark items as selected if they are in the selectedVegetables list
        for (VegetableItem item : vegetableItems) {
            if (selectedVegetables.contains(item.getName())) {
                item.setSelected(true);
            }
        }
    }

    @NonNull
    @Override
    public VegetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vegetable, parent, false);
        return new VegetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VegetableViewHolder holder, int position) {
        VegetableItem item = vegetableItems.get(position);

        holder.vegetableName.setText(item.getName());
        holder.vegetableIcon.setImageResource(item.getIconResource());

        // Set the selection state
        updateSelectionState(holder, item.isSelected());

        holder.itemCard.setOnClickListener(v -> {
            // Toggle selection
            boolean isCurrentlySelected = item.isSelected();
            item.setSelected(!isCurrentlySelected);

            // Update the selectedVegetables list
            if (item.isSelected()) {
                if (!selectedVegetables.contains(item.getName())) {
                    selectedVegetables.add(item.getName());
                }
            } else {
                selectedVegetables.remove(item.getName());
            }

            // Update the visual state
            updateSelectionState(holder, item.isSelected());
        });
    }

    private void updateSelectionState(VegetableViewHolder holder, boolean isSelected) {
        if (isSelected) {
            // Selected state
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            holder.vegetableName.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            // Unselected state
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.vegetableName.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return vegetableItems.size();
    }

    public static class VegetableViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        ImageView vegetableIcon;
        TextView vegetableName;

        public VegetableViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.vegetable_item_card);
            vegetableIcon = itemView.findViewById(R.id.vegetable_icon);
            vegetableName = itemView.findViewById(R.id.vegetable_name);
        }
    }
}