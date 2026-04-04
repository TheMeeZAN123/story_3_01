package com.example.hamhamapp;

/**
 * StudentDashboardActivity.java
 *
 * Purpose: Main home screen for student users after login.
 * Displays navigation cards to Find Counselor, My Appointments,
 * and My Profile. Shows upcoming appointment preview and
 * recent notifications. Email is received via Intent from
 * MainActivity.
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDashboardActivity extends AppCompatActivity {

    LinearLayout cardFindCounselor, cardMyAppointments, cardMyProfile;
    TextView welcomeText, profileLink, viewAllAppointments;
    ImageView notificationBell;
    String email;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // get email passed from login
        email = getIntent().getStringExtra("email");

        // initialize views
        cardFindCounselor = findViewById(R.id.cardFindCounselor);
        cardMyAppointments = findViewById(R.id.cardMyAppointments);
        cardMyProfile = findViewById(R.id.cardMyProfile);
        welcomeText = findViewById(R.id.welcomeText);
        notificationBell = findViewById(R.id.notificationBell);
        profileLink = findViewById(R.id.profileLink);
        viewAllAppointments = findViewById(R.id.viewAllAppointments);
        logoutBtn = findViewById(R.id.logoutBtn);

        // set welcome message
        // TODO: replace with actual name from Firestore
        welcomeText.setText("Welcome, (student name)!");

        // find counselor
        cardFindCounselor.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, FindCounselorActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // my appointments
        cardMyAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentMyAppointmentsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // my profile
        cardMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentProfileStudentSide.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // notifications bell
        notificationBell.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentNotificationsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // profile link top right
        profileLink.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentProfileStudentSide.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // view all appointments
        viewAllAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentMyAppointmentsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });
//
//        // confirm appointment button
//        // TODO: connect to Firebase to confirm actual appointment
//        findViewById(R.id.confirmAppointmentBtn).setOnClickListener(v -> {
//            // placeholder for now
//        });

        // logout : go back to main activity
        logoutBtn.setOnClickListener(v->{
            Intent intent = new Intent(StudentDashboardActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
