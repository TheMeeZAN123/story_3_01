package com.example.hamhamapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminAddCounselorActivity.java
 *
 * Purpose: Allows admin to register a new counselor account.
 * Admin assigns name, email, temporary password, phone,
 * specialties and description. Creates Firebase Authentication
 * account and Firestore document for the counselor via AuthRepository.
 * Also used for editing existing counselor info when
 * isEdit flag is passed via Intent.
 */
public class AdminAddCounselorActivity extends AppCompatActivity {

    ImageView backButton;
    EditText nameInput, emailInput, passwordInput,
            phoneInput, specialtiesInput, descriptionInput;
    Button registerCounselorBtn, cancelBtn;
    ProgressBar progressBar;
    String counselorId;
    boolean isEdit;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_counselor);

        authRepository = new AuthRepository();

        // get data passed from previous screen
        counselorId = getIntent().getStringExtra("counselorId");
        isEdit = getIntent().getBooleanExtra("isEdit", false);

        // initialize views
        backButton = findViewById(R.id.backButton);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        phoneInput = findViewById(R.id.phoneInput);
        specialtiesInput = findViewById(R.id.specialtiesInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        registerCounselorBtn = findViewById(R.id.registerCounselorBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        // Assuming there might be a progress bar in layout, if not we ignore or add one.
        // For now let's just use the buttons enabled state.

        // if edit mode, update title and prefill fields
        if (isEdit && counselorId != null) {
            registerCounselorBtn.setText("Save Changes");
            emailInput.setEnabled(false); // Cannot change email in edit mode
            passwordInput.setVisibility(View.GONE); // Hide password input in edit mode
            findViewById(android.R.id.content).findViewWithTag("passwordHint"); // just an example
            
            loadCounselorData();
        }

        // back button
        backButton.setOnClickListener(v -> finish());

        // cancel button
        cancelBtn.setOnClickListener(v -> finish());

        // register/save button
        registerCounselorBtn.setOnClickListener(v -> attemptSave());
    }

    private void loadCounselorData() {
        FirebaseFirestore.getInstance().collection("users").document(counselorId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        nameInput.setText(doc.getString("name"));
                        emailInput.setText(doc.getString("email"));
                        phoneInput.setText(doc.getString("phone"));
                        descriptionInput.setText(doc.getString("description"));
                        List<String> specs = (List<String>) doc.get("specialties");
                        if (specs != null) {
                            specialtiesInput.setText(String.join(", ", specs));
                        }
                    }
                });
    }

    private void attemptSave() {
        String name = nameInput.getText().toString().trim();
        String counselorEmail = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String specialties = specialtiesInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        // basic validation
        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            return;
        }
        if (counselorEmail.isEmpty()) {
            emailInput.setError("Email is required");
            return;
        }
        if (!isEdit && password.isEmpty()) {
            passwordInput.setError("Password is required");
            return;
        }
        if (!isEdit && password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        registerCounselorBtn.setEnabled(false);

        if (isEdit) {
            updateCounselor(name, phone, specialties, description);
        } else {
            authRepository.registerCounselor(name, counselorEmail, password, phone, specialties, description,
                    new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(AdminAddCounselorActivity.this, "Counselor registered successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(String error) {
                            registerCounselorBtn.setEnabled(true);
                            Toast.makeText(AdminAddCounselorActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void updateCounselor(String name, String phone, String specialties, String description) {
        String[] parts = specialties.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("description", description);
        updates.put("specialties", Arrays.asList(parts));

        FirebaseFirestore.getInstance().collection("users").document(counselorId)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Counselor updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    registerCounselorBtn.setEnabled(true);
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
