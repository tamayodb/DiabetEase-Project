package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GlucoseHistory extends AppCompatActivity {

    private static final String TAG = "GlucoseHistory";

    private RecyclerView glucoseRecyclerView;
    private GlucoseAdapter glucoseAdapter;
    private List<Glucose> glucoseList;

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
        glucoseAdapter = new GlucoseAdapter(glucoseList);
        glucoseRecyclerView.setAdapter(glucoseAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        glucoseLogsRef = firestore.collection("glucose_logs");

        Log.d(TAG, "Activity created, starting data load...");
        loadGlucoseData();

        Button todayButton = findViewById(R.id.todayButton);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GlucoseHistory.this, LogsActivity.class);
                startActivity(intent);
                // Optional: add finish() if you want to close the current activity
                // finish();
            }
        });
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

                            // Sort list descending by timestamp (latest first)
                            Collections.sort(glucoseList, new Comparator<Glucose>() {
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