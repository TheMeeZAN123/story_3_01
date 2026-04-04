package com.example.hamhamapp;

/**
 * StudentProfileCounselorSide.java
 *
 * Purpose: Displays a student's profile from the counselor's perspective.
 * Shows confidential student information including name, email, phone,
 * no-show count and session history. Only accessible to counselors.
 * Student ID is passed via Intent from CounselorAppointmentsActivity.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Student data is placeholder for UI testing
 * - Session history adapter not yet implemented
 * - Student ID not yet passed from appointments screen
 */

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentProfileCounselorSide extends AppCompatActivity {

    ImageView backButton;
    TextView studentName, studentEmail, studentPhone, noShowCount;
    ListView sessionHistoryList;
    LinearLayout emptySessionHistory;
    String email, studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_view_student_profile);

        // get data passed from previous screen
        email = getIntent().getStringExtra("email");
        studentId = getIntent().getStringExtra("studentId");

        // initialize views
        backButton = findViewById(R.id.backButton);
        studentName = findViewById(R.id.studentName);
        studentEmail = findViewById(R.id.studentEmail);
        studentPhone = findViewById(R.id.studentPhone);
        noShowCount = findViewById(R.id.noShowCount);
        sessionHistoryList = findViewById(R.id.sessionHistoryList);
        emptySessionHistory = findViewById(R.id.emptySessionHistory);

        // back button
        backButton.setOnClickListener(v -> finish());

        // TODO: fetch student data from Firestore using studentId
        // for now set placeholder data
        studentName.setText("Student Name");
        studentEmail.setText("student@university.edu");
        studentPhone.setText("Not set");
        noShowCount.setText("0");

        // TODO: load session history from Firestore
        // for now show empty state
        loadSessionHistory();
    }

    /**
     * Loads session history for this student.
     * Shows empty state if no sessions exist.
     * Will load from Firestore when connected.
     */
    private void loadSessionHistory() {
        // TODO: fetch session history from Firestore using studentId
        boolean hasHistory = false;

        if (hasHistory) {
            sessionHistoryList.setVisibility(View.VISIBLE);
            emptySessionHistory.setVisibility(View.GONE);
            // TODO: set up SessionHistoryAdapter
        } else {
            sessionHistoryList.setVisibility(View.GONE);
            emptySessionHistory.setVisibility(View.VISIBLE);
        }
    }
}