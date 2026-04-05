package com.example.hamhamapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CounselorEditProfileActivity.java
 *
 * Purpose: Controller (MVC) that allows a counselor to edit their profile
 * information (name, phone, specialties, description) and change their
 * login password. Profile changes are saved to the Firestore "users"
 * collection. Password changes go through Firebase Auth via AuthRepository.
 * Fields are pre-filled with current values fetched from Firestore on load.
 * Email cannot be edited — it is the Firebase Auth login credential.
 *
 * Outstanding issues: None.
 */
public class CounselorEditProfileActivity extends AppCompatActivity {

    ImageView backButton;
    EditText  nameInput, phoneInput, specialtiesInput, descriptionInput;
    EditText  newPasswordInput, confirmNewPasswordInput;
    Button    saveChangesBtn, cancelBtn, changePasswordBtn;
    String    email;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_counselor_info);

        authRepository = new AuthRepository();
        email          = getIntent().getStringExtra("email");

        backButton            = findViewById(R.id.backButton);
        nameInput             = findViewById(R.id.nameInput);
        phoneInput            = findViewById(R.id.phoneInput);
        specialtiesInput      = findViewById(R.id.specialtiesInput);
        descriptionInput      = findViewById(R.id.descriptionInput);
        saveChangesBtn        = findViewById(R.id.saveChangesBtn);
        cancelBtn             = findViewById(R.id.cancelBtn);
        newPasswordInput      = findViewById(R.id.newPasswordInput);
        confirmNewPasswordInput = findViewById(R.id.confirmNewPasswordInput);
        changePasswordBtn     = findViewById(R.id.changePasswordBtn);

        backButton.setOnClickListener(v -> finish());
        cancelBtn.setOnClickListener(v  -> finish());

        prefillFields();

        saveChangesBtn.setOnClickListener(v -> saveProfileChanges());

        changePasswordBtn.setOnClickListener(v -> attemptPasswordChange());
    }

    /**
     * Reads current counselor data from Firestore and populates each
     * EditText so the counselor can see their existing values before editing.
     */
    private void prefillFields() {
        if (authRepository.getCurrentUser() == null) return;
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(authRepository.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    nameInput.setText(doc.getString("name"));
                    phoneInput.setText(doc.getString("phone"));
                    descriptionInput.setText(doc.getString("description"));
                    List<String> specs = (List<String>) doc.get("specialties");
                    if (specs != null) {
                        specialtiesInput.setText(TextUtils.join(", ", specs));
                    }
                });
    }

    /**
     * Validates the name field then writes updated profile data to Firestore.
     * Specialties are parsed from a comma-separated string into a List.
     */
    private void saveProfileChanges() {
        String name        = nameInput.getText().toString().trim();
        String phone       = phoneInput.getText().toString().trim();
        String specialties = specialtiesInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            return;
        }

        String[] parts = specialties.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name",        name);
        updates.put("phone",       phone);
        updates.put("description", description);
        updates.put("specialties", Arrays.asList(parts));

        saveChangesBtn.setEnabled(false);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(authRepository.getCurrentUser().getUid())
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    saveChangesBtn.setEnabled(true);
                    Toast.makeText(this,
                            "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Validates the new password fields then delegates to
     * AuthRepository.changePassword(). Clears the password fields on success.
     */
    private void attemptPasswordChange() {
        String newPassword     = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmNewPasswordInput.getText().toString().trim();

        if (newPassword.isEmpty()) {
            newPasswordInput.setError("New password is required");
            return;
        }
        if (newPassword.length() < 6) {
            newPasswordInput.setError("Password must be at least 6 characters");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmNewPasswordInput.setError("Passwords do not match");
            return;
        }

        changePasswordBtn.setEnabled(false);
        authRepository.changePassword(newPassword, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                changePasswordBtn.setEnabled(true);
                Toast.makeText(CounselorEditProfileActivity.this,
                        message, Toast.LENGTH_SHORT).show();
                newPasswordInput.setText("");
                confirmNewPasswordInput.setText("");
            }
            @Override
            public void onError(String error) {
                changePasswordBtn.setEnabled(true);
                Toast.makeText(CounselorEditProfileActivity.this,
                        error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
