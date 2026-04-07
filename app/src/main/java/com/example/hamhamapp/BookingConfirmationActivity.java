package com.example.hamhamapp;

/**
 * BookingConfirmationActivity.java
 *
 * Purpose: Displays appointment details for student to review
 * before confirming a booking or joining a waitlist. Shows counselor name,
 * selected date and time. On confirmation, creates an appointment
 * or waitlist document in Firestore. Cancel returns to previous screen.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookingConfirmationActivity extends AppCompatActivity {

    ImageView backButton;
    TextView confirmCounselorName, confirmDate, confirmTime;
    Button confirmBookingBtn, cancelBookingBtn;
    String email, counselorId, counselorName, selectedDate, selectedTime, slotId;
    boolean isBooked;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation_screen);

        // get data passed from BookAppointmentActivity
        email = getIntent().getStringExtra("email");
        counselorId = getIntent().getStringExtra("counselorId");
        counselorName = getIntent().getStringExtra("counselorName");
        selectedDate = getIntent().getStringExtra("selectedDate");
        selectedTime = getIntent().getStringExtra("selectedTime");
        slotId = getIntent().getStringExtra("slotId");
        isBooked = getIntent().getBooleanExtra("isBooked", false);

        // initialize views
        backButton = findViewById(R.id.backButton);
        confirmCounselorName = findViewById(R.id.confirmCounselorName);
        confirmDate = findViewById(R.id.confirmDate);
        confirmTime = findViewById(R.id.confirmTime);
        confirmBookingBtn = findViewById(R.id.confirmBookingBtn);
        cancelBookingBtn = findViewById(R.id.cancelBookingBtn);

        db = FirebaseFirestore.getInstance();

        // Update UI based on whether we are booking or waitlisting
        if (isBooked) {
            confirmBookingBtn.setText("Join Waitlist");
        } else {
            confirmBookingBtn.setText("Confirm Booking");
        }

        // set appointment details
        confirmCounselorName.setText(counselorName != null ? counselorName : "Dr. Counselor");
        confirmDate.setText(selectedDate != null ? selectedDate : "");
        confirmTime.setText(selectedTime != null ? selectedTime : "");

        // back button
        backButton.setOnClickListener(v -> finish());

        // cancel button → go back
        cancelBookingBtn.setOnClickListener(v -> finish());

        // confirm booking / join waitlist button
        confirmBookingBtn.setOnClickListener(v -> {
            if (isBooked) {
                joinWaitlist();
            } else {
                confirmBooking();
            }
        });
    }

    private void confirmBooking() {
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("studentEmail", email);
        appointment.put("counselorId", counselorId);
        appointment.put("counselorName", counselorName);
        appointment.put("date", selectedDate);
        appointment.put("time", selectedTime);
        appointment.put("slotId", slotId);
        appointment.put("status", "upcoming");
        appointment.put("timestamp", System.currentTimeMillis());

        db.collection("appointments").add(appointment)
                .addOnSuccessListener(documentReference -> {
                    // Mark the slot as booked
                    db.collection("availability").document(slotId)
                            .update("isBooked", true)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Appointment Booked Successfully!", Toast.LENGTH_SHORT).show();
                                navigateToSuccess();
                            });
                })
                .addOnFailureListener(e -> 
                        Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void joinWaitlist() {
        Map<String, Object> waitlistEntry = new HashMap<>();
        waitlistEntry.put("studentEmail", email);
        waitlistEntry.put("counselorId", counselorId);
        waitlistEntry.put("slotId", slotId);
        waitlistEntry.put("timestamp", System.currentTimeMillis());
        waitlistEntry.put("status", "waiting");

        db.collection("waitlist").add(waitlistEntry)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "You have joined the waitlist!", Toast.LENGTH_SHORT).show();
                    navigateToSuccess();
                })
                .addOnFailureListener(e -> 
                        Toast.makeText(this, "Waitlist failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Navigates to student dashboard after successful booking or waitlist.
     * Clears back stack so student cannot go back to confirmation.
     */
    private void navigateToSuccess() {
        Intent intent = new Intent(BookingConfirmationActivity.this, StudentDashboardActivity.class);
        intent.putExtra("email", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}