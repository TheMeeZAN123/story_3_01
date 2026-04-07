package com.example.hamhamapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * CounselorSlotAdapter.java
 * Purpose: ArrayAdapter for the counselor's "Manage Availability" ListView.
 */
public class CounselorSlotAdapter extends ArrayAdapter<TimeSlot> {

    private final Context context;
    private final List<TimeSlot> slots;
    private final OnSlotActionListener listener;

    public interface OnSlotActionListener {
        void onMarkToggle(TimeSlot slot);
        void onDelete(TimeSlot slot);
    }

    public CounselorSlotAdapter(Context context, List<TimeSlot> slots, OnSlotActionListener listener) {
        super(context, R.layout.item_timeslot_counslor, slots);
        this.context = context;
        this.slots = slots;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_timeslot_counslor, parent, false);
        }

        TimeSlot slot = slots.get(position);

        TextView timeView = convertView.findViewById(R.id.slotTime);
        TextView statusView = convertView.findViewById(R.id.slotStatus);
        Button markBtn = convertView.findViewById(R.id.markBtn);
        ImageView deleteBtn = convertView.findViewById(R.id.deleteSlotBtn);

        timeView.setText(slot.getTime());

        if (slot.isBooked()) {
            statusView.setText("Booked");
            statusView.setTextColor(ContextCompat.getColor(context, R.color.text_gray));
            markBtn.setVisibility(View.GONE); // Cannot mark booked slots as unavailable manually easily here
        } else {
            statusView.setText("Available for booking");
            statusView.setTextColor(ContextCompat.getColor(context, R.color.counselor_green));
            markBtn.setVisibility(View.VISIBLE);
            markBtn.setText("Mark Unavailable");
        }

        markBtn.setOnClickListener(v -> {
            if (listener != null) listener.onMarkToggle(slot);
        });

        deleteBtn.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(slot);
        });

        return convertView;
    }
}