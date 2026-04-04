package com.example.hamhamapp;

/**
 * StudentEditProfileActivity.java
 *
 * Purpose: Allows student to edit their personal information
 * including name and phone number. Email cannot be changed
 * as it is tied to Firebase Authentication. Changes are
 * saved to Firestore when save button is clicked.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Fields not yet pre-filled with current data from Firestore
 * - Save changes not yet functional
 * - Input validation not yet fully implemented
 */

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentEditProfileActivity extends AppCompatActivity {

    ImageView backButton;
    EditText nameInput, phoneInput;
    Button saveChangesBtn, cancelBtn;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_info);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // back button
        backButton.setOnClickListener(v -> finish());

        // cancel button
        cancelBtn.setOnClickListener(v -> finish());

        // TODO: pre-fill fields with current student data from Firestore
        // FirebaseFirestore.getInstance()
        //     .collection("users")
        //     .document(email)
        //     .get()
        //     .addOnSuccessListener(doc -> {
        //         nameInput.setText(doc.getString("name"));
        //         phoneInput.setText(doc.getString("phone"));
        //     });

        // save changes button
        saveChangesBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            // basic validation
            if (name.isEmpty()) {
                nameInput.setError("Name is required");
                return;
            }

            // TODO: save updated data to Firestore
            // FirebaseFirestore.getInstance()
            //     .collection("users")
            //     .document(email)
            //     .update("name", name, "phone", phone)
            //     .addOnSuccessListener(v -> finish());

            // for now just go back
            finish();
        });
    }
}