package com.example.diabetease;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class LogsActivity extends BaseActivity {

    private Button logButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView averageNum, yesterdayNum, lowestNum, highestNum, currentDayText;
    private TextView weekRangeText;
    private ImageButton prevWeekButton, nextWeekButton;

    private int currentWeekOffset = 0;

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

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initializeViews();
        setupNavigationBar();
        setupWeekNavigation();
        setupBarChart();

        // Update to show today's correct date
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE MMM dd", Locale.getDefault());
        dayFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        String formattedDay = dayFormat.format(new Date());
        currentDayText.setText(formattedDay);

        fetchGlucoseStats();

        logButton.setOnClickListener(view -> showLogPopup());
    }

    private void initializeViews() {
        averageNum = findViewById(R.id.average_num);
        yesterdayNum = findViewById(R.id.yesterday_num);
        lowestNum = findViewById(R.id.lowest_num);
        highestNum = findViewById(R.id.highest_num);
        logButton = findViewById(R.id.log_button);
        weekRangeText = findViewById(R.id.week_range_text);
        prevWeekButton = findViewById(R.id.prev_week_button);
        nextWeekButton = findViewById(R.id.next_week_button);
        currentDayText = findViewById(R.id.current_day_text);

    }

    private void setupWeekNavigation() {
        prevWeekButton.setOnClickListener(v -> {
            currentWeekOffset--;
            loadWeekData(currentWeekOffset);
        });

        nextWeekButton.setOnClickListener(v -> {
            // Only allow navigation to future weeks up to current week
            if (currentWeekOffset < 0) {
                currentWeekOffset++;
                loadWeekData(currentWeekOffset);
            }
        });
    }

    private void setupBarChart() {
        loadWeekData(currentWeekOffset);
    }

    private void loadWeekData(int weekOffset) {
        BarChart barChart = findViewById(R.id.glucoseBarChart);

        // Create day labels with the actual dates
        String[] days = new String[7];

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = currentUser.getUid();

        // Calculate week boundaries - more reliable approach
        // IMPORTANT: Use "Asia/Manila" timezone consistently
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));

        // Store today's date for logging/debugging
        Date today = calendar.getTime();
        SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault());
        todayFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        Log.d("WeekData", "Today in Manila time: " + todayFormat.format(today));

        // First, add the week offset
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset);

        // Find Monday of this week
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (currentDayOfWeek - Calendar.MONDAY + 7) % 7;
        calendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday);

        // Set to start of Monday (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfWeek = calendar.getTime();

        // Create day labels with dates - ensure consistent timezone
        SimpleDateFormat dayLabelFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        dayLabelFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        // Log each day of the week for debugging
        Log.d("WeekData", "Week days:");
        for (int i = 0; i < 7; i++) {
            // Clone the calendar for each day to avoid modifying the original
            Calendar dayCal = (Calendar) calendar.clone();
            dayCal.add(Calendar.DAY_OF_YEAR, i);
            // Get the day name and convert to uppercase
            days[i] = dayLabelFormat.format(dayCal.getTime()).toUpperCase();
            Log.d("WeekData", "Day " + i + ": " + days[i] + " - " +
                    todayFormat.format(dayCal.getTime()));
        }

        // Now continue with the original calendar
        // Set to end of Sunday (23:59:59)
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfWeek = calendar.getTime();

        // Debug logging
        SimpleDateFormat debugFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Log.d("WeekData", "Week offset: " + weekOffset);
        Log.d("WeekData", "Start of week: " + debugFormat.format(startOfWeek));
        Log.d("WeekData", "End of week: " + debugFormat.format(endOfWeek));

        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String weekRange = displayFormat.format(startOfWeek) + " - " + displayFormat.format(endOfWeek);

        if (weekOffset == 0) {
            weekRangeText.setText("This Week (" + weekRange + ")");
        } else if (weekOffset == -1) {
            weekRangeText.setText("Last Week (" + weekRange + ")");
        } else if (weekOffset == 1) {
            weekRangeText.setText("Next Week (" + weekRange + ")");
        } else if (weekOffset < 0) {
            weekRangeText.setText(Math.abs(weekOffset) + " weeks ago (" + weekRange + ")");
        } else {
            weekRangeText.setText("In " + weekOffset + " weeks (" + weekRange + ")");
        }

        updateNavigationButtons(weekOffset);

        // Create timestamps for Firestore query
        Timestamp startTimestamp = new Timestamp(startOfWeek);
        Timestamp endTimestamp = new Timestamp(endOfWeek);

        // Query Firestore - simpler query to avoid index requirement
        Log.d("FirestoreQuery", "Querying for user: " + currentUserId);
        Log.d("FirestoreQuery", "Start timestamp: " + startTimestamp);
        Log.d("FirestoreQuery", "End timestamp: " + endTimestamp);

        // First, get all logs for the user, then filter by date in code
        db.collection("glucose_logs")
                .whereEqualTo("user_id", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("FirestoreQuery", "Found " + queryDocumentSnapshots.size() + " total documents");

                    HashMap<Integer, List<Double>> weekData = new HashMap<>();
                    HashMap<Integer, List<Date>> dayIndexToTimestamps = new HashMap<>();

                    // Initialize data structures
                    for (int i = 0; i < 7; i++) {
                        weekData.put(i, new ArrayList<>());
                        dayIndexToTimestamps.put(i, new ArrayList<>());
                    }

                    int logsInRange = 0;
                    // Process each document and filter by date
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Double glucoseValue = document.getDouble("glucose_value");
                        Timestamp timestamp = document.getTimestamp("timestamp");

                        if (glucoseValue != null && timestamp != null) {
                            Date date = timestamp.toDate();

                            // Filter by date range in code
                            if (date.compareTo(startOfWeek) >= 0 && date.compareTo(endOfWeek) <= 0) {
                                logsInRange++;

                                // Debug each log
                                Log.d("LogEntry", "Glucose: " + glucoseValue + ", Date: " + debugFormat.format(date));

                                Calendar logCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
                                logCalendar.setTime(date);

                                // Debug the exact day that Firestore is reporting
                                SimpleDateFormat testFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault());
                                testFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
                                Log.d("LogEntry", "Log date in Manila time: " + testFormat.format(date));

                                // Use the same calendar that we used to compute the week boundaries
                                // Find out which day of the week (0-6, with 0 being Monday) this log belongs to
                                Calendar tempCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
                                tempCal.setTime(startOfWeek); // Start with Monday of the week

                                int dayIndex = -1;
                                for (int i = 0; i < 7; i++) {
                                    // Create start and end times for this day
                                    Calendar dayStart = (Calendar) tempCal.clone();
                                    dayStart.add(Calendar.DAY_OF_YEAR, i);
                                    dayStart.set(Calendar.HOUR_OF_DAY, 0);
                                    dayStart.set(Calendar.MINUTE, 0);
                                    dayStart.set(Calendar.SECOND, 0);

                                    Calendar dayEnd = (Calendar) dayStart.clone();
                                    dayEnd.set(Calendar.HOUR_OF_DAY, 23);
                                    dayEnd.set(Calendar.MINUTE, 59);
                                    dayEnd.set(Calendar.SECOND, 59);

                                    // If log date falls between start and end of this day, this is our day index
                                    if (date.compareTo(dayStart.getTime()) >= 0 && date.compareTo(dayEnd.getTime()) <= 0) {
                                        dayIndex = i;
                                        break;
                                    }
                                }

                                Log.d("LogEntry", "Calculated day index: " + dayIndex);

                                if (dayIndex >= 0 && dayIndex < 7) {
                                    weekData.get(dayIndex).add(glucoseValue);
                                    dayIndexToTimestamps.get(dayIndex).add(date);
                                    Log.d("LogEntry", "Added to day " + days[dayIndex]);
                                }
                            }
                        }
                    }

                    Log.d("FirestoreQuery", "Logs in date range: " + logsInRange);

                    // Debug final data
                    for (int i = 0; i < 7; i++) {
                        Log.d("FinalData", days[i] + ": " + weekData.get(i).size() + " logs");
                    }

                    // Create bar chart entries
                    List<BarEntry> entries = new ArrayList<>();
                    HashMap<Integer, Float> dayAverages = new HashMap<>();

                    for (int i = 0; i < 7; i++) {
                        List<Double> dayValues = weekData.get(i);
                        float avg = 0f;
                        if (!dayValues.isEmpty()) {
                            double sum = 0;
                            for (double val : dayValues) {
                                sum += val;
                            }
                            avg = (float) (sum / dayValues.size());
                        }
                        entries.add(new BarEntry(i, avg));
                        dayAverages.put(i, avg);
                    }

                    // Setup chart data
                    BarDataSet dataSet = new BarDataSet(entries, "Glucose Levels");
                    dataSet.setColor(ContextCompat.getColor(this, R.color.blue));
                    dataSet.setValueTextSize(12f);

                    BarData data = new BarData(dataSet);
                    data.setBarWidth(0.5f);

                    barChart.setData(data);
                    barChart.setFitBars(true);
                    barChart.getDescription().setEnabled(false);

                    // Configure X-axis
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setDrawGridLines(false);
                    // Give more space for the multi-line day labels
                    xAxis.setLabelRotationAngle(0);
                    xAxis.setYOffset(10f);
                    xAxis.setTextSize(10f);

                    // Configure Y-axes
                    YAxis rightAxis = barChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    YAxis leftAxis = barChart.getAxisLeft();
                    leftAxis.setGranularity(50f);
                    leftAxis.setGranularityEnabled(true);
                    leftAxis.setAxisMinimum(0f);
                    leftAxis.setLabelCount(5, true);
                    leftAxis.setDrawGridLines(true);

                    barChart.animateY(1000);
                    barChart.invalidate();

                    // Set click listener for bar chart
                    barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            int xIndex = (int) e.getX();
                            List<Date> logDates = dayIndexToTimestamps.get(xIndex);

                            // Get the actual date for this day from the day label
                            String dayLabel = days[xIndex];

                            if (logDates != null && !logDates.isEmpty()) {
                                String message = String.format(
                                        "%s\nAverage: %.1f mg/dL\nNumber of logs: %d",
                                        dayLabel, dayAverages.get(xIndex), logDates.size()
                                );
                                Toast.makeText(LogsActivity.this, message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LogsActivity.this,
                                        dayLabel + "\nNo logs for this day",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected() {
                            // No action needed
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to fetch week logs", e);
                    Toast.makeText(this, "Failed to load week data", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateNavigationButtons(int weekOffset) {
        // Next button: disabled if at current week (offset 0) or in future
        nextWeekButton.setEnabled(weekOffset < 0);

        // Previous button: allow going back 12 weeks
        prevWeekButton.setEnabled(weekOffset > -12);

        // Update visual appearance
        nextWeekButton.setAlpha(nextWeekButton.isEnabled() ? 1.0f : 0.5f);
        prevWeekButton.setAlpha(prevWeekButton.isEnabled() ? 1.0f : 0.5f);
    }

    private void fetchGlucoseStats() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        db.collection("glucose_logs")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Double> glucoseValues = new ArrayList<>();
                    Double yesterdayValue = null;
                    Date latestYesterdayLogTime = null;

                    // Calculate yesterday's date range
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date yesterdayStart = cal.getTime();

                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    cal.set(Calendar.MILLISECOND, 999);
                    Date yesterdayEnd = cal.getTime();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Double value = doc.getDouble("glucose_value");
                        Timestamp timestamp = doc.getTimestamp("timestamp");

                        if (value != null && timestamp != null) {
                            glucoseValues.add(value);

                            Date logDate = timestamp.toDate();
                            // Check if this log is from yesterday and is the latest one
                            if (logDate.compareTo(yesterdayStart) >= 0 &&
                                    logDate.compareTo(yesterdayEnd) <= 0) {
                                if (latestYesterdayLogTime == null || logDate.after(latestYesterdayLogTime)) {
                                    latestYesterdayLogTime = logDate;
                                    yesterdayValue = value;
                                }
                            }
                        }
                    }

                    updateUI(glucoseValues, yesterdayValue);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to fetch glucose stats", e);
                    Toast.makeText(this, "Failed to load glucose data", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI(List<Double> values, Double yesterdayValue) {
        if (!values.isEmpty()) {
            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            for (double v : values) {
                sum += v;
                min = Math.min(min, v);
                max = Math.max(max, v);
            }

            double average = sum / values.size();
            averageNum.setText(String.format(Locale.getDefault(), "%.1f", average));
            lowestNum.setText(String.format(Locale.getDefault(), "%.1f", min));
            highestNum.setText(String.format(Locale.getDefault(), "%.1f", max));
        } else {
            averageNum.setText("---");
            lowestNum.setText("---");
            highestNum.setText("---");
        }

        yesterdayNum.setText(yesterdayValue != null
                ? String.format(Locale.getDefault(), "%.1f", yesterdayValue)
                : "---");
    }

    private void showLogPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.log_card, null);

        EditText gInput = dialogView.findViewById(R.id.ginput);
        Button insertButton = dialogView.findViewById(R.id.insert_button);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        insertButton.setOnClickListener(v -> {
            String inputText = gInput.getText().toString().trim();
            if (!inputText.isEmpty()) {
                try {
                    double glucoseValue = Double.parseDouble(inputText);
                    if (glucoseValue > 0 && glucoseValue <= 1000) { // Reasonable range check
                        insertGlucoseLog(glucoseValue);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Please enter a value between 1 and 1000 mg/dL", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid glucose value", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a glucose value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertGlucoseLog(double glucoseValue) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        // Get current time in Asia/Manila timezone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
        Date manilaDate = calendar.getTime();

        // Create Firestore Timestamp from Manila time
        Timestamp manilaTimestamp = new Timestamp(manilaDate);

        Map<String, Object> logData = new HashMap<>();
        logData.put("user_id", userId);
        logData.put("glucose_value", glucoseValue);
        logData.put("timestamp", manilaTimestamp);

        db.collection("glucose_logs")
                .add(logData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Log added successfully", Toast.LENGTH_SHORT).show();
                    Log.d("NewLog", "Just added log with timestamp: " + manilaDate.toString());
                    fetchGlucoseStats();  // Refresh stats after new log
                    loadWeekData(currentWeekOffset);  // Refresh chart
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding glucose log", e);
                    Toast.makeText(this, "Failed to add log", Toast.LENGTH_SHORT).show();
                });
    }
}