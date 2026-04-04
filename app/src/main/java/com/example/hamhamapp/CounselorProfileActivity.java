package com.example.hamhamapp;

/**
 * CounselorProfileActivity.java
 *
 * Purpose: Displays the counselor's personal profile information
 * including name, email, phone, specialties and rating.
 * Navigates to EditCounselorProfileActivity for editing.
 * Specialty tags are dynamically added from Firestore data.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Profile data is placeholder for UI testing
 * - Specialty tags are hardcoded, need dynamic loading from Firestore
 * - Rating not yet calculated from feedback data
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CounselorProfileActivity extends AppCompatActivity {

    ImageView backButton;
    Button editProfileBtn;
    TextView counselorName, counselorEmail, counselorPhone, counselorRating;
    LinearLayout specialtyTags;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_profile);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        counselorName = findViewById(R.id.counselorName);
        counselorEmail = findViewById(R.id.counselorEmail);
        counselorPhone = findViewById(R.id.counselorPhone);
        counselorRating = findViewById(R.id.counselorRating);
        specialtyTags = findViewById(R.id.specialtyTags);

        // TODO: fetch counselor data from Firestore using email
        // for now set placeholder data
        counselorEmail.setText(email);
        counselorName.setText("Dr. Counselor");
        counselorPhone.setText("Not set");
        counselorRating.setText("--");

        // TODO: load specialties from Firestore
        // for now add placeholder tags
        addSpecialtyTag("Anxiety");
        addSpecialtyTag("Depression");
        addSpecialtyTag("Stress");

        // back button
        backButton.setOnClickListener(v -> finish());

        // edit profile
        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CounselorProfileActivity.this, CounselorEditProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }

    /**
     * Dynamically adds a specialty tag TextView to the specialtyTags layout.
     * Called for each specialty when loading from Firestore.
     */
    private void addSpecialtyTag(String specialty) {
        TextView tag = new TextView(this);
        tag.setText(specialty);
        tag.setTextSize(11);
        tag.setTextColor(getColor(R.color.primary_blue));
        tag.setBackground(getDrawable(R.drawable.icon_bg_blue));
        tag.setPadding(24, 8, 24, 8);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(16);
        tag.setLayoutParams(params);

        specialtyTags.addView(tag);
    }
}