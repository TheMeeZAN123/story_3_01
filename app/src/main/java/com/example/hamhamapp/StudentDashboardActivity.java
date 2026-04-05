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
 * StudentDashboardActivity.java
 *
 * Purpose: Home screen Controller (MVC) for authenticated student users.
 * Fetches the student's display name from Firestore and populates the
 * welcome message. Provides navigation to Find Counselor, My Appointments,
 * and My Profile. Logout calls AuthRepository.logout() and clears the
 * Activity back stack so the user must log in again to return.
 *
 * Outstanding issues:
 * - Upcoming appointment preview card not yet loaded from Firestore.
 * - Notification badge count not yet fetched from Firestore.
 */
public class StudentDashboardActivity extends AppCompatActivity {

    LinearLayout cardFindCounselor, cardMyAppointments, cardMyProfile;
    TextView     welcomeText, profileLink, viewAllAppointments;
    ImageView    notificationBell;
    Button       logoutBtn;
    String       email;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        authRepository = new AuthRepository();
        email          = getIntent().getStringExtra("email");

        cardFindCounselor  = findViewById(R.id.cardFindCounselor);
        cardMyAppointments = findViewById(R.id.cardMyAppointments);
        cardMyProfile      = findViewById(R.id.cardMyProfile);
        welcomeText        = findViewById(R.id.welcomeText);
        notificationBell   = findViewById(R.id.notificationBell);
        profileLink        = findViewById(R.id.profileLink);
        viewAllAppointments = findViewById(R.id.viewAllAppointments);
        logoutBtn          = findViewById(R.id.logoutBtn);

        loadStudentName();

        cardFindCounselor.setOnClickListener(v ->
                startActivity(new Intent(this, FindCounselorActivity.class)
                        .putExtra("email", email)));

        cardMyAppointments.setOnClickListener(v ->
                startActivity(new Intent(this, StudentMyAppointmentsActivity.class)
                        .putExtra("email", email)));

        cardMyProfile.setOnClickListener(v ->
                startActivity(new Intent(this, StudentProfileStudentSide.class)
                        .putExtra("email", email)));

        notificationBell.setOnClickListener(v ->
                startActivity(new Intent(this, StudentNotificationsActivity.class)
                        .putExtra("email", email)));

        profileLink.setOnClickListener(v ->
                startActivity(new Intent(this, StudentProfileStudentSide.class)
                        .putExtra("email", email)));

        viewAllAppointments.setOnClickListener(v ->
                startActivity(new Intent(this, StudentMyAppointmentsActivity.class)
                        .putExtra("email", email)));

        logoutBtn.setOnClickListener(v ->
                authRepository.logout(new AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        startActivity(new Intent(StudentDashboardActivity.this,
                                MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(StudentDashboardActivity.this,
                                error, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    /**
     * Fetches the student's name from Firestore and updates the welcome TextView.
     * Falls back to showing the email address if the document cannot be read.
     */
    private void loadStudentName() {
        if (authRepository.getCurrentUser() == null) return;
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(authRepository.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String name = doc.exists() ? doc.getString("name") : null;
                    welcomeText.setText("Welcome, " + (name != null ? name : email) + "!");
                })
                .addOnFailureListener(e -> welcomeText.setText("Welcome!"));
    }
}
