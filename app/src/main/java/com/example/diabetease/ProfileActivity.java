package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
// Removed AppCompatActivity if BaseActivity already extends it
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth

public class ProfileActivity extends BaseActivity { // Assuming BaseActivity handles common setup

    private FirebaseAuth mAuth; // Declare FirebaseAuth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // If setupNavigationBar() is defined in BaseActivity or this class, ensure it's called.
        // If it's in BaseActivity, super.onCreate() might handle it, or you might call it explicitly.
        // For this example, assuming setupNavigationBar(); might be called in BaseActivity or here.
        // If it's specific to ProfileActivity and not in BaseActivity, define and call it:
        // setupNavigationBar(); // Uncomment and implement if needed in this class specifically

        // Find the About button
        Button aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        // Find the Log Out button
        Button logOutButton = findViewById(R.id.logOutButton); // Make sure this ID matches your XML
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out from Firebase
                mAuth.signOut();

                // Create an Intent to start LoginActivity
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                // Set flags to clear the activity stack and start a new task
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Optional: finish current ProfileActivity immediately
            }
        });
    }

    // Example of setupNavigationBar if it was specific to this class
    // private void setupNavigationBar() {
    //     // Your navigation bar setup code
    // }
}