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
            item.setSelected(selectedItems.contains(item.getDocumentId()));
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

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.vegetableImage);

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

    private void updateSelectionState(VegetableViewHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.itemCard.setBackground(ContextCompat.getDrawable(context, R.drawable.item_border_selected));
            holder.vegetableName.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else {
            holder.itemCard.setBackground(ContextCompat.getDrawable(context, R.drawable.item_border_default));
            holder.vegetableName.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VegetableViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        TextView vegetableName;
        ImageView vegetableImage;

        public VegetableViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.vegetable_item_card);
            vegetableName = itemView.findViewById(R.id.vegetable_name);
            vegetableImage = itemView.findViewById(R.id.vegetable_image);
        }
    }
}