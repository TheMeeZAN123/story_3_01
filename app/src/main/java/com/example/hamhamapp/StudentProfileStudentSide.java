package com.example.hamhamapp;

/**
 * StudentProfileStudentSide.java
 *
 * Purpose: Displays the student's personal profile information
 * including name, email, phone, account status and no-show count.
 * Provides quick actions to view appointments and book new ones.
 * Navigates to EditStudentProfileActivity for editing.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Profile data is placeholder for UI testing
 * - No-show count and account status not yet fetched from Firestore
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentProfileStudentSide extends AppCompatActivity {

    ImageView backButton;
    Button editProfileBtn;
    TextView studentName, studentEmail, studentPhone,
            noShowCount, accountStatus,
            viewMyAppointments, bookNewAppointment;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        studentName = findViewById(R.id.studentName);
        studentEmail = findViewById(R.id.studentEmail);
        studentPhone = findViewById(R.id.studentPhone);
        noShowCount = findViewById(R.id.noShowCount);
        accountStatus = findViewById(R.id.accountStatus);
        viewMyAppointments = findViewById(R.id.viewMyAppointments);
        bookNewAppointment = findViewById(R.id.bookNewAppointment);

        // TODO: fetch student data from Firestore using email
        // for now set placeholder data
        studentEmail.setText(email);
        studentName.setText("Student Name");
        studentPhone.setText("Not set");
        noShowCount.setText("0");
        accountStatus.setText("Active");

        // back button
        backButton.setOnClickListener(v -> finish());

        // edit profile
        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StudentProfileStudentSide.this, StudentEditProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // quick action: view appointments
        viewMyAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(StudentProfileStudentSide.this, StudentMyAppointmentsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // quick action: book new appointment
        bookNewAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(StudentProfileStudentSide.this, FindCounselorActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }
}