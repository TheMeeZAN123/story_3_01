package com.example.hamhamapp;

/**
 * AdminEditCounselorActivity.java
 *
 * Purpose: Displays action options for a selected counselor.
 * Admin can edit counselor info or toggle active/inactive status.
 * Counselor ID is passed via Intent from ManageCounselorsActivity.
 */

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class AdminEditCounselorActivity extends AppCompatActivity {

    ImageView backButton;
    LinearLayout actionEditInfo, actionToggleStatus;
    TextView toggleStatusText, counselorStatus, counselorName, counselorEmail;
    String email, counselorId;
    boolean isActive = true;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_counselor);

        db = FirebaseFirestore.getInstance();

        // get data passed from previous screen
        email = getIntent().getStringExtra("email");
        counselorId = getIntent().getStringExtra("counselorId");

        // initialize views
        backButton = findViewById(R.id.backButton);
        actionEditInfo = findViewById(R.id.actionEditInfo);
        actionToggleStatus = findViewById(R.id.actionToggleStatus);
        toggleStatusText = findViewById(R.id.toggleStatusText);
        counselorStatus = findViewById(R.id.counselorStatus);
        counselorName = findViewById(R.id.counselorName);
        counselorEmail = findViewById(R.id.counselorEmail);

        // back button
        backButton.setOnClickListener(v -> finish());

        // fetch counselor data from Firestore
        loadCounselorData();

        // edit counselor info
        actionEditInfo.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEditCounselorActivity.this, AdminAddCounselorActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("counselorId", counselorId);
            intent.putExtra("isEdit", true);
            startActivity(intent);
        });

        // toggle active/inactive with confirmation dialog
        actionToggleStatus.setOnClickListener(v -> showToggleConfirmation());
    }

    private void loadCounselorData() {
        if (counselorId == null) return;

        db.collection("users").document(counselorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String mail = documentSnapshot.getString("email");
                        Boolean active = documentSnapshot.getBoolean("isActive");
                        
                        if (active == null) active = true; // default to true if field missing
                        isActive = active;

                        counselorName.setText(name);
                        counselorEmail.setText(mail);
                        updateStatusUI();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading counselor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateStatusUI() {
        if (isActive) {
            toggleStatusText.setText("Mark as Inactive");
            counselorStatus.setText("Active");
            counselorStatus.setTextColor(getResources().getColor(R.color.counselor_green, null));
        } else {
            toggleStatusText.setText("Mark as Active");
            counselorStatus.setText("Inactive");
            counselorStatus.setTextColor(getResources().getColor(R.color.text_gray, null));
        }
    }

    /**
     * Shows confirmation dialog before toggling counselor status.
     * Prevents accidental status changes.
     */
    private void showToggleConfirmation() {
        String action = isActive ? "mark as inactive" : "mark as active";
        new AlertDialog.Builder(this)
                .setTitle("Confirm Status Change")
                .setMessage("Are you sure you want to " + action + " this counselor?")
                .setPositiveButton("Yes", (dialog, which) -> toggleStatus())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Toggles counselor active/inactive status in Firestore.
     */
    private void toggleStatus() {
        boolean nextState = !isActive;

        db.collection("users").document(counselorId)
                .update("isActive", nextState)
                .addOnSuccessListener(aVoid -> {
                    isActive = nextState;
                    updateStatusUI();
                    Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCounselorData();
    }
}
