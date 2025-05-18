package com.example.diabetease;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;

public class LogsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logs);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupNavigationBar();

        BarChart barChart = findViewById(R.id.glucoseBarChart);
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        HashMap<String, Float> dayValues = new HashMap<>();
        HashMap<Integer, List<String>> dayIndexToDatesMap = new HashMap<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

        CollectionReference logsRef = db.collection("glucose_logs");

        logsRef.whereEqualTo("user_id", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String logDate = document.getString("log_date"); // e.g., "2025-05-02"
                        Double glucoseValue = document.getDouble("glucose_value");

                        if (logDate != null && glucoseValue != null) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date date = sdf.parse(logDate);

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 (Sunday) to 7 (Saturday)

                                // Convert to index 0-6 where 0 = Mon, ..., 6 = Sun
                                int dayIndex = (dayOfWeek + 5) % 7;
                                String dayName = days[dayIndex];

                                // Keep the latest or average value — simple latest in this case
                                dayValues.put(dayName, glucoseValue.floatValue());

                                // Collect all log dates for each day index
                                if (!dayIndexToDatesMap.containsKey(dayIndex)) {
                                    dayIndexToDatesMap.put(dayIndex, new ArrayList<>());
                                }
                                dayIndexToDatesMap.get(dayIndex).add(logDate);

                            } catch (Exception e) {
                                Log.e("ParseError", "Date parsing failed", e);
                            }
                        }
                    }

                    // Prepare data for the bar chart
                    List<BarEntry> entries = new ArrayList<>();
                    for (int i = 0; i < days.length; i++) {
                        String day = days[i];
                        float value = dayValues.getOrDefault(day, 0f);
                        entries.add(new BarEntry(i, value));
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "");
                    dataSet.setColor(ContextCompat.getColor(this, R.color.blue));
                    dataSet.setValueTextSize(12f);

                    BarData data = new BarData(dataSet);
                    data.setBarWidth(0.5f);
                    barChart.setData(data);
                    barChart.setFitBars(true);
                    barChart.getDescription().setEnabled(false);

                    // X-axis configuration
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setDrawGridLines(false);

                    // Y-axis configuration
                    YAxis rightAxis = barChart.getAxisRight();
                    rightAxis.setEnabled(false);


                    // Y Axis (Left)
                    YAxis leftAxis = barChart.getAxisLeft();
                    leftAxis.setGranularity(50f);
                    leftAxis.setGranularityEnabled(true);
                    leftAxis.setAxisMinimum(0f);
                    leftAxis.setLabelCount(5, true);
                    leftAxis.setDrawGridLines(true);

                    barChart.animateY(1000);
                    barChart.invalidate();

                    // Handle bar taps
                    barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(com.github.mikephil.charting.data.Entry e, Highlight h) {
                            int xIndex = (int) e.getX();
                            List<String> logDates = dayIndexToDatesMap.get(xIndex);
                            if (logDates != null && !logDates.isEmpty()) {
                                StringBuilder message = new StringBuilder("Log Dates:\n");
                                for (String date : logDates) {
                                    message.append("- ").append(date).append("\n");
                                }
                                Toast.makeText(LogsActivity.this, message.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LogsActivity.this, "No logs found for this day", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected() {
                            // Required implementation of the interface method
                            // This method is called when no chart element is selected
                            // You can leave it empty or add behavior as needed
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch logs", e));
    }
}