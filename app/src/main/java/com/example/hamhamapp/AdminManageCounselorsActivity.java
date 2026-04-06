package com.example.hamhamapp;

/**
 * AdminManageCounselorsActivity.java
 *
 * Purpose: Displays a searchable list of all registered counselors
 * for the admin to manage. Loads counselors from Firestore in real-time,
 * supports live search by name or specialty, and lets the admin add new
 * counselors or tap an existing one to edit their info.
 *
 * Outstanding issues: None.
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageCounselorsActivity extends AppCompatActivity {

    ImageView            backButton;
    Button               addCounselorBtn;
    EditText             searchInput;
    ListView             counselorsList;
    LinearLayout         emptyState;
    String               email;

    private FirebaseFirestore       db;
    private CounselorAdminAdapter   adapter;
    private List<Counselor>         allCounselors;   // full list from Firestore
    private List<Counselor>         filteredList;    // list shown in ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_counselors);

        // get email passed from previous screen
        email = getIntent().getStringExtra("email");

        // initialize views
        backButton      = findViewById(R.id.backButton);
        addCounselorBtn = findViewById(R.id.addCounselorBtn);
        searchInput     = findViewById(R.id.searchInput);
        counselorsList  = findViewById(R.id.counselorsList);
        emptyState      = findViewById(R.id.emptyState);

        // initialize Firebase
        db = FirebaseFirestore.getInstance();

        // initialize lists and adapter
        allCounselors = new ArrayList<>();
        filteredList  = new ArrayList<>();
        adapter       = new CounselorAdminAdapter(this, filteredList);
        counselorsList.setAdapter(adapter);

        // back button
        backButton.setOnClickListener(v -> finish());

        // add counselor button
        addCounselorBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageCounselorsActivity.this,
                    AdminAddCounselorActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // counselor item click → go to edit screen for that counselor
        counselorsList.setOnItemClickListener((parent, view, position, id) -> {
            Counselor selected = filteredList.get(position);
            Intent intent = new Intent(AdminManageCounselorsActivity.this,
                    AdminEditCounselorActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("counselorId", selected.getUid());
            startActivity(intent);
        });

        // live search — filter list as admin types
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCounselors(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // load counselors from Firestore
        loadCounselors();
    }

    /**
     * Fetches all users with role == "counselor" from Firestore,
     * populates allCounselors, and refreshes the list view.
     */
    private void loadCounselors() {
        db.collection("users")
                .whereEqualTo("role", "counselor")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    allCounselors.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Counselor counselor = doc.toObject(Counselor.class);
                        allCounselors.add(counselor);
                    }
                    // show full list on first load (no search text yet)
                    filterCounselors(searchInput.getText().toString().trim());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to load counselors: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    /**
     * Filters allCounselors by name or specialty and updates the ListView.
     * An empty query shows all counselors.
     *
     * @param query The search string typed by the admin.
     */
    private void filterCounselors(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(allCounselors);
        } else {
            String lower = query.toLowerCase();
            for (Counselor c : allCounselors) {
                // match on name
                boolean nameMatch = c.getName() != null
                        && c.getName().toLowerCase().contains(lower);

                // match on any specialty
                boolean specialtyMatch = false;
                if (c.getSpecialties() != null) {
                    for (String spec : c.getSpecialties()) {
                        if (spec.toLowerCase().contains(lower)) {
                            specialtyMatch = true;
                            break;
                        }
                    }
                }

                if (nameMatch || specialtyMatch) {
                    filteredList.add(c);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    /**
     * Shows the empty state message when no counselors match the current filter,
     * and hides it when there are results to display.
     */
    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            counselorsList.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            counselorsList.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    /**
     * Refreshes the counselor list when returning from the Add or Edit screen,
     * in case a counselor was added or modified.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadCounselors();
    }
}