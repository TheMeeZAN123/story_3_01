package com.example.hamhamapp;

/**
 * AdminManageCounselorsActivity.java
 *
 * Purpose: Displays a searchable list of all registered counselors
 * for the admin to manage. Admin can add new counselors via the
 * Add Counselor button, or click on a counselor to edit their
 * info or toggle their active/inactive status.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Counselor list is placeholder for UI testing
 * - Search functionality not yet implemented
 * - CounselorAdminAdapter not yet implemented
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminManageCounselorsActivity extends AppCompatActivity {

    ImageView backButton;
    Button addCounselorBtn;
    EditText searchInput;
    ListView counselorsList;
    LinearLayout emptyState;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_counselors);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        addCounselorBtn = findViewById(R.id.addCounselorBtn);
        searchInput = findViewById(R.id.searchInput);
        counselorsList = findViewById(R.id.counselorsList);
        emptyState = findViewById(R.id.emptyState);

        // back button
        backButton.setOnClickListener(v -> finish());

        // add counselor button
        addCounselorBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageCounselorsActivity.this, AdminAddCounselorActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // TODO: load counselors from Firestore
        // TODO: implement search functionality
        // TODO: set up CounselorAdminAdapter for ListView

        // for now show empty state
        loadCounselors();

        // counselor item click → go to counselor actions
        counselorsList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(AdminManageCounselorsActivity.this, AdminEditCounselorActivity.class);
            intent.putExtra("email", email);
            // TODO: pass counselorId when Firebase connected
            // intent.putExtra("counselorId", counselor.getId());
            startActivity(intent);
        });
    }

    /**
     * Loads counselors from Firestore.
     * Shows empty state if no counselors exist.
     */
    private void loadCounselors() {
        // TODO: fetch counselors from Firestore
        boolean hasCounselors = false;

        if (hasCounselors) {
            counselorsList.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        } else {
            counselorsList.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
    }
}