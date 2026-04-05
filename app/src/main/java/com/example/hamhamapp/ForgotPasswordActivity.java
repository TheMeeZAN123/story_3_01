package com.example.hamhamapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;

/**
 * ForgotPasswordActivity.java
 *
 * Purpose: Controller (MVC) for the password-reset flow. Takes the
 * user's email and calls AuthRepository.sendPasswordResetEmail(), which
 * triggers Firebase Authentication to send a reset link. Firebase handles
 * the entire reset process externally — no additional in-app screen is
 * required after the email is sent. On success, the form is replaced by
 * a confirmation message.
 *
 * Outstanding issues: None.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    EditText  emailInput;
    Button    sendResetEmailBtn;
    ImageView backBtn;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authRepository     = new AuthRepository();
        emailInput         = findViewById(R.id.emailInput);
        sendResetEmailBtn  = findViewById(R.id.sendResetEmailBtn);
        backBtn            = findViewById(R.id.backButton);

        backBtn.setOnClickListener(v -> finish());

        sendResetEmailBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                emailInput.setError("Please enter your email");
                return;
            }
            sendResetEmailBtn.setEnabled(false);
            authRepository.sendPasswordResetEmail(email,
                    new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(String message) {
                            showSuccessMessage();
                        }
                        @Override
                        public void onError(String error) {
                            sendResetEmailBtn.setEnabled(true);
                            Toast.makeText(ForgotPasswordActivity.this,
                                    error, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    /**
     * Hides the email input and button and displays a confirmation message
     * after the password reset email has been dispatched successfully.
     */
    private void showSuccessMessage() {
        emailInput.setVisibility(View.GONE);
        sendResetEmailBtn.setVisibility(View.GONE);

        TextView msg = new TextView(this);
        msg.setText("Reset email sent! Please check your inbox.");
        msg.setTextSize(16);
        msg.setTextColor(getColor(R.color.counselor_green));
        msg.setPadding(0, 32, 0, 0);
        ((ViewGroup) emailInput.getParent()).addView(msg);
    }
}
