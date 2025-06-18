package com.example.diabetease;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GlucoseAdapter extends RecyclerView.Adapter<GlucoseAdapter.ViewHolder> {

    private static final String TAG = "GlucoseAdapter";
    private List<Glucose> glucoseList;

    public GlucoseAdapter(List<Glucose> glucoseList) {
        this.glucoseList = glucoseList;
        Log.d(TAG, "Adapter created with " + glucoseList.size() + " items");
    }

    @NonNull
    @Override
    public GlucoseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.glucose_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlucoseAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);

        if (position >= glucoseList.size()) {
            Log.e(TAG, "Position " + position + " is out of bounds for list size " + glucoseList.size());
            return;
        }

        Glucose record = glucoseList.get(position);

        Log.d(TAG, "Binding record: Value=" + record.getGlucose_value() +
                ", Status=" + record.getGlucose_status() +
                ", Timestamp=" + record.getTimestamp());

        // Set glucose value and unit
        holder.valueText.setText(String.valueOf(record.getGlucose_value()));
        holder.unitText.setText(" mg/dL");

        // Set status
        holder.statusLabel.setText(record.getGlucose_status());

        // Set status color (using proper Color.parseColor)
        switch (record.getGlucose_status().toLowerCase()) {
            case "high":
                holder.statusLabel.setTextColor(Color.parseColor("#FF3B30")); // Red
                break;
            case "low":
                holder.statusLabel.setTextColor(Color.parseColor("#FF9500")); // Orange
                break;
            case "normal":
            case "good":
                holder.statusLabel.setTextColor(Color.parseColor("#34C759")); // Green
                break;
            default:
                holder.statusLabel.setTextColor(Color.parseColor("#000000")); // Black
                Log.w(TAG, "Unknown status: " + record.getGlucose_status());
                break;
        }

        // Format timestamp to readable date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(record.getTimestamp()));
            holder.dateText.setText(formattedDate);
            Log.d(TAG, "Formatted date: " + formattedDate);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date for timestamp: " + record.getTimestamp(), e);
            holder.dateText.setText("Invalid date");
        }
    }

    @Override
    public int getItemCount() {
        int count = glucoseList.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView valueText, unitText, statusLabel, dateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            valueText = itemView.findViewById(R.id.value_text);
            unitText = itemView.findViewById(R.id.unit_text);
            statusLabel = itemView.findViewById(R.id.status_label);
            dateText = itemView.findViewById(R.id.date_text);

            // Log if any views are null
            if (valueText == null) Log.e("ViewHolder", "value_text is null");
            if (unitText == null) Log.e("ViewHolder", "unit_text is null");
            if (statusLabel == null) Log.e("ViewHolder", "status_label is null");
            if (dateText == null) Log.e("ViewHolder", "date_text is null");
        }
    }

    public void updateList(List<Glucose> newList) {
        Log.d(TAG, "updateList called with " + newList.size() + " items");
        glucoseList = newList;
        notifyDataSetChanged();
    }

    public void addItems(List<Glucose> newItems) {
        Log.d(TAG, "addItems called with " + newItems.size() + " new items");
        int startPosition = glucoseList.size();
        glucoseList.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
    }
}