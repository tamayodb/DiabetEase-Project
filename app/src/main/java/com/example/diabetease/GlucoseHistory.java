package com.example.diabetease;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GlucoseHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GlucoseAdapter adapter;
    private List<Glucose> glucoseList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button filterButton, todayButton, historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_history);

        recyclerView = findViewById(R.id.glucoseRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GlucoseAdapter(this, glucoseList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        filterButton = findViewById(R.id.filterButton);
        todayButton = findViewById(R.id.todayButton);
        historyButton = findViewById(R.id.historyButton);

        fetchGlucoseLogs();

        todayButton.setOnClickListener(view -> filterToday());
        historyButton.setOnClickListener(view -> fetchGlucoseLogs());
    }

    private void fetchGlucoseLogs() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("glucose_logs")
                .whereEqualTo("user_id", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    glucoseList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Glucose glucose = document.toObject(Glucose.class);
                        glucoseList.add(glucose);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void filterToday() {
        String userId = auth.getCurrentUser().getUid();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        db.collection("glucose_logs")
                .whereEqualTo("user_id", userId)
                .whereGreaterThanOrEqualTo("timestamp", new java.util.Date(cal.getTimeInMillis()))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    glucoseList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Glucose glucose = document.toObject(Glucose.class);
                        glucoseList.add(glucose);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
