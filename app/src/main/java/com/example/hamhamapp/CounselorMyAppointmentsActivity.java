package com.example.hamhamapp;

/**
 * CounselorMyAppointmentsActivity.java
 *
 * Purpose: Displays counselor's upcoming and past appointments.
 * Allows switching between upcoming and past tabs.
 * Upcoming appointments can be cancelled or rescheduled.
 * Counselor can view student profile from each appointment card.
 * Shows pending confirmation status for unconfirmed appointments.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Appointment data is placeholder for UI testing
 * - Cancel and reschedule functionality not yet implemented
 * - Adapters for ListViews not yet implemented
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CounselorMyAppointmentsActivity extends AppCompatActivity {

    ImageView backButton;
    TextView tabUpcoming, tabPast;
    ListView upcomingList, pastList;
    LinearLayout emptyStateAppointments;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_my_appointments);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabPast = findViewById(R.id.tabPast);
        upcomingList = findViewById(R.id.upcomingList);
        pastList = findViewById(R.id.pastList);
        emptyStateAppointments = findViewById(R.id.emptyStateAppointments);

        // back button
        backButton.setOnClickListener(v -> finish());

        // default: show upcoming tab
        showUpcoming();

        // tab switching
        tabUpcoming.setOnClickListener(v -> showUpcoming());
        tabPast.setOnClickListener(v -> showPast());

        // TODO: load upcoming appointments from Firestore
        // TODO: load past appointments from Firestore
        // TODO: set up CounselorAppointmentAdapter for both ListViews

    }

    private void showUpcoming() {
        // update tab styles
        tabUpcoming.setTextColor(getColor(R.color.primary_blue));
        tabUpcoming.setTypeface(null, android.graphics.Typeface.BOLD);
        tabUpcoming.setBackground(getDrawable(R.drawable.tab_selected));
        tabPast.setTextColor(getColor(R.color.text_gray));
        tabPast.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabPast.setBackground(null);

        // hide past list always
        pastList.setVisibility(View.GONE);

        // TODO: replace this with actual data check when Firebase connected
        // for now always show empty state since no data yet
        boolean hasUpcoming = false; // change to true when data exists
        if (hasUpcoming) {
            upcomingList.setVisibility(View.VISIBLE);
            emptyStateAppointments.setVisibility(View.GONE);
        } else {
            upcomingList.setVisibility(View.GONE);
            emptyStateAppointments.setVisibility(View.VISIBLE);
        }
    }

    private void showPast() {
        // update tab styles
        tabPast.setTextColor(getColor(R.color.primary_blue));
        tabPast.setTypeface(null, android.graphics.Typeface.BOLD);
        tabPast.setBackground(getDrawable(R.drawable.tab_selected));
        tabUpcoming.setTextColor(getColor(R.color.text_gray));
        tabUpcoming.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabUpcoming.setBackground(null);

        // hide upcoming list always
        upcomingList.setVisibility(View.GONE);

        // TODO: replace this with actual data check when Firebase connected
        // for now always show empty state since no data yet
        boolean hasPast = false; // change to true when data exists
        if (hasPast) {
            pastList.setVisibility(View.VISIBLE);
            emptyStateAppointments.setVisibility(View.GONE);
        } else {
            pastList.setVisibility(View.GONE);
            emptyStateAppointments.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState() {
        emptyStateAppointments.setVisibility(View.VISIBLE);
        upcomingList.setVisibility(View.GONE);
        pastList.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        emptyStateAppointments.setVisibility(View.GONE);
    }
}