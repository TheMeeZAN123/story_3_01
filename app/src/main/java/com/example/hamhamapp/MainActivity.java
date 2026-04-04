package com.example.hamhamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * MainActivity.java
 *
 * Purpose: Entry point of the application. Handles user login for
 * all three roles (Student, Counselor, Admin) and navigates to
 * the appropriate dashboard based on selected role.
 */
public class MainActivity extends AppCompatActivity {
    LinearLayout roleStudent, roleCounselor, roleAdmin;
    TextView forgotPassword, createAccount, createAccountMessage;
    EditText emailInput, passwordInput;
    Button signInButton;
    String selectedRole = "student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        roleStudent = findViewById(R.id.role_student_button);
        roleCounselor = findViewById(R.id.role_counselor_button);
        roleAdmin = findViewById(R.id.role_admin_button);
        forgotPassword = findViewById(R.id.forgotPassword);
        createAccount = findViewById(R.id.createAccountLink);
        createAccountMessage= findViewById(R.id.createAccountStr);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.sign_in_button);


        // default role is student
        selectedRole = "student";
        createAccount.setVisibility(View.VISIBLE);
        roleStudent.setBackground(getDrawable(R.drawable.role_selected));

        // role selection listeners
        roleStudent.setOnClickListener(v -> {
            selectedRole = "student";
            createAccount.setVisibility(View.VISIBLE);
            createAccountMessage.setVisibility(View.VISIBLE);
            roleStudent.setBackground(getDrawable(R.drawable.role_selected));
            roleCounselor.setBackground(getDrawable(R.drawable.role_not_selected));
            roleAdmin.setBackground(getDrawable(R.drawable.role_not_selected));
        });

        roleCounselor.setOnClickListener(v -> {
            selectedRole = "counselor";
            createAccount.setVisibility(View.GONE);
            createAccountMessage.setVisibility(View.GONE);
            roleStudent.setBackground(getDrawable(R.drawable.role_not_selected));
            roleCounselor.setBackground(getDrawable(R.drawable.role_selected));
            roleAdmin.setBackground(getDrawable(R.drawable.role_not_selected));
        });

        roleAdmin.setOnClickListener(v -> {
            selectedRole = "admin";
            createAccount.setVisibility(View.GONE);
            createAccountMessage.setVisibility(View.GONE);
            roleStudent.setBackground(getDrawable(R.drawable.role_not_selected));
            roleCounselor.setBackground(getDrawable(R.drawable.role_not_selected));
            roleAdmin.setBackground(getDrawable(R.drawable.role_selected));
        });

        // forgot password navigation
        forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
        });

        // create account navigation
        createAccount.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CreateAccount.class));
        });

        // sign in — routes based on selected role
        // TODO: replace with Firebase Authentication when connected
        signInButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (selectedRole.equals("student")) {
                Intent intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);

            } else if (selectedRole.equals("counselor")) {
                Intent intent = new Intent(MainActivity.this, CounselorDashboardActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);

            } else if (selectedRole.equals("admin")) {
                Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}