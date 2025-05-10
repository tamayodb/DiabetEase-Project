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

public class MeatAdapter extends RecyclerView.Adapter<MeatAdapter.MeatViewHolder> {

    private List<MeatItem> meatItems;
    private Context context;
    private ArrayList<String> selectedMeats;

    public MeatAdapter(List<MeatItem> meatItems, Context context, ArrayList<String> selectedMeats) {
        this.meatItems = meatItems;
        this.context = context;
        this.selectedMeats = selectedMeats;

        for (MeatItem item : meatItems) {
            if (selectedMeats.contains(item.getName())) {
                item.setSelected(true);
            }
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
        MeatItem item = meatItems.get(position);
        holder.meatName.setText(item.getName());
        holder.meatIcon.setImageResource(item.getIconResource());

        updateSelectionState(holder, item.isSelected());

        holder.itemCard.setOnClickListener(v -> {
            boolean isCurrentlySelected = item.isSelected();
            item.setSelected(!isCurrentlySelected);

            if (item.isSelected()) {
                if (!selectedMeats.contains(item.getName())) {
                    selectedMeats.add(item.getName());
                }
            } else {
                selectedMeats.remove(item.getName());
            }

            updateSelectionState(holder, item.isSelected());
        });
    }

    private void updateSelectionState(MeatViewHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            holder.meatName.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.meatName.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return meatItems.size();
    }

    public static class MeatViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        ImageView meatIcon;
        TextView meatName;

        public MeatViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.meat_item_card);
            meatIcon = itemView.findViewById(R.id.meat_icon);
            meatName = itemView.findViewById(R.id.meat_name);
        }
    }
}