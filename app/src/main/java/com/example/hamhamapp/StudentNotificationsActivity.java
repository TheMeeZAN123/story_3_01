package com.example.hamhamapp;

/**
 * NotificationsActivity.java
 *
 * Purpose: Displays a list of all notifications for the current user.
 * Used by both students and counselors. Shows appointment reminders,
 * confirmations, cancellations and rescheduling notifications.
 * Shows empty state when no notifications exist.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Notifications are placeholder for UI testing
 * - Adapter for ListView not yet implemented
 */

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentNotificationsActivity extends AppCompatActivity {

    ImageView backButton;
    ListView notificationsList;
    LinearLayout emptyState;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_notifications);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        notificationsList = findViewById(R.id.notificationsList);
        emptyState = findViewById(R.id.emptyState);

        // back button
        backButton.setOnClickListener(v -> finish());

        // TODO: load notifications from Firestore using email
        // TODO: set up NotificationsAdapter for ListView

        // for now show empty state since no data yet
        notificationsList.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }
}