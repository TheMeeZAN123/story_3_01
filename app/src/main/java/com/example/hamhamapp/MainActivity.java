package com.example.hamhamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;

/**
 * MainActivity.java
 *
 * Purpose: Entry point (Controller, MVC) of the HamhamApp. Handles login
 * for all three roles — Student, Counselor, Admin — using Firebase Auth via
 * AuthRepository. The selected role tab is used only for UI (showing/hiding
 * the Create Account link); actual routing is determined by the role field
 * fetched from Firestore after successful authentication. This prevents
 * users from accessing the wrong dashboard by tapping a different role tab.
 *
 * Outstanding issues: None.
 */
public class MainActivity extends AppCompatActivity {

    LinearLayout roleStudent, roleCounselor, roleAdmin;
    TextView     forgotPassword, createAccount, createAccountMessage;
    EditText     emailInput, passwordInput;
    Button       signInButton;
    ProgressBar  progressBar;
    String       selectedRole = "student";

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authRepository = new AuthRepository();

        // if already signed in skip straight to the dashboard
        if (authRepository.getCurrentUser() != null) {
            routeSignedInUser();
            return;
        }

        // initialize views
        roleStudent          = findViewById(R.id.role_student_button);
        roleCounselor        = findViewById(R.id.role_counselor_button);
        roleAdmin            = findViewById(R.id.role_admin_button);
        forgotPassword       = findViewById(R.id.forgotPassword);
        createAccount        = findViewById(R.id.createAccountLink);
        createAccountMessage = findViewById(R.id.createAccountStr);
        emailInput           = findViewById(R.id.emailInput);
        passwordInput        = findViewById(R.id.passwordInput);
        signInButton         = findViewById(R.id.sign_in_button);
        progressBar          = findViewById(R.id.progressBar);

        // default selection — student
        applyRoleSelection("student");

        roleStudent.setOnClickListener(v   -> applyRoleSelection("student"));
        roleCounselor.setOnClickListener(v -> applyRoleSelection("counselor"));
        roleAdmin.setOnClickListener(v     -> applyRoleSelection("admin"));

        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        createAccount.setOnClickListener(v ->
                startActivity(new Intent(this, CreateAccount.class)));

        signInButton.setOnClickListener(v -> attemptLogin());
    }

    /**
     * Highlights the selected role tab and shows / hides the Create Account
     * link (only visible for students).
     *
     * @param role "student", "counselor", or "admin".
     */
    private void applyRoleSelection(String role) {
        selectedRole = role;
        boolean isStudent = role.equals("student");
        createAccount.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        createAccountMessage.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        roleStudent.setBackground(getDrawable(
                isStudent ? R.drawable.role_selected : R.drawable.role_not_selected));
        roleCounselor.setBackground(getDrawable(
                role.equals("counselor") ? R.drawable.role_selected : R.drawable.role_not_selected));
        roleAdmin.setBackground(getDrawable(
                role.equals("admin") ? R.drawable.role_selected : R.drawable.role_not_selected));
    }

    /**
     * Validates input fields then delegates sign-in to AuthRepository.
     * Navigates to the correct dashboard based on the role returned from Firestore.
     */
    private void attemptLogin() {
        String email    = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return;
        }

        setLoading(true);

        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String role) {
                setLoading(false);
                navigateToDashboard(role, email);
            }

            @Override
            public void onError(String error) {
                setLoading(false);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Routes an already-authenticated user to their dashboard without
     * requiring them to log in again. Fetches role from Firestore via login().
     */
    private void routeSignedInUser() {
        String email = authRepository.getCurrentUser().getEmail();
        // Re-use login() with empty password — Firebase session is already valid,
        // we only need the role lookup from Firestore.
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(authRepository.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("role");
                        navigateToDashboard(role != null ? role : "student", email);
                    }
                });
    }

    /**
     * Starts the correct dashboard Activity for the given role and finishes
     * MainActivity so the back button does not return to the login screen.
     *
     * @param role  "student", "counselor", or "admin".
     * @param email Passed to the dashboard via Intent for display purposes.
     */
    private void navigateToDashboard(String role, String email) {
        Intent intent;
        if ("counselor".equals(role)) {
            intent = new Intent(this, CounselorDashboardActivity.class);
        } else if ("admin".equals(role)) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(this, StudentDashboardActivity.class);
        }
        intent.putExtra("email", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Shows or hides the ProgressBar and toggles the sign-in button
     * during an active Firebase network request.
     *
     * @param loading true while a request is in progress.
     */
    private void setLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        signInButton.setEnabled(!loading);
    }
}
