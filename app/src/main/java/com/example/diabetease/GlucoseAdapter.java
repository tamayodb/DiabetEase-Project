package com.example.diabetease;

import com.example.diabetease.Glucose;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class GlucoseAdapter extends RecyclerView.Adapter<GlucoseAdapter.GlucoseViewHolder> {

    private List<Glucose> glucoseList;
    private Context context;

    public GlucoseAdapter(Context context, List<Glucose> glucoseList) {
        this.context = context;
        this.glucoseList = glucoseList;
    }

    @NonNull
    @Override
    public GlucoseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.glucose_item, parent, false);
        return new GlucoseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlucoseViewHolder holder, int position) {
        Glucose glucose = glucoseList.get(position);
        holder.glucoseValue.setText(String.valueOf(glucose.getGlucose_value()));
        holder.unitText.setText("mg/dL");

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(glucose.getTimestamp());
        holder.dateText.setText(formattedTime);

        // Set status label text and color
        String status = glucose.getGlucose_status();
        holder.statusLabel.setText(status);

        if ("High".equalsIgnoreCase(status)) {
            holder.statusLabel.setTextColor(Color.parseColor("#FF5F5F")); // red
        } else if ("Low".equalsIgnoreCase(status)) {
            holder.statusLabel.setTextColor(Color.parseColor("#FFB84D")); // orange
        } else {
            holder.statusLabel.setTextColor(Color.parseColor("#4CAF50")); // green
        }
    }

    @Override
    public int getItemCount() {
        return glucoseList.size();
    }

    public static class GlucoseViewHolder extends RecyclerView.ViewHolder {
        TextView glucoseValue, unitText, dateText, statusLabel;

        public GlucoseViewHolder(@NonNull View itemView) {
            super(itemView);
            glucoseValue = itemView.findViewById(R.id.value_text);
            unitText = itemView.findViewById(R.id.unit_text);
            dateText = itemView.findViewById(R.id.date_text);
            statusLabel = itemView.findViewById(R.id.status_label);
        }
    }
}
