package com.example.hamhamapp;

/**
 * CounselorEditProfileActivity.java
 *
 * Purpose: Allows counselor to edit their personal information
 * including name, phone, specialties and description.
 * Email cannot be changed as it is tied to Firebase Authentication.
 * Specialties are entered as comma separated values and will be
 * parsed into a list when saved to Firestore.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Fields not yet pre-filled with current data from Firestore
 * - Save changes not yet functional
 * - Specialties need to be parsed by comma into list for Firestore
 */

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CounselorEditProfileActivity extends AppCompatActivity {

    ImageView backButton;
    EditText nameInput, phoneInput, specialtiesInput, descriptionInput;
    Button saveChangesBtn, cancelBtn;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_counselor_info);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        specialtiesInput = findViewById(R.id.specialtiesInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // back button
        backButton.setOnClickListener(v -> finish());

        // cancel button
        cancelBtn.setOnClickListener(v -> finish());

        // TODO: pre-fill fields with current counselor data from Firestore
        // FirebaseFirestore.getInstance()
        //     .collection("counselors")
        //     .document(email)
        //     .get()
        //     .addOnSuccessListener(doc -> {
        //         nameInput.setText(doc.getString("name"));
        //         phoneInput.setText(doc.getString("phone"));
        //         descriptionInput.setText(doc.getString("description"));
        //         // join specialties list into comma separated string
        //         List<String> specialties = (List<String>) doc.get("specialties");
        //         if (specialties != null) {
        //             specialtiesInput.setText(String.join(", ", specialties));
        //         }
        //     });

        // save changes button
        saveChangesBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String specialties = specialtiesInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            // basic validation
            if (name.isEmpty()) {
                nameInput.setError("Name is required");
                return;
            }

            // parse specialties by comma into list
            // e.g. "Anxiety, Depression" → ["Anxiety", "Depression"]
            String[] specialtiesList = specialties.split(",");

            // TODO: save updated data to Firestore
            // Map<String, Object> updates = new HashMap<>();
            // updates.put("name", name);
            // updates.put("phone", phone);
            // updates.put("description", description);
            // updates.put("specialties", Arrays.asList(specialtiesList));
            // FirebaseFirestore.getInstance()
            //     .collection("counselors")
            //     .document(email)
            //     .update(updates)
            //     .addOnSuccessListener(v -> finish());

            // for now just go back
            finish();
        });
    }
}