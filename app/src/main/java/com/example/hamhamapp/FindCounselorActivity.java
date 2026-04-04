package com.example.hamhamapp;

/**
 * FindCounselorActivity.java
 * Purpose: Displays a searchable, filterable list of available
 * counselors for students to browse. Allows filtering by specialty
 * and sorting by rating or name. Navigates to BookAppointmentActivity
 * when a counselor is selected.
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class FindCounselorActivity extends AppCompatActivity {

    ImageView backButton;
    EditText searchInput;
    Spinner specialtyFilter, sortBy;
    ListView counselorList;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_counselor);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        searchInput = findViewById(R.id.searchInput);
        specialtyFilter = findViewById(R.id.specialtyFilter);
        sortBy = findViewById(R.id.sortBy);
        counselorList = findViewById(R.id.counselorList);

        // back button
        backButton.setOnClickListener(v -> finish());

        // TODO: load counselors from Firebase Firestore
        // TODO: implement search functionality
        // TODO: implement filter by specialty
        // TODO: implement sort by rating/name
        // TODO: set up CounselorAdapter for ListView

        // counselor list item click → go to book appointment
        counselorList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(FindCounselorActivity.this, BookAppointmentActivity.class);
            intent.putExtra("email", email);
            // TODO: pass counselor ID when Firebase is connected
            // intent.putExtra("counselorId", counselor.getId());
            startActivity(intent);
        });
    }
}