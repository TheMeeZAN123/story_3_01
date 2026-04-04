package com.example.hamhamapp;

/**
 * BookAppointmentActivity.java
 * Purpose: Displays a selected counselor's profile and available
 * time slots for booking. Student selects a time slot and proceeds
 * to BookingConfirmationActivity.
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class BookAppointmentActivity extends AppCompatActivity {

    ImageView backButton;
    ListView timeSlotsList;
    String email, counselorId;

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

        // back button
        backButton.setOnClickListener(v -> finish());

        // TODO: load counselor info from Firebase
        // TODO: load available time slots from Firebase
        // TODO: set up TimeSlotAdapter for ListView

        timeSlotsList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(BookAppointmentActivity.this, BookingConfirmationActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("counselorId", counselorId);
            // TODO: pass actual counselor name and selected slot details
            intent.putExtra("counselorName", "Dr. Sarah Johnson");
            intent.putExtra("selectedDate", "2026-03-10");
            intent.putExtra("selectedTime", "10:00 AM");
            startActivity(intent);
        });
    }
}