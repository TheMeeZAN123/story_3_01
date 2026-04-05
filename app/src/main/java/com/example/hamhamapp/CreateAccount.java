package com.example.hamhamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;

/**
 * CreateAccount.java
 *
 * Purpose: Controller (MVC) for new student self-registration.
 * Collects name, email, password, and confirm-password. Validates
 * all inputs locally before delegating to AuthRepository, which creates
 * a Firebase Auth account and writes a Firestore student document.
 * On success, navigates to StudentDashboardActivity and clears the
 * back stack so Back does not return here. Only students self-register;
 * counselor and admin accounts are created by the Admin.
 *
 * Outstanding issues: None.
 */
public class CreateAccount extends AppCompatActivity {

    EditText    nameInput, emailInput, passwordInput, confirmPasswordInput;
    Button      registerButton;
    TextView    loginLink;
    ProgressBar progressBar;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        authRepository = new AuthRepository();

        nameInput            = findViewById(R.id.nameInput);
        emailInput           = findViewById(R.id.emailInput);
        passwordInput        = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton       = findViewById(R.id.createAccountBtn);
        loginLink            = findViewById(R.id.signInLink);
        progressBar          = findViewById(R.id.progressBar);

        loginLink.setOnClickListener(v -> finish());

        registerButton.setOnClickListener(v -> attemptRegistration());
    }

    /**
     * Validates all input fields. On success delegates to AuthRepository.registerStudent().
     * Shows inline field errors for invalid inputs without making a network call.
     */
    private void attemptRegistration() {
        String name            = nameInput.getText().toString().trim();
        String email           = emailInput.getText().toString().trim();
        String password        = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

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
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        setLoading(true);

        authRepository.registerStudent(name, email, password,
                new AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String uid) {
                        setLoading(false);
                        Toast.makeText(CreateAccount.this,
                                "Account created successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateAccount.this,
                                StudentDashboardActivity.class);
                        intent.putExtra("email", email);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        setLoading(false);
                        Toast.makeText(CreateAccount.this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Toggles loading state — shows ProgressBar and disables the register
     * button while a Firebase request is in flight.
     *
     * @param loading true to show loading indicator.
     */
    private void setLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        registerButton.setEnabled(!loading);
    }
}
