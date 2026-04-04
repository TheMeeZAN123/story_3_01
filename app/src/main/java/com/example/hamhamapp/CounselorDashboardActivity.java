package com.example.hamhamapp;

/**
 * CounselorDashboardActivity.java
 *
 * Purpose: Main home screen for counselor users after login.
 * Displays navigation cards to View Calendar, Manage Availability,
 * Student Records and My Profile. Shows average rating and
 * today's schedule preview. Email is received via Intent from
 * MainActivity.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Average rating, today's schedule are placeholder text
 * - Welcome name not yet fetched from Firestore
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CounselorDashboardActivity extends AppCompatActivity {

    LinearLayout cardViewCalendar, cardManageAvailability, cardStudentRecords, cardMyProfile;
    ImageView notificationBell;
    TextView welcomeText, profileLink, viewAllSchedule, averageRating;
    String email;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_dashboard);

        // get email passed from login
        email = getIntent().getStringExtra("email");

        // initialize views
        cardViewCalendar = findViewById(R.id.cardViewCalendar);
        cardManageAvailability = findViewById(R.id.cardManageAvailability);
        cardStudentRecords = findViewById(R.id.cardStudentRecords);
        cardMyProfile = findViewById(R.id.cardMyProfile);
        notificationBell = findViewById(R.id.notificationBell);
        welcomeText = findViewById(R.id.welcomeText);
        profileLink = findViewById(R.id.profileLink);
        viewAllSchedule = findViewById(R.id.viewAllSchedule);
        averageRating = findViewById(R.id.averageRating);
        logoutBtn = findViewById(R.id.logoutBtn);

        // TODO: replace with actual name from Firestore
        welcomeText.setText("Welcome, Dr. !");

        // TODO: replace with actual rating from Firestore
        averageRating.setText("--");

        // notification bell not going to implement rn

        // profile link
        profileLink.setOnClickListener(v -> {
            Intent intent = new Intent(CounselorDashboardActivity.this, CounselorProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // view calendar → counselor appointments
        cardViewCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(CounselorDashboardActivity.this, CounselorMyAppointmentsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // manage availability
        cardManageAvailability.setOnClickListener(v -> {
            Intent intent = new Intent(CounselorDashboardActivity.this, ManageAvailabilityActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // student records
        cardStudentRecords.setOnClickListener(v -> {
            Intent intent = new Intent(CounselorDashboardActivity.this, StudentProfileCounselorSide.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // my profile
        cardMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(CounselorDashboardActivity.this, CounselorProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // view all schedule
        viewAllSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(CounselorDashboardActivity.this, CounselorMyAppointmentsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // view student profile from today's schedule
        // TODO: connect to actual appointment data from Firebase
        findViewById(R.id.viewProfileBtn).setOnClickListener(v -> {
            Intent intent = new Intent(CounselorDashboardActivity.this, StudentProfileCounselorSide.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // logout : go back to main activity
        logoutBtn.setOnClickListener(v->{
            Intent intent = new Intent(CounselorDashboardActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}