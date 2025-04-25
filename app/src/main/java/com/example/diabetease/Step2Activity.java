package com.example.diabetease;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Step2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_step2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String birthdate = getIntent().getStringExtra("birthdate");
        String email = getIntent().getStringExtra("email");


        EditText passwordInput = findViewById(R.id.password_input);
        View bar1 = findViewById(R.id.strength_bar_1);
        View bar2 = findViewById(R.id.strength_bar_2);
        View bar3 = findViewById(R.id.strength_bar_3);
        View bar4 = findViewById(R.id.strength_bar_4);

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    // Hide all bars when input is empty
                    bar1.setVisibility(View.GONE);
                    bar2.setVisibility(View.GONE);
                    bar3.setVisibility(View.GONE);
                    bar4.setVisibility(View.GONE);
                    return;
                }
                // Show bars when user starts typing
                bar1.setVisibility(View.VISIBLE);
                bar2.setVisibility(View.VISIBLE);
                bar3.setVisibility(View.VISIBLE);
                bar4.setVisibility(View.VISIBLE);

                int strength = getPasswordStrengthLevel(s.toString());

                // Default: gray background with rounded corners
                for (int i = 0; i < 4; i++) {
                    View bar = getBarByIndex(i, bar1, bar2, bar3, bar4);
                    setRoundedBarColor(bar, i < strength ? "#00C851" : "#D3D3D3");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        EditText confirmPassInput = findViewById(R.id.confirm_pass_input);

        confirmPassInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = passwordInput.getText().toString();
                String confirmPassword = s.toString();

                if (!confirmPassword.equals(password)) {
                    confirmPassInput.setError("Passwords do not match");
                } else {
                    confirmPassInput.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        Button nextButton = findViewById(R.id.step3_button);

        nextButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPassInput.getText().toString();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in both password fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (getPasswordStrengthLevel(password) < 3) {
                Toast.makeText(this, "Please use a stronger password", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Step2Activity.this, Step3Activity.class);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("birthdate", birthdate);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
        });

    }

    private int getPasswordStrengthLevel(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*+=?-].*")) score++;
        return score;
    }

    private View getBarByIndex(int index, View... bars) {
        return bars[index];
    }

    private void setRoundedBarColor(View view, String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16f); // Adjust as needed
        drawable.setColor(Color.parseColor(colorHex));
        view.setBackground(drawable);
    }


}
