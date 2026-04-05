package com.example.hamhamapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamhamapp.AuthRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * StudentProfileStudentSide.java
 *
 * Purpose: Displays the student's own profile — name, email, phone,
 * no-show count, and account status — fetched from Firestore.
 * Provides navigation to edit profile, view appointments, and book
 * new appointments. Also exposes a Delete Account action, which
 * shows a confirmation dialog before calling AuthRepository to
 * remove the Firebase Auth account and Firestore document.
 * Follows the Controller layer of the MVC pattern.
 *
 * Outstanding issues: None.
 */
public class StudentProfileStudentSide extends AppCompatActivity {

    ImageView backButton;
    Button    editProfileBtn, deleteAccountBtn;
    TextView  studentName, studentEmail, studentPhone,
            noShowCount, accountStatus,
            viewMyAppointments, bookNewAppointment;
    String    email;

    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        authRepository = new AuthRepository();
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton          = findViewById(R.id.backButton);
        editProfileBtn      = findViewById(R.id.editProfileBtn);
        deleteAccountBtn    = findViewById(R.id.deleteAccountBtn);
        studentName         = findViewById(R.id.studentName);
        studentEmail        = findViewById(R.id.studentEmail);
        studentPhone        = findViewById(R.id.studentPhone);
        noShowCount         = findViewById(R.id.noShowCount);
        accountStatus       = findViewById(R.id.accountStatus);
        viewMyAppointments  = findViewById(R.id.viewMyAppointments);
        bookNewAppointment  = findViewById(R.id.bookNewAppointment);

        loadStudentProfile();

        backButton.setOnClickListener(v -> finish());

        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentEditProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        viewMyAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentMyAppointmentsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        bookNewAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(this, FindCounselorActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // delete account — shows confirmation dialog before proceeding
        deleteAccountBtn.setOnClickListener(v -> showDeleteConfirmation());
    }

    /**
     * Fetches the student's profile data from the Firestore "users" collection
     * and populates all TextView fields. Falls back to placeholder text on error.
     */
    private void loadStudentProfile() {
        FirebaseUser user = authRepository.getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        studentName.setText(doc.getString("name") != null
                                ? doc.getString("name") : "—");
                        studentEmail.setText(doc.getString("email") != null
                                ? doc.getString("email") : email);
                        studentPhone.setText(doc.getString("phone") != null
                                && !doc.getString("phone").isEmpty()
                                ? doc.getString("phone") : "Not set");
                        Long shows = doc.getLong("noShowCount");
                        noShowCount.setText(shows != null ? String.valueOf(shows) : "0");
                        String status = doc.getString("accountStatus");
                        accountStatus.setText(status != null ? status : "active");
                    }
                })
                .addOnFailureListener(e -> {
                    studentEmail.setText(email);
                    studentName.setText("—");
                });
    }

    /**
     * Shows an AlertDialog asking the student to confirm account deletion.
     * On confirmation, delegates to AuthRepository.deleteStudentAccount(),
     * then redirects to MainActivity and clears the back stack.
     */
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete account")
                .setMessage("This will permanently delete your account and all your data. "
                        + "This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAccountBtn.setEnabled(false);
                    authRepository.deleteStudentAccount(new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(StudentProfileStudentSide.this,
                                    "Account deleted.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentProfileStudentSide.this,
                                    MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(String error) {
                            deleteAccountBtn.setEnabled(true);
                            Toast.makeText(StudentProfileStudentSide.this,
                                    error, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}