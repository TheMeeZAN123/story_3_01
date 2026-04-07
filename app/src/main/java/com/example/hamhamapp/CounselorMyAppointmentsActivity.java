package com.example.hamhamapp;

/**
 * CounselorMyAppointmentsActivity.java
 *
 * Purpose: Displays counselor's upcoming and past appointments.
 * Allows switching between upcoming and past tabs.
 * Upcoming appointments can be cancelled or rescheduled.
 */

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CounselorMyAppointmentsActivity extends AppCompatActivity {

    ImageView backButton;
    TextView tabUpcoming, tabPast;
    ListView upcomingList, pastList;
    LinearLayout emptyStateAppointments;
    String email;

    private FirebaseFirestore db;
    private CounselorAppointmentAdapter upcomingAdapter, pastAdapter;
    private List<Appointment> upcomingAppts, pastAppts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_my_appointments);

        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabPast = findViewById(R.id.tabPast);
        upcomingList = findViewById(R.id.upcomingList);
        pastList = findViewById(R.id.pastList);
        emptyStateAppointments = findViewById(R.id.emptyStateAppointments);

        db = FirebaseFirestore.getInstance();
        upcomingAppts = new ArrayList<>();
        pastAppts = new ArrayList<>();

        upcomingAdapter = new CounselorAppointmentAdapter(this, upcomingAppts, true);
        pastAdapter = new CounselorAppointmentAdapter(this, pastAppts, false);

        upcomingList.setAdapter(upcomingAdapter);
        pastList.setAdapter(pastAdapter);

        // back button
        backButton.setOnClickListener(v -> finish());

        // tab switching
        tabUpcoming.setOnClickListener(v -> showUpcoming());
        tabPast.setOnClickListener(v -> showPast());

        // default: show upcoming tab
        showUpcoming();
        
        loadAppointments();
    }

    private void loadAppointments() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        db.collection("appointments")
                .whereEqualTo("counselorId", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    upcomingAppts.clear();
                    pastAppts.clear();
                    long now = System.currentTimeMillis();
                    
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Appointment appt = doc.toObject(Appointment.class);
                        appt.setId(doc.getId());
                        
                        if (appt.getStatus().equals("cancelled")) continue;

                        if (appt.getTimestamp() >= now) {
                            upcomingAppts.add(appt);
                        } else {
                            pastAppts.add(appt);
                        }
                    }
                    
                    // Refresh current view
                    if (upcomingList.getVisibility() == View.VISIBLE) {
                        refreshListView(upcomingAppts, upcomingList);
                    } else {
                        refreshListView(pastAppts, pastList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error loading appointments", e);
                    Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
                });
    }

    private void refreshListView(List<Appointment> list, ListView listView) {
        if (list.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyStateAppointments.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyStateAppointments.setVisibility(View.GONE);
            ((CounselorAppointmentAdapter)listView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void showUpcoming() {
        tabUpcoming.setTextColor(getColor(R.color.primary_blue));
        tabUpcoming.setTypeface(null, android.graphics.Typeface.BOLD);
        tabUpcoming.setBackground(getDrawable(R.drawable.tab_selected));
        tabPast.setTextColor(getColor(R.color.text_gray));
        tabPast.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabPast.setBackground(null);

        pastList.setVisibility(View.GONE);
        refreshListView(upcomingAppts, upcomingList);
    }

    private void showPast() {
        tabPast.setTextColor(getColor(R.color.primary_blue));
        tabPast.setTypeface(null, android.graphics.Typeface.BOLD);
        tabPast.setBackground(getDrawable(R.drawable.tab_selected));
        tabUpcoming.setTextColor(getColor(R.color.text_gray));
        tabUpcoming.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabUpcoming.setBackground(null);

        upcomingList.setVisibility(View.GONE);
        refreshListView(pastAppts, pastList);
    }
}