package com.example.diabetease;

import android.Manifest;
import android.app.Activity;
// --- BIRTHDATE ---
import android.app.DatePickerDialog;
// --- BIRTHDATE ---
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
// --- BIRTHDATE ---
import android.widget.DatePicker;
// --- BIRTHDATE ---
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
// --- BIRTHDATE ---
import java.text.ParseException;
import java.text.SimpleDateFormat;
// --- BIRTHDATE ---
import java.util.ArrayList;
// --- BIRTHDATE ---
import java.util.Calendar;
// --- BIRTHDATE ---
import java.util.HashMap;
import java.util.List;
// --- BIRTHDATE ---
import java.util.Locale;
// --- BIRTHDATE ---
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";
    private static final String CLOUDINARY_UPLOAD_PRESET = "android_profile_pics"; // Your Cloudinary upload preset

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private ShapeableImageView profilePictureImageView;
    private ImageView icPlusImageView;
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText currentPasswordEditText; // For re-authentication
    // --- BIRTHDATE ---
    private TextInputEditText birthdateEditText; // Added for birthdate
    // --- BIRTHDATE ---
    private Button updateButton;
    private Button aboutButton;
    private Button logOutButton;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private Uri selectedImageUri = null;

    private int pendingOperations = 0;
    private final List<String> successfulUpdates = new ArrayList<>();
    private String originalFullName = "";
    private String originalEmailFromAuth = ""; // Stores the email from Auth at load time
    // --- BIRTHDATE ---
    private String originalBirthdate = ""; // Stores the original birthdate from Firestore
    private Calendar selectedBirthdateCalendar = Calendar.getInstance(); // For DatePickerDialog
    private final SimpleDateFormat birthdateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Date format
    // --- BIRTHDATE ---


    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isValidEmail(String emailStr) {
        if (emailStr == null) {
            return false;
        }
        return VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupNavigationBar(); // From BaseActivity

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        profilePictureImageView = findViewById(R.id.profilePicture);
        icPlusImageView = findViewById(R.id.ic_plus);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        // --- BIRTHDATE ---
        // Make sure you have a TextInputEditText with id "birthdateEditText" in your activity_profile.xml
        birthdateEditText = findViewById(R.id.birthdateEditText);
        // --- BIRTHDATE ---
        updateButton = findViewById(R.id.updateButton);
        aboutButton = findViewById(R.id.aboutButton);
        logOutButton = findViewById(R.id.logOutButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (currentUser != null) {
            loadUserProfile();
        } else {
            navigateToLogin();
            return;
        }

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            Log.d(TAG, "Image selected: " + selectedImageUri.toString());
                            try {
                                if ("content".equals(selectedImageUri.getScheme())) {
                                    final int intentFlags = data.getFlags();
                                    if ((intentFlags & Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0) {
                                        getContentResolver().takePersistableUriPermission(
                                                selectedImageUri,
                                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        );
                                    }
                                }
                            } catch (SecurityException e) {
                                Log.e(TAG, "SecurityException taking persistable URI permission", e);
                            }
                            Glide.with(ProfileActivity.this).load(selectedImageUri)
                                    .placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic)
                                    .into(profilePictureImageView);
                            Toast.makeText(this, "Image selected. Click 'Update' to save.", Toast.LENGTH_SHORT).show();
                        } else {
                            selectedImageUri = null;
                        }
                    } else {
                        selectedImageUri = null;
                    }
                });

        icPlusImageView.setOnClickListener(v -> checkPermissionAndOpenGallery());
        updateButton.setOnClickListener(v -> updateUserProfile());
        aboutButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, AboutActivity.class)));
        logOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            navigateToLogin();
        });

        // --- BIRTHDATE ---
        birthdateEditText.setOnClickListener(v -> showDatePickerDialog());
        birthdateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePickerDialog();
            }
        });
        // --- BIRTHDATE ---
    }

    // --- BIRTHDATE ---
    private void showDatePickerDialog() {
        Calendar calendarToShow = Calendar.getInstance();
        if (!TextUtils.isEmpty(originalBirthdate)) { // Use originalBirthdate to initialize
            try {
                calendarToShow.setTime(Objects.requireNonNull(birthdateFormat.parse(originalBirthdate)));
            } catch (ParseException e) {
                Log.e(TAG, "Could not parse originalBirthdate for DatePicker: " + originalBirthdate, e);
                // calendarToShow remains today's date
            }
        }

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            selectedBirthdateCalendar.set(Calendar.YEAR, year);
            selectedBirthdateCalendar.set(Calendar.MONTH, monthOfYear);
            selectedBirthdateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            birthdateEditText.setText(birthdateFormat.format(selectedBirthdateCalendar.getTime()));
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, dateSetListener,
                calendarToShow.get(Calendar.YEAR),
                calendarToShow.get(Calendar.MONTH),
                calendarToShow.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Optional: Prevent future dates
        datePickerDialog.show();
    }
    // --- BIRTHDATE ---

    private void loadUserProfile() {
        if (currentUser == null) return;

        originalEmailFromAuth = currentUser.getEmail();
        if (originalEmailFromAuth != null) {
            emailEditText.setText(originalEmailFromAuth);
        } else {
            emailEditText.setHint("No email associated with login.");
        }

        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                if (firstName != null && lastName != null) {
                    originalFullName = String.format("%s %s", firstName, lastName);
                } else if (firstName != null) {
                    originalFullName = firstName;
                } else {
                    originalFullName = "";
                }
                nameEditText.setText(originalFullName);

                String profilePicUrl = documentSnapshot.getString("profilePictureUrl");
                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                    Glide.with(this).load(profilePicUrl)
                            .placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic)
                            .into(profilePictureImageView);
                } else {
                    profilePictureImageView.setImageResource(R.drawable.profile_pic);
                }

                // --- BIRTHDATE ---
                originalBirthdate = documentSnapshot.getString("birthdate"); // Load birthdate
                if (originalBirthdate != null && !originalBirthdate.isEmpty()) {
                    birthdateEditText.setText(originalBirthdate);
                    try {
                        // Initialize selectedBirthdateCalendar with the loaded date
                        selectedBirthdateCalendar.setTime(Objects.requireNonNull(birthdateFormat.parse(originalBirthdate)));
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing stored birthdate for Calendar: " + originalBirthdate, e);
                        selectedBirthdateCalendar = Calendar.getInstance(); // Reset to current if parsing fails
                    }
                } else {
                    birthdateEditText.setText(""); // Clear if no birthdate
                    birthdateEditText.setHint("YYYY-MM-DD"); // Set a hint
                    selectedBirthdateCalendar = Calendar.getInstance(); // Reset
                }
                // --- BIRTHDATE ---

            } else {
                Log.w(TAG, "User document does not exist in Firestore.");
                if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                    originalFullName = currentUser.getDisplayName();
                } else {
                    originalFullName = "";
                }
                nameEditText.setText(originalFullName);
                profilePictureImageView.setImageResource(R.drawable.profile_pic);
                // --- BIRTHDATE ---
                birthdateEditText.setText("");
                birthdateEditText.setHint("YYYY-MM-DD");
                originalBirthdate = "";
                selectedBirthdateCalendar = Calendar.getInstance();
                // --- BIRTHDATE ---
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching user details from Firestore", e);
            Toast.makeText(ProfileActivity.this, "Failed to load profile details.", Toast.LENGTH_SHORT).show();
            if (currentUser.getDisplayName() != null) {
                nameEditText.setText(currentUser.getDisplayName());
            }
            // --- BIRTHDATE ---
            birthdateEditText.setText("");
            birthdateEditText.setHint("YYYY-MM-DD");
            originalBirthdate = "";
            selectedBirthdateCalendar = Calendar.getInstance();
            // --- BIRTHDATE ---
        });
    }

    private synchronized void operationCompleted(boolean success, String fieldName) {
        pendingOperations--;
        if (success && fieldName != null) {
            if (!successfulUpdates.contains(fieldName)) {
                successfulUpdates.add(fieldName);
            }
        }
        Log.d(TAG, "Operation completed. Pending: " + pendingOperations + ", Successes: " + successfulUpdates.size() + (success ? " (" + fieldName + " success)" : " (failure)"));

        if (pendingOperations == 0) {
            if (!successfulUpdates.isEmpty()) {
                if (successfulUpdates.size() > 1) {
                    Toast.makeText(ProfileActivity.this, "Profile details updated!", Toast.LENGTH_LONG).show();
                } else if (successfulUpdates.size() == 1) {
                    String updatedField = successfulUpdates.get(0);
                    // --- BIRTHDATE ---
                    if ("Email".equals(updatedField)) {
                        Toast.makeText(ProfileActivity.this, "Email changed successfully!", Toast.LENGTH_LONG).show();
                    } else if ("Birthdate".equals(updatedField)) {
                        Toast.makeText(ProfileActivity.this, "Birthdate updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, updatedField + " updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                    // --- BIRTHDATE ---
                }
                // Refresh the current user data after all operations
                if (mAuth.getCurrentUser() != null) {
                    currentUser = mAuth.getCurrentUser();
                    if (successfulUpdates.contains("Email")) {
                        originalEmailFromAuth = currentUser.getEmail();
                    }
                    emailEditText.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
                    if (successfulUpdates.contains("Name")) {
                        nameEditText.setText(originalFullName);
                    }
                    // --- BIRTHDATE ---
                    if (successfulUpdates.contains("Birthdate")) {
                        // originalBirthdate is already updated on successful Firestore write
                        birthdateEditText.setText(originalBirthdate);
                    }
                    // --- BIRTHDATE ---
                }
            } else {
                Log.d(TAG, "All operations finished, but no successful updates recorded or all failed.");
                if (currentUser != null && originalEmailFromAuth != null && !emailEditText.getText().toString().equals(originalEmailFromAuth)
                        && !successfulUpdates.contains("Email")) {
                    emailEditText.setText(originalEmailFromAuth);
                }
                // --- BIRTHDATE ---
                // Revert birthdate UI if update failed and it was changed in the EditText
                String currentBirthdateText = birthdateEditText.getText().toString();
                if (!currentBirthdateText.equals(originalBirthdate) && !successfulUpdates.contains("Birthdate")) {
                    birthdateEditText.setText(originalBirthdate);
                }
                // --- BIRTHDATE ---
            }
            successfulUpdates.clear();
            newPasswordEditText.setText("");
            currentPasswordEditText.setText("");
            selectedImageUri = null;
        }
    }


    private void reauthenticateAndPerformSensitiveOperation(String currentPassword, String operationType, Runnable operation) {
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(this, "User not properly logged in.", Toast.LENGTH_SHORT).show();
            operationCompleted(false, operationType);
            return;
        }

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordEditText.setError("Current password is required to change " + operationType.toLowerCase() + ".");
            currentPasswordEditText.requestFocus();
            Toast.makeText(this, "Current password is required to change " + operationType.toLowerCase() + ".", Toast.LENGTH_LONG).show();
            operationCompleted(false, operationType);
            return;
        }
        currentPasswordEditText.setError(null);

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
        currentUser.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User re-authenticated successfully for " + operationType + " update.");
                    operation.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Re-authentication failed for " + operationType + " update.", e);
                    currentPasswordEditText.setError("Incorrect current password.");
                    currentPasswordEditText.requestFocus();
                    Toast.makeText(ProfileActivity.this, "Re-authentication failed. Check current password.", Toast.LENGTH_LONG).show();
                    operationCompleted(false, operationType);
                });
    }

    private void updateUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        pendingOperations = 0;
        successfulUpdates.clear();
        nameEditText.setError(null);
        emailEditText.setError(null);
        newPasswordEditText.setError(null);
        currentPasswordEditText.setError(null);
        // --- BIRTHDATE ---
        birthdateEditText.setError(null);
        // --- BIRTHDATE ---

        String fullName = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String newAuthEmail = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String newPassword = Objects.requireNonNull(newPasswordEditText.getText()).toString().trim();
        String currentPassword = Objects.requireNonNull(currentPasswordEditText.getText()).toString();
        // --- BIRTHDATE ---
        String newBirthdate = Objects.requireNonNull(birthdateEditText.getText()).toString().trim();
        // --- BIRTHDATE ---

        boolean anyFieldModified = false;

        // --- Update Name in Firestore ---
        if (!TextUtils.isEmpty(fullName) && !fullName.equals(originalFullName)) {
            anyFieldModified = true;
            pendingOperations++;
            String[] nameParts = fullName.split("\\s+", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
            Map<String, Object> nameUpdates = new HashMap<>();
            nameUpdates.put("firstName", firstName);
            nameUpdates.put("lastName", lastName);
            final String attemptedFullName = fullName;
            userDocRef.update(nameUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User name updated in Firestore.");
                        originalFullName = attemptedFullName;
                        operationCompleted(true, "Name");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating name in Firestore", e);
                        Toast.makeText(ProfileActivity.this, "Failed to update name.", Toast.LENGTH_SHORT).show();
                        operationCompleted(false, "Name");
                    });
        }

        // --- BIRTHDATE ---
        // Update Birthdate in Firestore
        // Check if birthdate changed from original or if original was empty and new one is not
        boolean birthdateActuallyChanged = (!newBirthdate.equals(originalBirthdate) && !TextUtils.isEmpty(newBirthdate)) ||
                (TextUtils.isEmpty(originalBirthdate) && !TextUtils.isEmpty(newBirthdate));

        if (birthdateActuallyChanged) {
            anyFieldModified = true;
            pendingOperations++;
            DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
            final String attemptedBirthdate = newBirthdate; // For closure
            userDocRef.update("birthdate", newBirthdate)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User birthdate updated in Firestore.");
                        originalBirthdate = attemptedBirthdate; // Update originalBirthdate on success
                        operationCompleted(true, "Birthdate");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating birthdate in Firestore", e);
                        Toast.makeText(ProfileActivity.this, "Failed to update birthdate.", Toast.LENGTH_SHORT).show();
                        operationCompleted(false, "Birthdate");
                    });
        }
        // --- BIRTHDATE ---

        // --- Update Firebase Authentication Email ---
        boolean authEmailChanged = !TextUtils.isEmpty(newAuthEmail) && originalEmailFromAuth != null && !newAuthEmail.equalsIgnoreCase(originalEmailFromAuth);

        if (authEmailChanged) {
            anyFieldModified = true;
            pendingOperations++;
            Log.d(TAG, "Attempting to update Firebase Auth email. Pending ops: " + pendingOperations);

            if (!isValidEmail(newAuthEmail)) {
                emailEditText.setError("Please enter a valid email address.");
                emailEditText.requestFocus();
                operationCompleted(false, "Email");
            } else {
                emailEditText.setError(null);
                reauthenticateAndPerformSensitiveOperation(currentPassword, "Email", () -> {
                    currentUser.updateEmail(newAuthEmail)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Firebase Auth email update initiated. Verification email sent to " + newAuthEmail);
                                    // Successfully initiated email update. Firebase handles the rest (e.g., verification email).
                                    // We can consider this "success" for the purpose of the operation count.
                                    // The 'originalEmailFromAuth' will be updated in operationCompleted upon success.
                                    operationCompleted(true, "Email");
                                } else {
                                    Log.e(TAG, "Error updating Firebase Auth email", task.getException());
                                    String emailErrorMessage = "Failed to update email.";
                                    if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                        emailErrorMessage = "Security check: Enter current password to update email.";
                                        currentPasswordEditText.requestFocus();
                                    } else if (task.getException() != null && task.getException().getMessage() != null &&
                                            task.getException().getMessage().contains("EMAIL_EXISTS")) {
                                        emailErrorMessage = "This email address is already in use.";
                                        emailEditText.setError(emailErrorMessage);
                                        emailEditText.requestFocus();
                                    } else if (task.getException() != null && task.getException().getMessage() != null) {
                                        emailErrorMessage = "Failed to update email: " + task.getException().getMessage();
                                    }
                                    Toast.makeText(ProfileActivity.this, emailErrorMessage, Toast.LENGTH_LONG).show();
                                    emailEditText.setText(originalEmailFromAuth);
                                    operationCompleted(false, "Email");
                                }
                            });
                });
            }
        }

        // --- Update Password in Firebase Auth ---
        if (!TextUtils.isEmpty(newPassword)) {
            anyFieldModified = true;
            pendingOperations++;
            Log.d(TAG, "Attempting to update Firebase Auth password. Pending ops: " + pendingOperations);
            if (newPassword.length() < 6) {
                newPasswordEditText.setError("Password must be at least 6 characters.");
                newPasswordEditText.requestFocus();
                operationCompleted(false, "Password");
            } else {
                newPasswordEditText.setError(null);
                reauthenticateAndPerformSensitiveOperation(currentPassword, "Password", () -> {
                    currentUser.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User password updated successfully.");
                                    operationCompleted(true, "Password");
                                } else {
                                    Log.e(TAG, "Error updating password", task.getException());
                                    String pwErrorMessage = "Failed to update password.";
                                    if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                        pwErrorMessage = "Security check: Enter current password to update password.";
                                        currentPasswordEditText.requestFocus();
                                    } else if (task.getException() != null && task.getException().getMessage() != null) {
                                        pwErrorMessage = "Failed to update password: " + task.getException().getMessage();
                                    }
                                    Toast.makeText(ProfileActivity.this, pwErrorMessage, Toast.LENGTH_LONG).show();
                                    operationCompleted(false, "Password");
                                }
                            });
                });
            }
        }

        // --- Upload Profile Picture ---
        if (selectedImageUri != null) {
            anyFieldModified = true;
            pendingOperations++;
            Log.d(TAG, "Attempting to upload profile picture. Pending ops: " + pendingOperations);
            uploadProfilePictureToCloudinary(selectedImageUri);
        }

        if (!anyFieldModified && pendingOperations == 0) {
            boolean nameError = nameEditText.getError() != null;
            boolean emailError = emailEditText.getError() != null;
            boolean newPasswordError = newPasswordEditText.getError() != null;
            boolean currentPasswordError = currentPasswordEditText.getError() != null;
            // --- BIRTHDATE ---
            boolean birthdateError = birthdateEditText.getError() != null; // Though we don't set errors on it currently
            // --- BIRTHDATE ---

            if (!nameError && !emailError && !newPasswordError && !currentPasswordError && !birthdateError &&
                    TextUtils.isEmpty(newPassword) && selectedImageUri == null) {
                Toast.makeText(this, "No changes to update.", Toast.LENGTH_SHORT).show();
            } else if (anyFieldModified) {
                // This means some operation was started but failed very early
            } else if (nameError || emailError || newPasswordError || currentPasswordError || birthdateError) {
                Toast.makeText(this, "Please correct the errors before updating.", Toast.LENGTH_SHORT).show();
            }
        } else if (pendingOperations == 0 && anyFieldModified && successfulUpdates.isEmpty()) {
            Log.d(TAG, "All attempted operations failed.");
        }
    }

    private void uploadProfilePictureToCloudinary(Uri imageUri) {
        if (imageUri == null) {
            Log.e(TAG, "Image URI is null, cannot upload.");
            operationCompleted(false, "Profile Picture");
            return;
        }
        Log.d(TAG, "Uploading to Cloudinary: " + imageUri.toString());

        String requestId = MediaManager.get().upload(imageUri)
                .unsigned(CLOUDINARY_UPLOAD_PRESET)
                .option("folder", "profile_pictures")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Cloudinary upload started: " + requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG, "Cloudinary upload success: " + resultData);
                        String imageUrl = (String) resultData.get("secure_url");
                        if (imageUrl == null) {
                            imageUrl = (String) resultData.get("url");
                        }

                        if (imageUrl != null && currentUser != null) {
                            db.collection("users").document(currentUser.getUid())
                                    .update("profilePictureUrl", imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Profile picture URL updated in Firestore.");
                                        // selectedImageUri = null; // Already handled in operationCompleted
                                        operationCompleted(true, "Profile Picture");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating profile picture URL in Firestore", e);
                                        Toast.makeText(ProfileActivity.this, "Failed to save picture URL.", Toast.LENGTH_SHORT).show();
                                        operationCompleted(false, "Profile Picture");
                                    });
                        } else {
                            Log.e(TAG, "Cloudinary URL is null or user is null after upload.");
                            Toast.makeText(ProfileActivity.this, "Failed to get image URL.", Toast.LENGTH_SHORT).show();
                            operationCompleted(false, "Profile Picture");
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Cloudinary upload error: " + error.getDescription());
                        Toast.makeText(ProfileActivity.this, "Image upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                        operationCompleted(false, "Profile Picture");
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.w(TAG, "Cloudinary upload rescheduled: " + error.getDescription());
                    }
                })
                .dispatch();
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        galleryLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission is required to select images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}