package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private TextInputLayout emailLayout, passwordLayout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // Initialize views
            emailEditText = findViewById(R.id.email_input);
            passwordEditText = findViewById(R.id.password_input);
            Button loginButton = findViewById(R.id.login_button);
            emailLayout = findViewById(R.id.emailLayout);
            passwordLayout = findViewById(R.id.passwordLayout);

            // Set up the login button click listener
            loginButton.setOnClickListener(v -> {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate input
                if (email.isEmpty()) {
                    emailLayout.setError("Please enter your email.");
                    return;
                } else {
                    emailLayout.setError(null);  // Remove error if valid
                }

                if (password.isEmpty()) {
                    passwordLayout.setError("Please enter your password.");
                    return;
                } else {
                    passwordLayout.setError(null);  // Remove error if valid
                }

                // Proceed with login
                loginUser(email, password);
            });
        }

        // Method to log in the user using Firebase Authentication
        private void loginUser(String email, String password) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Login successful
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            // If authentication fails
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
