package com.example.hamhamapp;

/**
 * ForgotPasswordActivity.java
 *
 * Purpose: Handles password reset requests. Takes the user's email
 * and sends a password reset link via Firebase Authentication.
 * Firebase handles the entire reset process externally — no new
 * screen needed after sending the email.
 */

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailInput;
    Button sendResetEmailBtn;
    ImageView backBtn;
    TextView backToLogin;
    TextView successMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // initialize views
        emailInput = findViewById(R.id.emailInput);
        sendResetEmailBtn = findViewById(R.id.sendResetEmailBtn);
        backBtn = findViewById(R.id.backButton);

        // back button goes to login
        backBtn.setOnClickListener(v -> finish());

        // send reset email
        // TODO: replace with Firebase sendPasswordResetEmail() when connected
        sendResetEmailBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                emailInput.setError("Please enter your email");
                return;
            }

            // TODO: Firebase code goes here:
            // FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            //     .addOnCompleteListener(task -> {
            //         if (task.isSuccessful()) { showSuccess(); }
            //         else { showError(); }
            //     });

            // for now just show success message
            showSuccess();
        });
    }

    private void showSuccess() {
        emailInput.setVisibility(View.GONE);
        sendResetEmailBtn.setVisibility(View.GONE);

        // show success message
        TextView successMsg = new TextView(this);
        successMsg.setText("Reset email sent! Please check your inbox.");
        successMsg.setTextSize(16);
        successMsg.setTextColor(getColor(R.color.counselor_green));
        successMsg.setPadding(0, 32, 0, 0);

        // add to layout
        ((android.view.ViewGroup) emailInput.getParent()).addView(successMsg);
    }
}
