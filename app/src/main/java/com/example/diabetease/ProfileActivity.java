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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";
    // This should be your actual Cloudinary upload preset name
    private static final String CLOUDINARY_UPLOAD_PRESET = "android_profile_pics";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private ShapeableImageView profilePictureImageView;
    private ImageView icPlusImageView;
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText newPasswordEditText;
    private Button updateButton;
    private Button aboutButton;
    private Button logOutButton;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupNavigationBar(); // Make sure this method exists in BaseActivity or here

        // The problematic check that always triggered an error toast has been removed.
        // It's assumed that CLOUDINARY_UPLOAD_PRESET is correctly set now.

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        profilePictureImageView = findViewById(R.id.profilePicture);
        icPlusImageView = findViewById(R.id.ic_plus);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        newPasswordEditText = findViewById(R.id.birthdateEditText); // Assuming this is for the new password
        updateButton = findViewById(R.id.updateButton);
        aboutButton = findViewById(R.id.aboutButton);
        logOutButton = findViewById(R.id.logOutButton);

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
            navigateToLogin();
            return; // Important to return if navigating away
        }

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            Log.d(TAG, "Image selected from gallery. URI: " + selectedImageUri.toString());

                            try {
                                // Attempt to take persistable URI permission if applicable
                                final int intentFlags = data.getFlags();
                                if ((intentFlags & Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0) {
                                    if ("content".equals(selectedImageUri.getScheme())) {
                                        getContentResolver().takePersistableUriPermission(
                                                selectedImageUri,
                                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        );
                                        Log.d(TAG, "Successfully took persistable READ URI permission for: " + selectedImageUri);
                                    } else {
                                        Log.d(TAG, "URI scheme (" + selectedImageUri.getScheme() + ") does not support persistable permissions. Skipping takePersistableUriPermission.");
                                    }
                                } else {
                                    Log.d(TAG, "FLAG_GRANT_READ_URI_PERMISSION not set in intent flags for: " + selectedImageUri + ". Persistable permission not taken.");
                                }
                            } catch (SecurityException e) {
                                Log.e(TAG, "SecurityException taking persistable URI permission for: " + selectedImageUri, e);
                                // This might not be fatal for immediate use, but good to log.
                            }

                            Glide.with(ProfileActivity.this)
                                    .load(selectedImageUri)
                                    .placeholder(R.drawable.profile_pic)
                                    .error(R.drawable.profile_pic)
                                    .into(profilePictureImageView);
                            Toast.makeText(this, "Image selected. Click 'Update' to save.", Toast.LENGTH_LONG).show();
                        } else {
                            selectedImageUri = null; // Reset if data or URI is null
                            Log.w(TAG, "Gallery data or URI is null.");
                        }
                    } else {
                        selectedImageUri = null; // Reset if selection is cancelled or failed
                        Log.w(TAG, "Gallery selection cancelled or failed. Result code: " + result.getResultCode());
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
        emailEditText.setText(currentUser.getEmail());

        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                if (firstName != null && lastName != null) {
                    nameEditText.setText(String.format("%s %s", firstName, lastName));
                } else if (firstName != null) {
                    nameEditText.setText(firstName);
                } else {
                    nameEditText.setText("");
                }

                String profilePicUrl = documentSnapshot.getString("profilePictureUrl");
                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                    Glide.with(this)
                            .load(profilePicUrl)
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .into(profilePictureImageView);
                } else {
                    profilePictureImageView.setImageResource(R.drawable.profile_pic); // Default if no URL
                }
            } else {
                Log.d(TAG, "No such document for user details in Firestore. Checking Auth display name.");
                if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                    nameEditText.setText(currentUser.getDisplayName());
                } else {
                    nameEditText.setText("");
                }
                profilePictureImageView.setImageResource(R.drawable.profile_pic); // Default if no Firestore doc
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching user details from Firestore", e);
            Toast.makeText(ProfileActivity.this, "Failed to load profile details.", Toast.LENGTH_SHORT).show();
            profilePictureImageView.setImageResource(R.drawable.profile_pic); // Default on failure
        });
    }

    private void updateUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String newPassword = Objects.requireNonNull(newPasswordEditText.getText()).toString().trim();
        boolean detailsAttemptedUpdate = false;

        // Update Name in Firestore
        if (!TextUtils.isEmpty(fullName)) {
            // It might be good to compare with current name to avoid unnecessary updates
            String[] nameParts = fullName.split("\\s+", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
            Map<String, Object> nameUpdates = new HashMap<>();
            nameUpdates.put("firstName", firstName);
            nameUpdates.put("lastName", lastName);

            userDocRef.update(nameUpdates)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User name updated in Firestore."))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating name in Firestore", e);
                        Toast.makeText(ProfileActivity.this, "Failed to update name.", Toast.LENGTH_SHORT).show();
                    });
            detailsAttemptedUpdate = true;
        }

        // Update Password in Firebase Auth
        if (!TextUtils.isEmpty(newPassword)) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated successfully.");
                            newPasswordEditText.setText(""); // Clear field after successful update
                            // Toast for password update can be combined or separate
                        } else {
                            Log.e(TAG, "Error updating password", task.getException());
                            Toast.makeText(ProfileActivity.this, "Failed to update password: " +
                                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            detailsAttemptedUpdate = true;
        }

        // Upload Profile Picture if a new one is selected
        if (selectedImageUri != null) {
            uploadProfilePictureToCloudinary(selectedImageUri);
            detailsAttemptedUpdate = true; // Image upload itself is an update attempt.
        } else {
            // If only name/password changed and no new image
            if (detailsAttemptedUpdate) {
                // A general success message if other details were updated and no image was chosen.
                // Specific toasts for name/password success/failure are handled above.
                Toast.makeText(ProfileActivity.this, "Profile update process started.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "No changes to update.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfilePictureToCloudinary(Uri imageUri) {
        Log.d(TAG, "uploadProfilePictureToCloudinary: URI: " + (imageUri != null ? imageUri.toString() : "null"));

        if (currentUser == null) {
            Toast.makeText(this, "Not logged in. Cannot upload picture.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "No image selected to upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        // The problematic check that always triggered an error toast has been removed.
        // It's assumed that CLOUDINARY_UPLOAD_PRESET is correctly set now.

        // URI Validity Check using InputStream (good for content URIs)
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
            if (inputStream == null) { // Should not happen if openInputStream doesn't throw
                Log.e(TAG, "Failed to open InputStream for URI: " + imageUri);
                Toast.makeText(this, "Error: Could not access selected image file.", Toast.LENGTH_LONG).show();
                return;
            }
            // InputStream is valid here, will be closed automatically by try-with-resources
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException for URI: " + imageUri, e);
            Toast.makeText(this, "Error: Selected image file not found.", Toast.LENGTH_LONG).show();
            return;
        } catch (IOException e) { // Catch other IO issues during openInputStream or close
            Log.e(TAG, "IOException for URI: " + imageUri, e);
            Toast.makeText(this, "Error: Could not read selected image.", Toast.LENGTH_LONG).show();
            return;
        } catch (SecurityException e) { // Catch SecurityException if URI access is denied
            Log.e(TAG, "SecurityException opening InputStream for URI: " + imageUri, e);
            Toast.makeText(this, "Error: Permission denied for the selected image.", Toast.LENGTH_LONG).show();
            return;
        }


        Toast.makeText(ProfileActivity.this, "Uploading profile picture...", Toast.LENGTH_SHORT).show();

        MediaManager.get().upload(imageUri)
                .unsigned(CLOUDINARY_UPLOAD_PRESET) // This now uses your actual preset na
                .option("folder", "android_uploads/profile_pictures/" + currentUser.getUid()) // Optional: organize in Cloudinary
               // .option("transformation", new com.cloudinary.Transformation().width(400).height(400).crop("fill").gravity("face")) // Optional: transform
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Cloudinary upload started. ID: " + requestId);
                        // You might want to show a progress bar here
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Log.d(TAG, "Cloudinary upload progress: " + (bytes * 100 / totalBytes) + "%");
                        // Update progress bar
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG, "Cloudinary upload successful. Result: " + resultData);
                        // Hide progress bar
                        String cloudinaryUrl = (String) resultData.get("secure_url"); // Prefer secure_url
                        if (cloudinaryUrl == null) {
                            cloudinaryUrl = (String) resultData.get("url"); // Fallback to url
                        }

                        if (cloudinaryUrl != null && !cloudinaryUrl.isEmpty()) {
                            final String finalCloudinaryUrl = cloudinaryUrl;
                            // Save this URL to Firestore
                            db.collection("users").document(currentUser.getUid())
                                    .update("profilePictureUrl", finalCloudinaryUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Profile picture URL updated in Firestore: " + finalCloudinaryUrl);
                                        Toast.makeText(ProfileActivity.this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                                        selectedImageUri = null; // Clear selected image after successful upload and save
                                        // Update ImageView with the new image from Cloudinary
                                        Glide.with(ProfileActivity.this)
                                                .load(finalCloudinaryUrl)
                                                .placeholder(R.drawable.profile_pic) // Optional
                                                .error(R.drawable.profile_pic)       // Optional
                                                .into(profilePictureImageView);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating profile picture URL in Firestore.", e);
                                        Toast.makeText(ProfileActivity.this, "Uploaded, but failed to save URL to database.", Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Log.e(TAG, "Cloudinary URL is null/empty from resultData.");
                            Toast.makeText(ProfileActivity.this, "Upload succeeded but failed to get image URL.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Cloudinary upload error. ID: " + requestId + ", Error: " + error.getDescription() + " (Code: " + error.getCode() + ")");
                        // Hide progress bar
                        Toast.makeText(ProfileActivity.this, "Image upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.w(TAG, "Cloudinary upload rescheduled. ID: " + requestId + ", Error: " + error.getDescription());
                        // Hide progress bar
                        Toast.makeText(ProfileActivity.this, "Upload rescheduled: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }
                })
                .dispatch(); // Don't forget to dispatch the request
    }

    private void checkPermissionAndOpenGallery() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else { // Android 12 and below
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{permission}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // intent.setType("image/*"); // This is redundant with MediaStore.Images.Media.EXTERNAL_CONTENT_URI for ACTION_PICK
        if (intent.resolveActivity(getPackageManager()) != null) {
            galleryLauncher.launch(intent);
        } else {
            Toast.makeText(this, "No gallery app found.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No activity found to handle image picking intent.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish ProfileActivity so user can't go back to it without logging in
    }

    // Ensure setupNavigationBar() is correctly defined in BaseActivity or this class if needed.
    // protected void setupNavigationBar() {
    //     // Example: if it's not in BaseActivity
    //     // BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view); // Assuming you have one
    //     // bottomNavigationView.setOnItemSelectedListener(item -> { /* handle navigation */ return true; });
    //     Log.d(TAG, "setupNavigationBar called - ensure implementation if used.");
    // }
}