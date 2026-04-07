package com.example.hamhamapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * CounselorAppointmentAdapter.java
 * Purpose: ArrayAdapter for the counselor's appointment list.
 */
public class CounselorAppointmentAdapter extends ArrayAdapter<Appointment> {

    private final Context context;
    private final List<Appointment> appointments;
    private final boolean isUpcoming;

    public CounselorAppointmentAdapter(Context context, List<Appointment> appointments, boolean isUpcoming) {
        super(context, R.layout.item_appointment_counselor_view, appointments);
        this.context = context;
        this.appointments = appointments;
        this.isUpcoming = isUpcoming;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_appointment_counselor_view, parent, false);
        }

        Appointment appt = appointments.get(position);

        TextView studentNameView = convertView.findViewById(R.id.studentName);
        TextView dateView = convertView.findViewById(R.id.appointmentDate);
        TextView timeView = convertView.findViewById(R.id.appointmentTime);
        ImageView rescheduleBtn = convertView.findViewById(R.id.rescheduleBtn);
        ImageView cancelBtn = convertView.findViewById(R.id.cancelBtn);
        Button viewProfileBtn = convertView.findViewById(R.id.viewStudentProfileBtn);
        LinearLayout statusBox = convertView.findViewById(R.id.statusBox);

        studentNameView.setText(appt.getStudentEmail()); // Ideally fetch name, but using email as placeholder
        dateView.setText(appt.getDate());
        timeView.setText(appt.getTime());

        if (isUpcoming) {
            rescheduleBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            statusBox.setVisibility(View.VISIBLE);
        } else {
            rescheduleBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            statusBox.setVisibility(View.GONE);
        }

        viewProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudentProfileCounselorSide.class);
            intent.putExtra("studentEmail", appt.getStudentEmail());
            context.startActivity(intent);
        });

        cancelBtn.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("appointments").document(appt.getId())
                    .update("status", "cancelled")
                    .addOnSuccessListener(unused -> {
                        // Also free up the slot
                        FirebaseFirestore.getInstance().collection("availability").document(appt.getSlotId())
                                .update("isBooked", false);
                        Toast.makeText(context, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                        appointments.remove(position);
                        notifyDataSetChanged();
                    });
        });

        return convertView;
    }
}