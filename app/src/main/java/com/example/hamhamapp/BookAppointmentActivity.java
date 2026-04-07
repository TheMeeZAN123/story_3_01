package com.example.hamhamapp;

/**
 * BookAppointmentActivity.java
 * Purpose: Displays a selected counselor's profile and available
 * time slots for booking. Student selects a time slot and proceeds
 * to BookingConfirmationActivity.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class BookAppointmentActivity extends AppCompatActivity {

    ImageView backButton;
    ListView timeSlotsList;
    TextView counselorName, counselorRating, counselorDescription;
    LinearLayout emptyState;
    String email, counselorId;

    private FirebaseFirestore db;

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

        // back button
        backButton.setOnClickListener(v -> finish());

        // Load counselor info from Firebase
        loadCounselorInfo();

        // TODO: load available time slots from Firebase
        // TODO: set up TimeSlotAdapter for ListView

        timeSlotsList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(BookAppointmentActivity.this, BookingConfirmationActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("counselorId", counselorId);
            // TODO: pass actual counselor name and selected slot details
            intent.putExtra("counselorName", counselorName.getText().toString());
            intent.putExtra("selectedDate", "2026-03-10");
            intent.putExtra("selectedTime", "10:00 AM");
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
}