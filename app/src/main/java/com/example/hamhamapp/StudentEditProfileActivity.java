package com.example.hamhamapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * StudentEditProfileActivity.java
 *
 * Purpose: Allows student to edit their personal information
 * including name and phone number. Email cannot be changed
 * as it is tied to Firebase Authentication. Changes are
 * saved to Firestore when save button is clicked.
 * Includes password change functionality.
 */
public class StudentEditProfileActivity extends AppCompatActivity {

    ImageView backButton;
    EditText nameInput, phoneInput;
    EditText newPasswordInput, confirmNewPasswordInput;
    Button saveChangesBtn, cancelBtn, changePasswordBtn;
    String email;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_info);

        authRepository = new AuthRepository();
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmNewPasswordInput = findViewById(R.id.confirmNewPasswordInput);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        // back button
        backButton.setOnClickListener(v -> finish());

        // cancel button
        cancelBtn.setOnClickListener(v -> finish());

        prefillFields();

        // save changes button
        saveChangesBtn.setOnClickListener(v -> saveProfileChanges());

        // change password button
        changePasswordBtn.setOnClickListener(v -> attemptPasswordChange());
    }

    private void prefillFields() {
        if (authRepository.getCurrentUser() == null) return;
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(authRepository.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        nameInput.setText(doc.getString("name"));
                        phoneInput.setText(doc.getString("phone"));
                    }
                });
    }

    private void saveProfileChanges() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            return;
        }

        saveChangesBtn.setEnabled(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);

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
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void attemptPasswordChange() {
        String newPassword = newPasswordInput.getText().toString().trim();
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
                Toast.makeText(StudentEditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                newPasswordInput.setText("");
                confirmNewPasswordInput.setText("");
            }

            @Override
            public void onError(String error) {
                changePasswordBtn.setEnabled(true);
                Toast.makeText(StudentEditProfileActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
