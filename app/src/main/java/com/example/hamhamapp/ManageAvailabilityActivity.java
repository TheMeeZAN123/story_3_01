package com.example.hamhamapp;

/**
 * ManageAvailabilityActivity.java
 *
 * Purpose: Allows counselors to manage their available time slots.
 * Counselor selects a date (defaults to today), then adds time
 * slots with start and end times. Supports recurring slots for
 * selected days over the next 4 weeks.
 */

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ManageAvailabilityActivity extends AppCompatActivity {

    ImageView backButton, selectDateBtn;
    TextView selectedDateText, slotsDateLabel;
    Button addSlotBtn, cancelBtn, saveChangesBtn;
    ListView slotsListCounselor;
    LinearLayout emptySlots;
    CheckBox cbMon, cbTue, cbWed, cbThu, cbFri, cbSat, cbSun;
    
    String email, selectedDate;
    private FirebaseFirestore db;
    private CounselorSlotAdapter adapter;
    private List<TimeSlot> currentSlots;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_availability);

        email = getIntent().getStringExtra("email");

        // initialize views
        backButton = findViewById(R.id.backButton);
        selectDateBtn = findViewById(R.id.selectDateBtn);
        selectedDateText = findViewById(R.id.selectedDateText);
        slotsDateLabel = findViewById(R.id.slotsDateLabel);
        addSlotBtn = findViewById(R.id.addSlotBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        slotsListCounselor = findViewById(R.id.slotsListCounselor);
        emptySlots = findViewById(R.id.emptySlots);
        
        cbMon = findViewById(R.id.cbMon);
        cbTue = findViewById(R.id.cbTue);
        cbWed = findViewById(R.id.cbWed);
        cbThu = findViewById(R.id.cbThu);
        cbFri = findViewById(R.id.cbFri);
        cbSat = findViewById(R.id.cbSat);
        cbSun = findViewById(R.id.cbSun);

        db = FirebaseFirestore.getInstance();
        currentSlots = new ArrayList<>();
        adapter = new CounselorSlotAdapter(this, currentSlots, new CounselorSlotAdapter.OnSlotActionListener() {
            @Override public void onMarkToggle(TimeSlot slot) { deleteSlot(slot); }
            @Override public void onDelete(TimeSlot slot) { deleteSlot(slot); }
        });
        slotsListCounselor.setAdapter(adapter);

        // Default to today's date
        Calendar today = Calendar.getInstance();
        selectedDate = sdf.format(today.getTime());
        selectedDateText.setText(selectedDate);
        slotsDateLabel.setText("for " + selectedDate);

        backButton.setOnClickListener(v -> finish());
        cancelBtn.setOnClickListener(v -> finish());
        selectDateBtn.setOnClickListener(v -> showDatePicker());
        selectedDateText.setOnClickListener(v -> showDatePicker());

        addSlotBtn.setOnClickListener(v -> {
            if (selectedDate == null) return;
            showStartTimePicker();
        });

        saveChangesBtn.setOnClickListener(v -> finish());

        loadSlotsForDate(selectedDate);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(year, month, day);
                    selectedDate = sdf.format(chosen.getTime());
                    selectedDateText.setText(selectedDate);
                    slotsDateLabel.setText("for " + selectedDate);
                    loadSlotsForDate(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            String startTime = formatTime(hour, minute);
            showEndTimePicker(startTime);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }

    private void showEndTimePicker(String startTime) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            String endTime = formatTime(hour, minute);
            processNewSlot(startTime, endTime);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }

    private String formatTime(int hour, int minute) {
        String amPm = hour < 12 ? "AM" : "PM";
        int hour12 = hour % 12;
        if (hour12 == 0) hour12 = 12;
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm);
    }

    private void processNewSlot(String startTime, String endTime) {
        List<Integer> daysToApply = new ArrayList<>();
        if (cbMon.isChecked()) daysToApply.add(Calendar.MONDAY);
        if (cbTue.isChecked()) daysToApply.add(Calendar.TUESDAY);
        if (cbWed.isChecked()) daysToApply.add(Calendar.WEDNESDAY);
        if (cbThu.isChecked()) daysToApply.add(Calendar.THURSDAY);
        if (cbFri.isChecked()) daysToApply.add(Calendar.FRIDAY);
        if (cbSat.isChecked()) daysToApply.add(Calendar.SATURDAY);
        if (cbSun.isChecked()) daysToApply.add(Calendar.SUNDAY);

        if (daysToApply.isEmpty()) {
            addSingleSlot(selectedDate, startTime, endTime);
        } else {
            addRecurringSlots(daysToApply, startTime, endTime);
        }
    }

    private void addSingleSlot(String date, String start, String end) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TimeSlot slot = new TimeSlot(null, uid, date, start, end, false);
        db.collection("availability").add(slot).addOnSuccessListener(ref -> {
            loadSlotsForDate(selectedDate);
        });
    }

    private void addRecurringSlots(List<Integer> days, String start, String end) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        WriteBatch batch = db.batch();
        
        for (Integer day : days) {
            Calendar c = Calendar.getInstance();
            // Start from today or selected date
            try {
                c.setTime(sdf.parse(selectedDate));
            } catch (Exception e) {}

            for (int week = 0; week < 4; week++) {
                // Find next occurrence of this day
                while (c.get(Calendar.DAY_OF_WEEK) != day) {
                    c.add(Calendar.DATE, 1);
                }
                
                String dateStr = sdf.format(c.getTime());
                TimeSlot slot = new TimeSlot(null, uid, dateStr, start, end, false);
                batch.set(db.collection("availability").document(), slot);
                
                c.add(Calendar.DATE, 1); // move past this occurrence to find next week's
            }
        }

        batch.commit().addOnSuccessListener(unused -> {
            Toast.makeText(this, "Recurring slots added for next 4 weeks", Toast.LENGTH_SHORT).show();
            loadSlotsForDate(selectedDate);
        });
    }

    private void loadSlotsForDate(String date) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("availability")
                .whereEqualTo("counselorId", uid)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    currentSlots.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        TimeSlot slot = doc.toObject(TimeSlot.class);
                        slot.setId(doc.getId());
                        currentSlots.add(slot);
                    }
                    updateUI();
                });
    }

    private void deleteSlot(TimeSlot slot) {
        if (slot.isBooked()) {
            Toast.makeText(this, "Cannot delete a booked slot", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("availability").document(slot.getId()).delete()
                .addOnSuccessListener(unused -> loadSlotsForDate(selectedDate));
    }

    private void updateUI() {
        if (currentSlots.isEmpty()) {
            emptySlots.setVisibility(View.VISIBLE);
            slotsListCounselor.setVisibility(View.GONE);
        } else {
            emptySlots.setVisibility(View.GONE);
            slotsListCounselor.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}