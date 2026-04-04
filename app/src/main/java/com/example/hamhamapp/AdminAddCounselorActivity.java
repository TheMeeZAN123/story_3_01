package com.example.hamhamapp;

/**
 * AdminAddCounselorActivity.java
 *
 * Purpose: Allows admin to register a new counselor account.
 * Admin assigns name, email, temporary password, phone,
 * specialties and description. Creates Firebase Authentication
 * account and Firestore document for the counselor.
 * Also used for editing existing counselor info when
 * isEdit flag is passed via Intent.
 *
 * Outstanding issues:
 * - Firebase Authentication not yet connected
 * - Firebase Firestore not yet connected
 * - Input validation not yet implemented
 * - Edit mode not yet implemented
 */

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminAddCounselorActivity extends AppCompatActivity {

    ImageView backButton;
    EditText nameInput, emailInput, passwordInput,
            phoneInput, specialtiesInput, descriptionInput;
    Button registerCounselorBtn, cancelBtn;
    String email, counselorId;
    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_counselor);

        // get data passed from previous screen
        email = getIntent().getStringExtra("email");
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

        // if edit mode, update title and prefill fields
        if (isEdit) {
            // TODO: fetch counselor data from Firestore using counselorId
            // TODO: prefill all fields with existing data
            registerCounselorBtn.setText("Save Changes");
        }

        // back button
        backButton.setOnClickListener(v -> finish());

        // cancel button
        cancelBtn.setOnClickListener(v -> finish());

        // register/save button
        registerCounselorBtn.setOnClickListener(v -> {
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
            if (password.isEmpty() && !isEdit) {
                passwordInput.setError("Password is required");
                return;
            }

            if (isEdit) {
                // TODO: update counselor in Firestore
            } else {
                // TODO: create Firebase Auth account for counselor
                // TODO: create Firestore document for counselor
                // FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                //     counselorEmail, password)
                //     .addOnSuccessListener(authResult -> {
                //         // save counselor data to Firestore
                //     });
            }

            // for now just go back
            finish();
        });
    }
}
