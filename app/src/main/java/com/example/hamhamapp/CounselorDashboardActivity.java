package com.example.hamhamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * CounselorDashboardActivity.java
 *
 * Purpose: Home screen Controller (MVC) for authenticated counselor users.
 * Fetches the counselor's name and average rating from Firestore.
 * Provides navigation to View Calendar, Manage Availability, Student Records,
 * and My Profile. Logout calls AuthRepository.logout() and clears the
 * Activity back stack.
 *
 * Outstanding issues:
 * - Today's schedule preview not yet loaded from Firestore.
 * - Notification bell not yet implemented.
 */
public class CounselorDashboardActivity extends AppCompatActivity {

    LinearLayout cardViewCalendar, cardManageAvailability,
            cardStudentRecords, cardMyProfile;
    ImageView    notificationBell;
    TextView     welcomeText, profileLink, viewAllSchedule, averageRating;
    Button       logoutBtn;
    String       email;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_dashboard);

        authRepository = new AuthRepository();
        email          = getIntent().getStringExtra("email");

        cardViewCalendar       = findViewById(R.id.cardViewCalendar);
        cardManageAvailability = findViewById(R.id.cardManageAvailability);
        cardStudentRecords     = findViewById(R.id.cardStudentRecords);
        cardMyProfile          = findViewById(R.id.cardMyProfile);
        notificationBell       = findViewById(R.id.notificationBell);
        welcomeText            = findViewById(R.id.welcomeText);
        profileLink            = findViewById(R.id.profileLink);
        viewAllSchedule        = findViewById(R.id.viewAllSchedule);
        averageRating          = findViewById(R.id.averageRating);
        logoutBtn              = findViewById(R.id.logoutBtn);

        loadCounselorInfo();

        profileLink.setOnClickListener(v ->
                startActivity(new Intent(this, CounselorProfileActivity.class)
                        .putExtra("email", email)));

        cardViewCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CounselorMyAppointmentsActivity.class)
                        .putExtra("email", email)));

        cardManageAvailability.setOnClickListener(v ->
                startActivity(new Intent(this, ManageAvailabilityActivity.class)
                        .putExtra("email", email)));

        cardStudentRecords.setOnClickListener(v ->
                startActivity(new Intent(this, StudentProfileCounselorSide.class)
                        .putExtra("email", email)));

        cardMyProfile.setOnClickListener(v ->
                startActivity(new Intent(this, CounselorProfileActivity.class)
                        .putExtra("email", email)));

        viewAllSchedule.setOnClickListener(v ->
                startActivity(new Intent(this, CounselorMyAppointmentsActivity.class)
                        .putExtra("email", email)));

        // TODO: connect viewProfileBtn to actual today's-schedule appointment data
        findViewById(R.id.viewProfileBtn).setOnClickListener(v ->
                startActivity(new Intent(this, StudentProfileCounselorSide.class)
                        .putExtra("email", email)));

        logoutBtn.setOnClickListener(v ->
                authRepository.logout(new AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        startActivity(new Intent(CounselorDashboardActivity.this,
                                MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(CounselorDashboardActivity.this,
                                error, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    /**
     * Fetches the counselor's name and average rating from Firestore and
     * populates the welcome message and rating display. Falls back gracefully
     * if the document cannot be read.
     */
    private void loadCounselorInfo() {
        if (authRepository.getCurrentUser() == null) return;
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(authRepository.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name   = doc.getString("name");
                        Double rating = doc.getDouble("rating");
                        welcomeText.setText("Welcome, Dr. " + (name != null ? name : "") + "!");
                        averageRating.setText(rating != null
                                ? String.format("%.1f", rating) : "--");
                    }
                })
                .addOnFailureListener(e -> welcomeText.setText("Welcome!"));
    }
}
