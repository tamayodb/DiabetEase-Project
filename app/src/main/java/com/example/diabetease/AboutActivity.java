package com.example.diabetease;

import android.os.Bundle;
import android.view.View; // Import View
import android.widget.ImageView; // Import ImageView (or Button if it's a Button)
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about); // This loads your activity_about.xml

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Functionality for the back button in AboutActivity
        ImageView backButton = findViewById(R.id.backButton); // Assuming your back button is an ImageView
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Finish the current activity (AboutActivity) and go back to the previous one (ProfileActivity)
                    finish();
                }
            });
        }
    }
}