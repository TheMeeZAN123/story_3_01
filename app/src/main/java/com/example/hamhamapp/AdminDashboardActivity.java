package com.example.hamhamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;

/**
 * AdminDashboardActivity.java
 *
 * Purpose: Home screen Controller (MVC) for authenticated admin users.
 * Provides navigation to Manage Counselors and a quick action to
 * register a new counselor. Logout calls AuthRepository.logout() and
 * clears the Activity back stack.
 *
 * Outstanding issues:
 * - View All Appointments card not yet connected to an Activity.
 * - Flagged Students card not yet connected to an Activity.
 * - Dashboard stats (total counselors, appointments today) not yet
 *   loaded from Firestore.
 */
public class AdminDashboardActivity extends AppCompatActivity {

    LinearLayout cardManageCounselors, cardViewAllAppointments,
            cardFlaggedStudents, actionRegisterCounselor, actionReviewFlagged;
    Button       logoutBtn;
    String       email;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        authRepository = new AuthRepository();
        email          = getIntent().getStringExtra("email");

        cardManageCounselors    = findViewById(R.id.cardManageCounselors);
        cardViewAllAppointments = findViewById(R.id.cardViewAllAppointments);
        cardFlaggedStudents     = findViewById(R.id.cardFlaggedStudents);
        actionRegisterCounselor = findViewById(R.id.actionRegisterCounselor);
        actionReviewFlagged     = findViewById(R.id.actionReviewFlagged);
        logoutBtn               = findViewById(R.id.logoutBtn);

        cardManageCounselors.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageCounselorsActivity.class)
                        .putExtra("email", email)));

        actionRegisterCounselor.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAddCounselorActivity.class)
                        .putExtra("email", email)));

        logoutBtn.setOnClickListener(v ->
                authRepository.logout(new AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        startActivity(new Intent(AdminDashboardActivity.this,
                                MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminDashboardActivity.this,
                                error, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
