package com.example.hamhamapp;

/**
 * CounselorCalendarActivity.java
 *
 * Purpose: Provides a full calendar view for counselors to see their schedule.
 * Tapping a date shows all slots (Available and Booked) for that day.
 * Booked slots show student info, free slots show availability.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CounselorCalendarActivity extends AppCompatActivity {

    ImageView backButton;
    CalendarView calendarView;
    TextView selectedDateLabel;
    LinearLayout calendarSlotsContainer, emptySchedule;
    
    private FirebaseFirestore db;
    private String email, selectedDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    
    // Store appointments and availability indexed by date for quick lookup
    private Map<String, List<TimeSlot>> allSlotsByDate = new HashMap<>();
    private Map<String, Appointment> apptsBySlotId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_calendar);

        email = getIntent().getStringExtra("email");
        db = FirebaseFirestore.getInstance();

        backButton = findViewById(R.id.backButton);
        calendarView = findViewById(R.id.calendarView);
        selectedDateLabel = findViewById(R.id.selectedDateLabel);
        calendarSlotsContainer = findViewById(R.id.calendarSlotsContainer);
        emptySchedule = findViewById(R.id.emptySchedule);

        backButton.setOnClickListener(v -> finish());

        // Default to today
        Calendar today = Calendar.getInstance();
        selectedDate = sdf.format(today.getTime());
        selectedDateLabel.setText("Schedule for " + selectedDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            selectedDate = sdf.format(c.getTime());
            selectedDateLabel.setText("Schedule for " + selectedDate);
            updateDailyView();
        });

        loadAllData();
    }

    private void loadAllData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        // 1. Load all appointments to map them to slots
        db.collection("appointments")
                .whereEqualTo("counselorId", uid)
                .get()
                .addOnSuccessListener(apptSnap -> {
                    apptsBySlotId.clear();
                    for (QueryDocumentSnapshot doc : apptSnap) {
                        Appointment appt = doc.toObject(Appointment.class);
                        appt.setId(doc.getId());
                        if (!appt.getStatus().equals("cancelled")) {
                            apptsBySlotId.put(appt.getSlotId(), appt);
                        }
                    }
                    
                    // 2. Load all availability slots
                    db.collection("availability")
                            .whereEqualTo("counselorId", uid)
                            .get()
                            .addOnSuccessListener(availSnap -> {
                                allSlotsByDate.clear();
                                for (QueryDocumentSnapshot doc : availSnap) {
                                    TimeSlot slot = doc.toObject(TimeSlot.class);
                                    slot.setId(doc.getId());
                                    
                                    String date = slot.getDate();
                                    if (!allSlotsByDate.containsKey(date)) {
                                        allSlotsByDate.put(date, new ArrayList<>());
                                    }
                                    allSlotsByDate.get(date).add(slot);
                                }
                                updateDailyView();
                            });
                });
    }

    private void updateDailyView() {
        calendarSlotsContainer.removeAllViews();
        List<TimeSlot> slots = allSlotsByDate.get(selectedDate);

        if (slots == null || slots.isEmpty()) {
            emptySchedule.setVisibility(View.VISIBLE);
            calendarSlotsContainer.setVisibility(View.GONE);
        } else {
            emptySchedule.setVisibility(View.GONE);
            calendarSlotsContainer.setVisibility(View.VISIBLE);

            // Sort chronologically
            Collections.sort(slots, (s1, s2) -> {
                try {
                    Date d1 = timeFormat.parse(s1.getStartTime());
                    Date d2 = timeFormat.parse(s2.getStartTime());
                    if (d1 != null && d2 != null) return d1.compareTo(d2);
                } catch (Exception e) { }
                return 0;
            });

            for (TimeSlot slot : slots) {
                View card = getLayoutInflater().inflate(R.layout.item_timeslot_counslor, calendarSlotsContainer, false);
                TextView timeText = card.findViewById(R.id.slotTime);
                TextView statusText = card.findViewById(R.id.slotStatus);
                android.widget.Button markBtn = card.findViewById(R.id.markBtn);
                ImageView deleteBtn = card.findViewById(R.id.deleteSlotBtn);

                timeText.setText(slot.getTime());
                
                // Calendar view specific styling
                deleteBtn.setVisibility(View.GONE); // View only
                markBtn.setVisibility(View.GONE);

                if (slot.isBooked()) {
                    card.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_bg_blue));
                    Appointment appt = apptsBySlotId.get(slot.getId());
                    if (appt != null) {
                        statusText.setText("BOOKED: " + appt.getStudentEmail());
                        statusText.setTextColor(ContextCompat.getColor(this, R.color.primary_blue));
                        card.setOnClickListener(v -> {
                            Intent intent = new Intent(this, StudentProfileCounselorSide.class);
                            intent.putExtra("studentEmail", appt.getStudentEmail());
                            startActivity(intent);
                        });
                    } else {
                        statusText.setText("Booked (Pending Details)");
                    }
                } else {
                    card.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_bg_green));
                    statusText.setText("FREE / AVAILABLE");
                    statusText.setTextColor(ContextCompat.getColor(this, R.color.counselor_green));
                }

                calendarSlotsContainer.addView(card);
            }
        }
    }
}