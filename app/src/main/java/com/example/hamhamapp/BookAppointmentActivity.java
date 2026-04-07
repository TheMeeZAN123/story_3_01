package com.example.hamhamapp;

/**
 * BookAppointmentActivity.java
 * Purpose: Displays a selected counselor's profile and all
 * time slots (available and booked) for booking or waitlisting.
 * Student selects a slot and proceeds to BookingConfirmationActivity.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookAppointmentActivity extends AppCompatActivity {

    ImageView backButton;
    ListView timeSlotsList;
    TextView counselorName, counselorRating, counselorDescription;
    LinearLayout emptyState;
    String email, counselorId;

    private FirebaseFirestore db;
    private TimeSlotAdapter adapter;
    private List<TimeSlot> allSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // get data passed from previous screen
        email = getIntent().getStringExtra("email");
        counselorId = getIntent().getStringExtra("counselorId");

        // initialize views
        backButton = findViewById(R.id.backButton);
        timeSlotsList = findViewById(R.id.timeSlotsList);
        counselorName = findViewById(R.id.counselorName);
        counselorRating = findViewById(R.id.counselorRating);
        counselorDescription = findViewById(R.id.counselorDescription);
        emptyState = findViewById(R.id.emptyState);

        // initialize Firebase
        db = FirebaseFirestore.getInstance();

        // initialize list and adapter
        allSlots = new ArrayList<>();
        adapter = new TimeSlotAdapter(this, allSlots);
        timeSlotsList.setAdapter(adapter);

        // back button
        backButton.setOnClickListener(v -> finish());

        // Load counselor info and slots from Firebase
        loadCounselorInfo();
        loadAllSlots();

        timeSlotsList.setOnItemClickListener((parent, view, position, id) -> {
            TimeSlot selectedSlot = allSlots.get(position);
            Intent intent = new Intent(BookAppointmentActivity.this, BookingConfirmationActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("counselorId", counselorId);
            intent.putExtra("counselorName", counselorName.getText().toString());
            intent.putExtra("selectedDate", selectedSlot.getDate());
            intent.putExtra("selectedTime", selectedSlot.getTime());
            intent.putExtra("slotId", selectedSlot.getId());
            intent.putExtra("isBooked", selectedSlot.isBooked()); // pass status for waitlist logic
            startActivity(intent);
        });
    }

    private void loadCounselorInfo() {
        if (counselorId == null) return;

        db.collection("users").document(counselorId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Counselor counselor = documentSnapshot.toObject(Counselor.class);
                        if (counselor != null) {
                            counselorName.setText(counselor.getName());
                            counselorRating.setText(String.format("%.1f", counselor.getRating()));
                            counselorDescription.setText(counselor.getDescription());
                        }
                    }
                })
                .addOnFailureListener(e -> 
                        Toast.makeText(this, "Failed to load counselor info", Toast.LENGTH_SHORT).show());
    }

    private void loadAllSlots() {
        if (counselorId == null) return;

        // Fetch all slots for this counselor to show both available and booked (for waitlist)
        db.collection("availability")
                .whereEqualTo("counselorId", counselorId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    allSlots.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        TimeSlot slot = doc.toObject(TimeSlot.class);
                        slot.setId(doc.getId());
                        allSlots.add(slot);
                    }
                    
                    // Sort client-side
                    Collections.sort(allSlots, (s1, s2) -> {
                        int dateComp = s1.getDate().compareTo(s2.getDate());
                        if (dateComp != 0) return dateComp;
                        return s1.getTime().compareTo(s2.getTime());
                    });

                    if (allSlots.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        timeSlotsList.setVisibility(View.GONE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                        timeSlotsList.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error loading slots", e);
                    Toast.makeText(this, "Failed to load time slots", Toast.LENGTH_SHORT).show();
                    emptyState.setVisibility(View.VISIBLE);
                });
    }
}