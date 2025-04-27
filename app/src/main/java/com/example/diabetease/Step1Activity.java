package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.Toast;

import java.util.Calendar;


public class Step1Activity extends AppCompatActivity {

    EditText birthdateInput, firstNameInput, lastNameInput, emailInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_step1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button goToStep2Button = findViewById(R.id.step2_button);
        goToStep2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameInput.getText().toString().trim();
                String lastName = lastNameInput.getText().toString().trim();
                String birthdate = birthdateInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();

                // Basic validation
                if (firstName.isEmpty() || lastName.isEmpty() || birthdate.isEmpty() || email.isEmpty()) {
                    Toast.makeText(Step1Activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(Step1Activity.this, Step2Activity.class);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("birthdate", birthdate);
                intent.putExtra("email", email);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        firstNameInput = findViewById(R.id.first_name);
        lastNameInput = findViewById(R.id.last_name);
        emailInput = findViewById(R.id.email_input);


        birthdateInput = findViewById(R.id.birthdate_input);

        birthdateInput.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Step1Activity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format: MM/DD/YYYY
                        String formattedDate = String.format("%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                        birthdateInput.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });
    }

}