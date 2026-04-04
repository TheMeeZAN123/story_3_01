package com.example.hamhamapp;

/**
 * AdminEditCounselorActivity.java
 *
 * Purpose: Displays action options for a selected counselor.
 * Admin can edit counselor info or toggle active/inactive status.
 * Counselor ID is passed via Intent from ManageCounselorsActivity.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Counselor data is placeholder for UI testing
 * - Toggle status confirmation dialog not yet implemented
 */

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminEditCounselorActivity extends AppCompatActivity {

    ImageView backButton;
    LinearLayout actionEditInfo, actionToggleStatus;
    TextView toggleStatusText, counselorStatus;
    String email, counselorId;
    boolean isActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_counselor);

        // get data passed from previous screen
        email = getIntent().getStringExtra("email");
        counselorId = getIntent().getStringExtra("counselorId");

        // initialize views
        backButton = findViewById(R.id.backButton);
        actionEditInfo = findViewById(R.id.actionEditInfo);
        actionToggleStatus = findViewById(R.id.actionToggleStatus);
        toggleStatusText = findViewById(R.id.toggleStatusText);
        counselorStatus = findViewById(R.id.counselorStatus);

        // back button
        backButton.setOnClickListener(v -> finish());

        // TODO: fetch counselor data from Firestore using counselorId
        // TODO: set counselor name, email, status

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
     * Toggles counselor active/inactive status.
     * Will update Firestore when connected.
     */
    private void toggleStatus() {
        isActive = !isActive;

        if (isActive) {
            toggleStatusText.setText("Mark as Inactive");
            counselorStatus.setText("Active");
            counselorStatus.setTextColor(getColor(R.color.counselor_green));
        } else {
            toggleStatusText.setText("Mark as Active");
            counselorStatus.setText("Inactive");
            counselorStatus.setTextColor(getColor(R.color.text_gray));
        }

        // TODO: update status in Firestore
    }
}
