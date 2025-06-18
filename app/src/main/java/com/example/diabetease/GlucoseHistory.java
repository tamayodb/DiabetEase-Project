package com.example.diabetease;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GlucoseHistory extends AppCompatActivity {

    private static final String TAG = "GlucoseHistory";

    private RecyclerView glucoseRecyclerView;
    private GlucoseAdapter glucoseAdapter;
    private List<Glucose> glucoseList;
    private List<Glucose> originalGlucoseList; // Store original data for filtering
    private Button filterButton;

    private TextView todayButton, historyButton;
    private int selectedMonth = -1; // -1 means no filter applied
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private CollectionReference glucoseLogsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_history);

        glucoseRecyclerView = findViewById(R.id.glucoseRecyclerView);
        glucoseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        glucoseList = new ArrayList<>();
        originalGlucoseList = new ArrayList<>(); // Initialize original list
        glucoseAdapter = new GlucoseAdapter(glucoseList);
        glucoseRecyclerView.setAdapter(glucoseAdapter);

        filterButton = findViewById(R.id.filterButton);
        historyButton = findViewById(R.id.history_button);
        todayButton = findViewById(R.id.today_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        glucoseLogsRef = firestore.collection("glucose_logs");

        Log.d(TAG, "Activity created, starting data load...");
        loadGlucoseData();

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todayButton.setBackgroundResource(R.drawable.toggle_selected);
                todayButton.setTextColor(Color.WHITE);

                todayButton.setBackgroundColor(Color.TRANSPARENT);

                openDashboard();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyButton.setBackgroundResource(R.drawable.toggle_selected);
                historyButton.setTextColor(Color.WHITE);

                historyButton.setBackgroundColor(Color.TRANSPARENT);
                historyButton.setTextColor(Color.parseColor("#202F3E"));
            }
        });

        // Filter button click listener
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthPickerDialog();
            }
        });

    }

    private void openDashboard() {
        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }

    private void loadGlucoseData() {
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        Log.d(TAG, "Loading data for user: " + currentUserId);

        glucoseLogsRef.whereEqualTo("user_id", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            Log.d(TAG, "Query successful! Found " + result.size() + " documents");

                            glucoseList.clear();  // Clear old data
                            originalGlucoseList.clear(); // Clear original data
                            int validRecords = 0;
                            int invalidRecords = 0;

                            for (DocumentSnapshot document : result) {
                                Log.d(TAG, "Processing document: " + document.getId());

                                String status = document.getString("glucose_status");
                                Long value = document.getLong("glucose_value");
                                Timestamp timestamp = document.getTimestamp("timestamp");

                                // Debug each field
                                Log.d(TAG, "Document fields - Status: " + status +
                                        ", Value: " + value +
                                        ", Timestamp: " + timestamp);

                                // Only require value and timestamp - handle missing status
                                if (value != null && timestamp != null) {
                                    // If status is null, determine it based on glucose value
                                    if (status == null || status.isEmpty()) {
                                        status = determineGlucoseStatus(value.intValue());
                                        Log.d(TAG, "Status was null, determined as: " + status);
                                    }

                                    Glucose glucose = new Glucose(
                                            status,
                                            value.intValue(),
                                            timestamp.toDate().getTime(),
                                            currentUserId
                                    );
                                    originalGlucoseList.add(glucose); // Add to original list
                                    glucoseList.add(glucose);
                                    validRecords++;
                                    Log.d(TAG, "Added valid record: " + value + " " + status);
                                } else {
                                    invalidRecords++;
                                    Log.w(TAG, "Skipped invalid record - missing value or timestamp");
                                }
                            }

                            Log.d(TAG, "Processing complete - Valid: " + validRecords +
                                    ", Invalid: " + invalidRecords +
                                    ", Total in list: " + glucoseList.size());

                            // Sort both lists descending by timestamp (latest first)
                            Collections.sort(glucoseList, new Comparator<Glucose>() {
                                @Override
                                public int compare(Glucose g1, Glucose g2) {
                                    return Long.compare(g2.getTimestamp(), g1.getTimestamp());
                                }
                            });

                            Collections.sort(originalGlucoseList, new Comparator<Glucose>() {
                                @Override
                                public int compare(Glucose g1, Glucose g2) {
                                    return Long.compare(g2.getTimestamp(), g1.getTimestamp());
                                }
                            });

                            Log.d(TAG, "List sorted, notifying adapter...");
                            glucoseAdapter.notifyDataSetChanged(); // Update RecyclerView
                            Log.d(TAG, "Adapter notified successfully");

                        } else {
                            Log.e(TAG, "Query failed", task.getException());
                            Toast.makeText(GlucoseHistory.this, "Failed to load data: " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showMonthPickerDialog() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        // Add "Show All" option at the beginning
        String[] options = new String[months.length + 1];
        options[0] = "Show All";
        System.arraycopy(months, 0, options, 1, months.length);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create the dialog
        AlertDialog dialog = builder.setTitle("Select Month to Filter")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Show All selected
                            selectedMonth = -1;
                            filterButton.setText("Filter by Month");
                            showAllData();
                        } else {
                            // Month selected
                            selectedMonth = which; // 1-12 for Jan-Dec
                            String selectedMonthName = months[which - 1];
                            filterByMonth(selectedMonth);
                            filterButton.setText(selectedMonthName);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        // Apply Poppins font to dialog after showing
        try {
            android.graphics.Typeface poppinsFont = getResources().getFont(R.font.poppins_medium);

            // Apply font to title
            android.widget.TextView titleView = dialog.findViewById(android.R.id.title);
            if (titleView != null) {
                titleView.setTypeface(poppinsFont);
            }

            // Apply font to list items
            android.widget.ListView listView = dialog.getListView();
            if (listView != null) {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    android.view.View listItem = listView.getChildAt(i);
                    if (listItem instanceof android.widget.TextView) {
                        ((android.widget.TextView) listItem).setTypeface(poppinsFont);
                    }
                }
            }

            // Apply font to Cancel button
            android.widget.Button cancelButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (cancelButton != null) {
                cancelButton.setTypeface(poppinsFont);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying font to dialog", e);
        }
    }

    private void filterByMonth(int monthNumber) {
        glucoseList.clear();

        for (Glucose glucose : originalGlucoseList) {
            if (isGlucoseInMonth(glucose, monthNumber)) {
                glucoseList.add(glucose);
            }
        }

        glucoseAdapter.notifyDataSetChanged();

        // Show message if no data found
        if (glucoseList.isEmpty()) {
            Toast.makeText(this, "No glucose records found for selected month", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAllData() {
        glucoseList.clear();
        glucoseList.addAll(originalGlucoseList);
        glucoseAdapter.notifyDataSetChanged();
    }

    private boolean isGlucoseInMonth(Glucose glucose, int monthNumber) {
        try {
            Date recordDate = new Date(glucose.getTimestamp());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(recordDate);
            int recordMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
            return recordMonth == monthNumber;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date for filtering", e);
            return false;
        }
    }


    // Helper method to determine glucose status based on value
    private String determineGlucoseStatus(int glucoseValue) {
        if (glucoseValue < 70) {
            return "Low";
        } else if (glucoseValue > 140) {
            return "High";
        } else {
            return "Good";
        }
    }
}