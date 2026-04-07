package com.example.hamhamapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * TimeSlotAdapter.java
 * Purpose: ArrayAdapter for displaying available time slots in BookAppointmentActivity.
 */
public class TimeSlotAdapter extends ArrayAdapter<TimeSlot> {

    private final Context context;
    private final List<TimeSlot> timeSlots;

    public TimeSlotAdapter(Context context, List<TimeSlot> timeSlots) {
        super(context, R.layout.item_timeslots, timeSlots);
        this.context = context;
        this.timeSlots = timeSlots;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_timeslots, parent, false);
        }

        TimeSlot slot = timeSlots.get(position);

        TextView dateView = convertView.findViewById(R.id.slotDate);
        TextView timeView = convertView.findViewById(R.id.slotTime);

        dateView.setText(slot.getDate());
        timeView.setText(slot.getTime());

        // Visual feedback based on booking status (though we usually only show available ones here)
        if (slot.isBooked()) {
            timeView.setAlpha(0.5f);
            timeView.setBackgroundResource(R.drawable.slot_unavailable); // Assuming this exists or using a gray bg
        } else {
            timeView.setAlpha(1.0f);
            timeView.setBackgroundResource(R.drawable.slot_available);
        }

        return convertView;
    }
}