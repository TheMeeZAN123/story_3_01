package com.example.hamhamapp;

/**
 * BookingConfirmationActivity.java
 *
 * Purpose: Displays appointment details for student to review
 * before confirming a booking. Shows counselor name, selected
 * date and time. On confirmation, creates an appointment
 * document in Firestore. Cancel returns to previous screen.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Appointment details received via Intent from BookAppointmentActivity
 * - Booking confirmation not yet saved to Firestore
 * - Notification not yet triggered on booking confirmation
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookingConfirmationActivity extends AppCompatActivity {

    ImageView backButton;
    TextView confirmCounselorName, confirmDate, confirmTime;
    Button confirmBookingBtn, cancelBookingBtn;
    String email, counselorId, counselorName, selectedDate, selectedTime;

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

        // initialize views
        backButton = findViewById(R.id.backButton);
        confirmCounselorName = findViewById(R.id.confirmCounselorName);
        confirmDate = findViewById(R.id.confirmDate);
        confirmTime = findViewById(R.id.confirmTime);
        confirmBookingBtn = findViewById(R.id.confirmBookingBtn);
        cancelBookingBtn = findViewById(R.id.cancelBookingBtn);

        // set appointment details
        // use placeholder if data not passed yet
        confirmCounselorName.setText(counselorName != null ? counselorName : "Dr. Sarah Johnson");
        confirmDate.setText(selectedDate != null ? selectedDate : "2026-03-10");
        confirmTime.setText(selectedTime != null ? selectedTime : "10:00 AM");

        // back button
        backButton.setOnClickListener(v -> finish());

        // cancel button → go back
        cancelBookingBtn.setOnClickListener(v -> finish());

        // confirm booking button
        confirmBookingBtn.setOnClickListener(v -> {
            // TODO: create appointment document in Firestore
            // Map<String, Object> appointment = new HashMap<>();
            // appointment.put("studentId", email);
            // appointment.put("counselorId", counselorId);
            // appointment.put("date", selectedDate);
            // appointment.put("time", selectedTime);
            // appointment.put("status", "upcoming");
            // appointment.put("isConfirmed", false);
            // FirebaseFirestore.getInstance()
            //     .collection("appointments")
            //     .add(appointment)
            //     .addOnSuccessListener(ref -> {
            //         // TODO: trigger notification
            //         navigateToSuccess();
            //     });

            // for now just navigate to student dashboard
            navigateToSuccess();
        });
    }

    /**
     * Navigates to student dashboard after successful booking.
     * Clears back stack so student cannot go back to confirmation.
     */
    private void navigateToSuccess() {
        Intent intent = new Intent(BookingConfirmationActivity.this, StudentDashboardActivity.class);
        intent.putExtra("email", email);
        // clear back stack so back button doesn't return to confirmation
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}