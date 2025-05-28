package com.example.diabetease;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private ShapeableImageView profilePictureImageView;
    private ImageView icPlusImageView;
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText newPasswordEditText; // Assuming birthdateEditText is for new password
    private Button updateButton;
    private Button aboutButton;
    private Button logOutButton;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private Uri selectedImageUri; // To store the selected image URI if user picks a new one

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Views
        profilePictureImageView = findViewById(R.id.profilePicture);
        icPlusImageView = findViewById(R.id.ic_plus);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        // Assuming birthdateEditText is used for entering the new password based on your XML
        newPasswordEditText = findViewById(R.id.birthdateEditText);
        updateButton = findViewById(R.id.updateButton);
        aboutButton = findViewById(R.id.aboutButton);
        logOutButton = findViewById(R.id.logOutButton);

        // Make emailEditText not focusable/editable as it's from Auth
        emailEditText.setFocusable(false);
        emailEditText.setClickable(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (currentUser != null) {
            loadUserProfile();
        } else {
            // No user is signed in, redirect to LoginActivity
            navigateToLogin();
            return; // Important to prevent further execution in onCreate
        }

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            Glide.with(ProfileActivity.this)
                                    .load(selectedImageUri)
                                    .placeholder(R.drawable.profile_pic) // Add a placeholder
                                    .error(R.drawable.profile_pic) // Add an error image
                                    .into(profilePictureImageView);
                            // Note: Image is only updated locally here.
                            // The actual upload happens when the "Update" button is clicked.
                        }
                    }
                });

        icPlusImageView.setOnClickListener(v -> checkPermissionAndOpenGallery());

        updateButton.setOnClickListener(v -> updateUserProfile());

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        logOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            navigateToLogin();
        });
    }

    private void loadUserProfile() {
        if (currentUser == null) return;

        // Load email from Firebase Auth
        emailEditText.setText(currentUser.getEmail());

        // Load name and potentially profile picture URL from Firestore
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String profilePicUrl = documentSnapshot.getString("profilePictureUrl"); // Assuming you store this

                if (firstName != null && lastName != null) {
                    nameEditText.setText(String.format("%s %s", firstName, lastName));
                } else if (firstName != null) {
                    nameEditText.setText(firstName);
                }


                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                    Glide.with(this)
                            .load(profilePicUrl)
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic) // default if load fails
                            .into(profilePictureImageView);
                }
            } else {
                Log.d(TAG, "No such document for user details in Firestore");
                // You might want to pre-fill with email if name is not found
                if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                    nameEditText.setText(currentUser.getDisplayName());
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching user details from Firestore", e));
    }

    private void updateUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String newPassword = Objects.requireNonNull(newPasswordEditText.getText()).toString().trim();

        // Update Name in Firestore
        if (!TextUtils.isEmpty(fullName)) {
            // Simple split for first and last name, adjust if needed
            String[] nameParts = fullName.split("\\s+", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
            Map<String, Object> nameUpdates = new HashMap<>();
            nameUpdates.put("firstName", firstName);
            nameUpdates.put("lastName", lastName);
            // Also update the email in Firestore if you keep a copy there, though Auth is the source of truth
            nameUpdates.put("email", currentUser.getEmail());


            userDocRef.update(nameUpdates) // Or .set(nameUpdates, SetOptions.merge()) if document might not exist
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User name updated in Firestore."))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating name in Firestore", e));
        }

        // Update Password in Firebase Auth
        if (!TextUtils.isEmpty(newPassword)) {
            // It's good practice to re-authenticate the user before a sensitive operation like password change.
            // However, for simplicity here, we'll directly try to update.
            // For a production app, consider implementing re-authentication.
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated successfully.");
                            newPasswordEditText.setText(""); // Clear the field
                            Toast.makeText(ProfileActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error updating password", task.getException());
                            Toast.makeText(ProfileActivity.this, "Failed to update password. " +
                                            "Try logging out and in again. " +
                                            Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            // If only name was changed (or no password entered), still show a success message for other updates
            Toast.makeText(ProfileActivity.this, "Profile details updated!", Toast.LENGTH_SHORT).show();
        }

        // Handle Profile Picture Upload to Firebase Storage (if a new image was selected)
        if (selectedImageUri != null) {
            uploadProfilePicture(selectedImageUri);
            selectedImageUri = null; // Reset after initiating upload
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
        // Implement Firebase Storage upload logic here
        // 1. Get a reference to Firebase Storage
        // StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // 2. Create a reference for the file (e.g., "profile_pictures/user_uid.jpg")
        // StorageReference profilePicRef = storageRef.child("profile_pictures/" + currentUser.getUid() + ".jpg");
        // 3. Upload the file
        // profilePicRef.putFile(imageUri)
        //    .addOnSuccessListener(taskSnapshot -> {
        //        // Get the download URL
        //        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
        //            String downloadUrl = uri.toString();
        //            // Save this downloadUrl to Firestore user document
        //            db.collection("users").document(currentUser.getUid())
        //                    .update("profilePictureUrl", downloadUrl)
        //                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile picture URL updated in Firestore."))
        //                    .addOnFailureListener(e -> Log.e(TAG, "Error updating profile picture URL.", e));
        //            Toast.makeText(ProfileActivity.this, "Profile picture uploaded!", Toast.LENGTH_SHORT).show();
        //        });
        //    })
        //    .addOnFailureListener(e -> {
        //        Log.e(TAG, "Error uploading profile picture", e);
        //        Toast.makeText(ProfileActivity.this, "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
        //    });
        Log.i(TAG, "Profile picture upload logic for " + imageUri.toString() + " needs to be implemented with Firebase Storage.");
        Toast.makeText(this, "Profile picture upload needs Firebase Storage setup.", Toast.LENGTH_LONG).show();

    }


    private void checkPermissionAndOpenGallery() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission is required to select an image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}