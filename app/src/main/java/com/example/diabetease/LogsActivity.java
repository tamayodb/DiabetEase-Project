package com.example.diabetease;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        //pop up
        logButton = findViewById(R.id.log_button);
        logButton.setOnClickListener(view -> showLogPopup());


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
            averageNum.setText("---");
            lowestNum.setText("---");
            highestNum.setText("---");
        }

        if (yesterdayValue != null) {
            yesterdayNum.setText(String.format(Locale.getDefault(), "%.2f", yesterdayValue));
        } else {
            yesterdayNum.setText("---");
        }
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
                double glucoseValue = Double.parseDouble(inputText);

                insertGlucoseLog(glucoseValue);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a glucose value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertGlucoseLog(double value) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        String status = "Normal";
        if (value > 140) status = "High";
        else if (value < 70) status = "Low";

        Map<String, Object> log = new HashMap<>();
        log.put("user_id", userId);
        log.put("glucose_value", value);
        log.put("glucose_status", status);
        log.put("log_date", date);
        log.put("log_time", time);

        FirebaseFirestore.getInstance()
                .collection("glucose_logs")
                .add(log)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Log saved", Toast.LENGTH_SHORT).show();
                    fetchGlucoseStats(); // Refresh stats on UI
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save log", Toast.LENGTH_SHORT).show();
                });
    }


}
