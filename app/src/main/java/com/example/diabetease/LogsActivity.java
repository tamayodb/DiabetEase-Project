package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogsActivity extends BaseActivity {

    private Button logButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView averageNum, yesterdayNum, lowestNum, highestNum;

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

        averageNum = findViewById(R.id.average_num);
        yesterdayNum = findViewById(R.id.yesterday_num);
        lowestNum = findViewById(R.id.lowest_num);
        highestNum = findViewById(R.id.highest_num);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        fetchGlucoseStats();

        //Buttons
        //logButton = findViewById(R.id.log_button);

        //Open pop up log, listeners
        //logButton.setOnClickListener(view -> {
            //Intent intent = new Intent(this, .class)
        //});
    }

    private void fetchGlucoseStats() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("glucose_logs")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Double> glucoseValues = new ArrayList<>();
                    Double yesterdayValue = null;

                    // Prepare yesterday date range
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    String yesterdayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

                    DateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date latestYesterdayLog = null;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Double value = doc.getDouble("glucose_value");
                        String dateStr = doc.getString("log_date");
                        String timeStr = doc.getString("log_time");

                        if (value != null && dateStr != null && timeStr != null) {
                            glucoseValues.add(value);

                            // Check if it's from yesterday
                            if (dateStr.equals(yesterdayStr)) {
                                try {
                                    Date logDate = fullFormat.parse(dateStr + " " + timeStr);
                                    if (latestYesterdayLog == null || logDate.after(latestYesterdayLog)) {
                                        latestYesterdayLog = logDate;
                                        yesterdayValue = value;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    updateUI(glucoseValues, yesterdayValue);
                })
                .addOnFailureListener(e -> {
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
                if (v < min) min = v;
                if (v > max) max = v;
            }

            averageNum.setText(String.format(Locale.getDefault(), "%.2f", sum / values.size()));
            lowestNum.setText(String.format(Locale.getDefault(), "%.2f", min));
            highestNum.setText(String.format(Locale.getDefault(), "%.2f", max));
        } else {
            averageNum.setText("000");
            lowestNum.setText("000");
            highestNum.setText("000");
        }

        if (yesterdayValue != null) {
            yesterdayNum.setText(String.format(Locale.getDefault(), "%.2f", yesterdayValue));
        } else {
            yesterdayNum.setText("000");
        }
    }

}
