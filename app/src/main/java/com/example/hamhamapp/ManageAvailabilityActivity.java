package com.example.hamhamapp;

/**
 * ManageAvailabilityActivity.java
 *
 * Purpose: Allows counselors to manage their available time slots.
 * Counselor selects a date using DatePickerDialog, then adds time
 * slots using TimePickerDialog. Slots can be marked available/
 * unavailable or deleted. Changes are saved to Firestore.
 *
 * Outstanding issues:
 * - Firebase Firestore not yet connected
 * - Save changes not yet functional
 * - SlotAdapter for ListView not yet implemented
 * - Mark available/unavailable toggle not yet implemented
 */

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class ManageAvailabilityActivity extends AppCompatActivity {

    ImageView backButton, selectDateBtn;
    TextView selectedDateText, slotsDateLabel;
    Button addSlotBtn, cancelBtn, saveChangesBtn;
    ListView slotsListCounselor;
    LinearLayout emptySlots;
    String email, selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_availability);

        // get email passed from previous screen
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

        // back button
        backButton.setOnClickListener(v -> finish());

        // cancel button
        cancelBtn.setOnClickListener(v -> finish());

        // date picker — opens calendar dialog
        selectDateBtn.setOnClickListener(v -> showDatePicker());
        selectedDateText.setOnClickListener(v -> showDatePicker());

        // add slot button — opens time picker dialog
        addSlotBtn.setOnClickListener(v -> {
            if (selectedDate == null) {
                selectedDateText.setError("Please select a date first");
                return;
            }
            showTimePicker();
        });

        // save changes
        // TODO: save slots to Firestore
        saveChangesBtn.setOnClickListener(v -> {
            // TODO: implement save to Firestore
            finish();
        });
    }

    /**
     * Shows Android's built in DatePickerDialog.
     * Updates selectedDateText and slotsDateLabel with chosen date.
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // format date as YYYY-MM-DD
                    selectedDate = selectedYear + "-" +
                            String.format("%02d", selectedMonth + 1) + "-" +
                            String.format("%02d", selectedDay);

                    selectedDateText.setText(selectedDate);
                    slotsDateLabel.setText("for " + selectedDate);

                    // TODO: load existing slots for this date from Firestore
                    loadSlotsForDate(selectedDate);
                },
                year, month, day
        );

        // prevent selecting past dates
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    /**
     * Shows Android's built in TimePickerDialog.
     * Adds selected time as a new slot to the list.
     */
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // format time as HH:MM AM/PM
                    String amPm = selectedHour < 12 ? "AM" : "PM";
                    int hour12 = selectedHour % 12;
                    if (hour12 == 0) hour12 = 12;
                    String time = String.format("%02d:%02d %s", hour12, selectedMinute, amPm);

                    // TODO: add slot to Firestore
                    addSlotToList(time);
                },
                hour, minute, false
        );
        timePickerDialog.show();
    }

    /**
     * Loads existing time slots for the selected date.
     * Currently shows empty state — will load from Firestore later.
     */
    private void loadSlotsForDate(String date) {
        // TODO: fetch slots from Firestore for this date
        // for now show empty state
        emptySlots.setVisibility(View.VISIBLE);
        slotsListCounselor.setVisibility(View.GONE);
    }

    /**
     * Adds a new time slot to the list view.
     * Will be saved to Firestore when save is clicked.
     */
    private void addSlotToList(String time) {
        // TODO: add to adapter and update ListView
        // TODO: implement SlotAdapter
        emptySlots.setVisibility(View.GONE);
        slotsListCounselor.setVisibility(View.VISIBLE);
    }
}
