package com.example.hamhamapp;

/**
 * AdminDashboardActivity.java
 *
 * Purpose: Main home screen for admin users after login.
 * Provides navigation to Manage Counselors, View All Appointments,
 * and Flagged Students. Also has quick actions for registering
 * new counselors and reviewing flagged accounts.
 * Email is received via Intent from MainActivity.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - All data is placeholder for UI testing
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    LinearLayout cardManageCounselors, cardViewAllAppointments, cardFlaggedStudents, actionRegisterCounselor, actionReviewFlagged;
    String email;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // get email passed from login
        email = getIntent().getStringExtra("email");

        // initialize views
        cardManageCounselors = findViewById(R.id.cardManageCounselors);
        cardViewAllAppointments = findViewById(R.id.cardViewAllAppointments);
        cardFlaggedStudents = findViewById(R.id.cardFlaggedStudents);
        actionRegisterCounselor = findViewById(R.id.actionRegisterCounselor);
        actionReviewFlagged = findViewById(R.id.actionReviewFlagged);
        logoutBtn= findViewById(R.id.logoutBtn);

        // manage counselors
        cardManageCounselors.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminManageCounselorsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // quick action: register new counselor
        actionRegisterCounselor.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminAddCounselorActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // logout : go back to main activity
        logoutBtn.setOnClickListener(v->{
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}