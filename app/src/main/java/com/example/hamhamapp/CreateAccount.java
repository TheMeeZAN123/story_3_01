package com.example.hamhamapp;

/**
 * CreateAccount.java
 *
 * Purpose: Handles new student account registration. Collects
 * name, email, password and confirm password from the user.
 * Validates inputs before proceeding. Navigates to
 * StudentDashboardActivity on successful registration.
 * Only accessible to students — counselor and admin accounts
 * are created by the admin.
 *
 * Outstanding issues:
 * - Firebase Authentication not yet connected
 * - Registration does not create Firestore user document yet
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {

    EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    Button registerButton;
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // initialize views
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.createAccountBtn);
        loginLink = findViewById(R.id.signInLink);

        // back to login
        loginLink.setOnClickListener(v -> finish());

        // register button
        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // validation
            if (name.isEmpty()) {
                nameInput.setError("Name is required");
                return;
            }
            if (email.isEmpty()) {
                emailInput.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                passwordInput.setError("Password is required");
                return;
            }
            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                return;
            }

            // TODO: replace with Firebase Authentication when connected
            // FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            //     .addOnSuccessListener(authResult -> {
            //         // save student data to Firestore
            //         // navigate to dashboard
            //     });

            Intent intent = new Intent(CreateAccount.this, StudentDashboardActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("name", name);
            startActivity(intent);
        });
    }
}