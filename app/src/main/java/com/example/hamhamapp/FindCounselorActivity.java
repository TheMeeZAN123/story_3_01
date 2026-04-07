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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindCounselorActivity extends AppCompatActivity {

    ImageView backButton;
    EditText searchInput;
    Spinner specialtyFilter, sortBy;
    ListView counselorList;
    String email;

    private FirebaseFirestore db;
    private CounselorStudentAdapter adapter;
    private List<Counselor> allCounselors;
    private List<Counselor> filteredList;

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

        // initialize Firebase
        db = FirebaseFirestore.getInstance();

        // initialize lists and adapter
        allCounselors = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new CounselorStudentAdapter(this, filteredList, counselor -> {
            Intent intent = new Intent(FindCounselorActivity.this, BookAppointmentActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("counselorId", counselor.getUid());
            startActivity(intent);
        });
        counselorList.setAdapter(adapter);

        // back button
        backButton.setOnClickListener(v -> finish());

        // search input listener
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // specialty filter listener
        specialtyFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // sort by listener
        sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load counselors
        loadCounselors();
    }

    private void loadCounselors() {
        db.collection("users")
                .whereEqualTo("role", "counselor")
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    allCounselors.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Counselor counselor = doc.toObject(Counselor.class);
                        allCounselors.add(counselor);
                    }
                    applyFilters();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load counselors: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void applyFilters() {
        String searchQuery = searchInput.getText().toString().toLowerCase().trim();
        String selectedSpecialty = specialtyFilter.getSelectedItem().toString();
        String selectedSort = sortBy.getSelectedItem().toString();

        filteredList.clear();

        for (Counselor c : allCounselors) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    (c.getName() != null && c.getName().toLowerCase().contains(searchQuery)) ||
                    (c.getDescription() != null && c.getDescription().toLowerCase().contains(searchQuery));

            boolean matchesSpecialty = selectedSpecialty.equals("All Specialties") ||
                    (c.getSpecialties() != null && c.getSpecialties().contains(selectedSpecialty));

            if (matchesSearch && matchesSpecialty) {
                filteredList.add(c);
            }
        }

        // Apply sorting
        if (selectedSort.equals("Sort by Rating")) {
            Collections.sort(filteredList, (c1, c2) -> Double.compare(c2.getRating(), c1.getRating()));
        } else if (selectedSort.equals("Sort by Name")) {
            Collections.sort(filteredList, (c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        }

        adapter.notifyDataSetChanged();
    }
}