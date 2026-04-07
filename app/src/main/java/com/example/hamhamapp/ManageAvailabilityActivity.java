package com.example.hamhamapp;

/**
 * ManageAvailabilityActivity.java
 *
 * Purpose: Allows counselors to manage their available time slots.
 * Counselor selects a date (defaults to today), then adds time
 * slots with start and end times. Supports recurring slots for
 * selected days and automatic splitting of long availability windows 
 * into smaller sessions. Prevents creation of overlapping or duplicate time slots.
 */

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

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

public class ManageAvailabilityActivity extends AppCompatActivity {

    ImageView backButton, selectDateBtn;
    TextView selectedDateText, slotsDateLabel;
    Button addSlotBtn, cancelBtn, saveChangesBtn;
    LinearLayout slotsContainer;
    LinearLayout emptySlots;
    CheckBox cbMon, cbTue, cbWed, cbThu, cbFri, cbSat, cbSun;
    
    String email, selectedDate;
    private FirebaseFirestore db;
    private List<TimeSlot> currentSlots;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

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
        slotsContainer = findViewById(R.id.slotsContainer);
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
            showAddSlotDialog();
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

    private void showAddSlotDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_availability, null);
        builder.setView(dialogView);

        Button startBtn = dialogView.findViewById(R.id.btnStartTime);
        Button endBtn = dialogView.findViewById(R.id.btnEndTime);
        EditText durationInput = dialogView.findViewById(R.id.etDuration);
        Button confirmBtn = dialogView.findViewById(R.id.btnConfirm);

        final Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, 9);
        startCal.set(Calendar.MINUTE, 0);
        
        final Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, 17);
        endCal.set(Calendar.MINUTE, 0);

        startBtn.setText(formatTime(9, 0));
        endBtn.setText(formatTime(17, 0));

        startBtn.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, h, m) -> {
                startCal.set(Calendar.HOUR_OF_DAY, h);
                startCal.set(Calendar.MINUTE, m);
                startBtn.setText(formatTime(h, m));
            }, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), false).show();
        });

        endBtn.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, h, m) -> {
                endCal.set(Calendar.HOUR_OF_DAY, h);
                endCal.set(Calendar.MINUTE, m);
                endBtn.setText(formatTime(h, m));
            }, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), false).show();
        });

        AlertDialog dialog = builder.create();
        confirmBtn.setOnClickListener(v -> {
            String durStr = durationInput.getText().toString().trim();
            int durationMinutes = durStr.isEmpty() ? 0 : Integer.parseInt(durStr);
            
            if (endCal.before(startCal) || endCal.equals(startCal)) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            processNewSlots(startCal, endCal, durationMinutes);
            dialog.dismiss();
        });
        dialog.show();
    }

    private String formatTime(int hour, int minute) {
        String amPm = hour < 12 ? "AM" : "PM";
        int hour12 = hour % 12;
        if (hour12 == 0) hour12 = 12;
        return String.format(Locale.US, "%02d:%02d %s", hour12, minute, amPm);
    }

    private void processNewSlots(Calendar start, Calendar end, int durationMinutes) {
        List<Integer> daysToApply = new ArrayList<>();
        if (cbMon.isChecked()) daysToApply.add(Calendar.MONDAY);
        if (cbTue.isChecked()) daysToApply.add(Calendar.TUESDAY);
        if (cbWed.isChecked()) daysToApply.add(Calendar.WEDNESDAY);
        if (cbThu.isChecked()) daysToApply.add(Calendar.THURSDAY);
        if (cbFri.isChecked()) daysToApply.add(Calendar.FRIDAY);
        if (cbSat.isChecked()) daysToApply.add(Calendar.SATURDAY);
        if (cbSun.isChecked()) daysToApply.add(Calendar.SUNDAY);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("availability")
                .whereEqualTo("counselorId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, List<TimeRange>> existingByDate = new HashMap<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String date = doc.getString("date");
                        String sTime = doc.getString("startTime");
                        String eTime = doc.getString("endTime");
                        
                        try {
                            Date sDate = timeFormat.parse(sTime);
                            Date eDate = timeFormat.parse(eTime);
                            if (sDate != null && eDate != null) {
                                if (!existingByDate.containsKey(date)) existingByDate.put(date, new ArrayList<>());
                                existingByDate.get(date).add(new TimeRange(sDate.getTime(), eDate.getTime()));
                            }
                        } catch (ParseException e) {}
                    }

                    WriteBatch batch = db.batch();
                    boolean hasNew = false;

                    if (daysToApply.isEmpty()) {
                        if (createSlotsForSingleDate(batch, uid, selectedDate, start, end, durationMinutes, existingByDate)) {
                            hasNew = true;
                        }
                    } else {
                        for (Integer day : daysToApply) {
                            Calendar c = Calendar.getInstance();
                            try {
                                Date parsedDate = sdf.parse(selectedDate);
                                if (parsedDate != null) c.setTime(parsedDate);
                            } catch (Exception e) {}
                            
                            for (int week = 0; week < 4; week++) {
                                while (c.get(Calendar.DAY_OF_WEEK) != day) c.add(Calendar.DATE, 1);
                                if (createSlotsForSingleDate(batch, uid, sdf.format(c.getTime()), start, end, durationMinutes, existingByDate)) {
                                    hasNew = true;
                                }
                                c.add(Calendar.DATE, 1);
                            }
                        }
                    }

                    if (hasNew) {
                        batch.commit().addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Availability updated", Toast.LENGTH_SHORT).show();
                            loadSlotsForDate(selectedDate);
                        });
                    } else {
                        Toast.makeText(this, "No new slots added (they overlap with existing ones)", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean createSlotsForSingleDate(WriteBatch batch, String uid, String date, Calendar start, Calendar end, int duration, Map<String, List<TimeRange>> existing) {
        boolean addedAny = false;
        if (duration <= 0) {
            if (!isOverlapping(date, start.getTimeInMillis(), end.getTimeInMillis(), existing)) {
                TimeSlot slot = new TimeSlot(null, uid, date, formatTime(start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE)), 
                        formatTime(end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE)), false);
                batch.set(db.collection("availability").document(), slot);
                addedAny = true;
            }
        } else {
            Calendar currentStart = (Calendar) start.clone();
            while (true) {
                Calendar currentEnd = (Calendar) currentStart.clone();
                currentEnd.add(Calendar.MINUTE, duration);
                if (currentEnd.after(end)) break;

                if (!isOverlapping(date, currentStart.getTimeInMillis(), currentEnd.getTimeInMillis(), existing)) {
                    TimeSlot slot = new TimeSlot(null, uid, date, 
                            formatTime(currentStart.get(Calendar.HOUR_OF_DAY), currentStart.get(Calendar.MINUTE)), 
                            formatTime(currentEnd.get(Calendar.HOUR_OF_DAY), currentEnd.get(Calendar.MINUTE)), false);
                    batch.set(db.collection("availability").document(), slot);
                    addedAny = true;
                }
                currentStart = (Calendar) currentEnd.clone();
            }
        }
        return addedAny;
    }

    private boolean isOverlapping(String date, long startMs, long endMs, Map<String, List<TimeRange>> existing) {
        // We only care about the time part for comparison within a day
        long sTime = getTimeOnly(startMs);
        long eTime = getTimeOnly(endMs);

        List<TimeRange> ranges = existing.get(date);
        if (ranges == null) return false;

        for (TimeRange range : ranges) {
            long exStart = getTimeOnly(range.start);
            long exEnd = getTimeOnly(range.end);

            // Overlap check: (StartA < EndB) and (EndA > StartB)
            if (sTime < exEnd && eTime > exStart) return true;
        }
        return false;
    }

    private long getTimeOnly(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        Calendar res = Calendar.getInstance();
        res.set(1970, 0, 1, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), 0);
        res.set(Calendar.MILLISECOND, 0);
        return res.getTimeInMillis();
    }

    private static class TimeRange {
        long start, end;
        TimeRange(long s, long e) { this.start = s; this.end = e; }
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
                    
                    Collections.sort(currentSlots, (s1, s2) -> {
                        try {
                            Date d1 = timeFormat.parse(s1.getStartTime());
                            Date d2 = timeFormat.parse(s2.getStartTime());
                            if (d1 != null && d2 != null) return d1.compareTo(d2);
                        } catch (ParseException e) {}
                        return 0;
                    });
                    updateUI();
                });
    }

    private void updateUI() {
        slotsContainer.removeAllViews();
        if (currentSlots.isEmpty()) {
            emptySlots.setVisibility(View.VISIBLE);
            slotsContainer.setVisibility(View.GONE);
        } else {
            emptySlots.setVisibility(View.GONE);
            slotsContainer.setVisibility(View.VISIBLE);
            for (TimeSlot slot : currentSlots) {
                View slotView = getLayoutInflater().inflate(R.layout.item_timeslot_counslor, slotsContainer, false);
                TextView timeView = slotView.findViewById(R.id.slotTime);
                TextView statusView = slotView.findViewById(R.id.slotStatus);
                Button markBtn = slotView.findViewById(R.id.markBtn);
                ImageView deleteBtn = slotView.findViewById(R.id.deleteSlotBtn);

                timeView.setText(slot.getTime());
                if (slot.isBooked()) {
                    statusView.setText("Booked");
                    statusView.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                    markBtn.setVisibility(View.GONE);
                } else {
                    statusView.setText("Available for booking");
                    statusView.setTextColor(ContextCompat.getColor(this, R.color.counselor_green));
                    markBtn.setVisibility(View.VISIBLE);
                    markBtn.setText("Mark Unavailable");
                }
                markBtn.setOnClickListener(v -> deleteSlot(slot));
                deleteBtn.setOnClickListener(v -> deleteSlot(slot));
                slotsContainer.addView(slotView);
            }
        }
    }

    private void deleteSlot(TimeSlot slot) {
        if (slot.isBooked()) {
            Toast.makeText(this, "Cannot delete a booked slot", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("availability").document(slot.getId()).delete()
                .addOnSuccessListener(unused -> loadSlotsForDate(selectedDate));
    }
}