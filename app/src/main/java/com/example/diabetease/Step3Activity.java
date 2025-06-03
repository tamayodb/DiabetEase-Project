package com.example.diabetease;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Step3Activity extends AppCompatActivity {

    // Track the currently expanded section view
    private TextView currentlyExpandedContent = null;
    private ImageView currentlyExpandedIcon = null;
    private void setupSection(int layoutId, String title, String content) {
        View section = findViewById(layoutId);
        TextView titleView = section.findViewById(R.id.section_title);
        TextView contentView = section.findViewById(R.id.section_content);
        ImageView toggleIcon = section.findViewById(R.id.ic_plus);
        RelativeLayout headerLayout = section.findViewById(R.id.header_layout);

        titleView.setText(title);
        contentView.setText(content);

        // Set initial state as collapsed
        contentView.setVisibility(View.GONE);
        toggleIcon.setImageResource(R.drawable.ic_plus);

        headerLayout.setOnClickListener(v -> {
            // If this section is already expanded, collapse it
            if (contentView.getVisibility() == View.VISIBLE) {
                collapseSection(contentView, toggleIcon);
                currentlyExpandedContent = null;
                currentlyExpandedIcon = null;
            }
            // If another section is expanded, collapse it and expand this one
            else {
                if (currentlyExpandedContent != null) {
                    collapseSection(currentlyExpandedContent, currentlyExpandedIcon);
                }
                expandSection(contentView, toggleIcon);
                currentlyExpandedContent = contentView;
                currentlyExpandedIcon = toggleIcon;
            }
        });
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_step3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupSection(R.id.section1, "Information We Collect", "We collect personal information you provide such as your name, email, age, and health-related data (e.g., blood sugar levels, medication logs).");
        setupSection(R.id.section2, "How We Use Your Information", "We use your data to personalize your experience, provide health insights, and improve app functionality.");
        setupSection(R.id.section3, "Data Sharing and Disclosure", "We do not sell your data. Information may be shared with trusted third-party services for analytics and cloud storage, as required to operate the app.");
        setupSection(R.id.section4, "Data Security", "We implement industry-standard security measures to protect your data from unauthorized access, loss, or misuse.");
        setupSection(R.id.section5, "Your Choices and Rights", "You can update or delete your data at any time through the app settings. You may also contact us to request access, correction, or deletion of your personal information.");

        // Setup buttons
        Button refuseBtn = findViewById(R.id.refuse_button);
        Button acceptBtn = findViewById(R.id.accept_button);

        acceptBtn.setOnClickListener(v -> {
            // Get passed data
            String firstName = getIntent().getStringExtra("firstName");
            String lastName = getIntent().getStringExtra("lastName");
            String birthdate = getIntent().getStringExtra("birthdate");
            String email = getIntent().getStringExtra("email");
            String password = getIntent().getStringExtra("password");

            // Define the default profile picture URL
            String defaultProfilePictureUrl = "https://drive.usercontent.google.com/u/0/uc?id=1dtwRrKtn-Mm7bJ28F6OO2-bPWZ4GGwK2&export=download";

            // Register tuserMap.put("profilePictureUrl", defaultProfilePictureUrl);o Firebase Auth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            String userId = firebaseUser.getUid();

                            // Store additional user info to Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("firstName", firstName);
                            userMap.put("lastName", lastName);
                            userMap.put("birthdate", birthdate);
                            userMap.put("email", email);
                            userMap.put("profilePictureUrl", defaultProfilePictureUrl);

                            db.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        // Go to Step4Activity
                                        Intent intent = new Intent(Step3Activity.this, Step4Activity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Firestore write failed
                                        Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            // Auth failed
                            Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        refuseBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(Step3Activity.this)
                    .setTitle("Privacy Policy Required")
                    .setMessage("You must accept the privacy policy to continue using the app.")
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    // Animate expanding a section
    private void expandSection(TextView contentView, ImageView toggleIcon) {
        toggleIcon.setImageResource(R.drawable.ic_minus);

        contentView.setVisibility(View.VISIBLE);
        contentView.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int targetHeight = contentView.getMeasuredHeight();
        contentView.getLayoutParams().height = 0;
        contentView.requestLayout();

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            contentView.getLayoutParams().height = (int) animation.getAnimatedValue();
            contentView.requestLayout();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // After animation completes, set height to wrap_content to handle content changes
                contentView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                contentView.requestLayout();
            }
        });
        animator.start();
    }

    // Animate collapsing a section
    private void collapseSection(TextView contentView, ImageView toggleIcon) {
        // Change icon
        toggleIcon.setImageResource(R.drawable.ic_plus);

        // Get current height
        int initialHeight = contentView.getMeasuredHeight();

        // Create an animation to collapse the height
        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            contentView.getLayoutParams().height = (int) animation.getAnimatedValue();
            contentView.requestLayout();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.setVisibility(View.GONE);
                // Reset the height to wrap_content for next expansion
                contentView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                contentView.requestLayout();
            }
        });
        animator.start();
    }
}