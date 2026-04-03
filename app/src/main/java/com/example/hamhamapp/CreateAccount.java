package com.example.hamhamapp;

/**
 * RegisterActivity.java
 *
 * Purpose: Handles new student account registration. Collects
 * name, email and password from the user. Navigates to
 * StudentDashboardActivity on successful registration.
 * Only accessible to students — counselor and admin accounts
 * are created by the admin.
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {

    EditText nameInput, emailInput, passwordInput;
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
        registerButton = findViewById(R.id.createAccountBtn);
        loginLink = findViewById(R.id.signInLink);

        // back to login
        loginLink.setOnClickListener(v -> {
            finish();
        });

//        // register button
//        // TODO: replace with Firebase Authentication when connected
//        registerButton.setOnClickListener(v -> {
//            String email = emailInput.getText().toString().trim();
//            String name = nameInput.getText().toString().trim();
//
//            Intent intent = new Intent(RegisterActivity.this, StudentDashboardActivity.class);
//            intent.putExtra("email", email);
//            intent.putExtra("name", name);
//            startActivity(intent);
//        });
    }
}