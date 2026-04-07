package com.example.hamhamapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * CounselorAdminAdapter.java
 *
 * Purpose: ArrayAdapter for the admin's "Manage Counselors" ListView.
 * Binds a list of Counselor objects to the item_counselor_adming layout,
 * populating name, email, active/inactive status, and specialty tags.
 */
public class CounselorAdminAdapter extends ArrayAdapter<Counselor> {

    private final Context        context;
    private final List<Counselor> counselors;

    public CounselorAdminAdapter(Context context, List<Counselor> counselors) {
        super(context, R.layout.item_counselor_adming, counselors);
        this.context   = context;
        this.counselors = counselors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_counselor_adming, parent, false);
        }

        Counselor counselor = counselors.get(position);

        TextView nameView    = convertView.findViewById(R.id.counselorName);
        TextView emailView   = convertView.findViewById(R.id.counselorEmail);
        TextView statusView  = convertView.findViewById(R.id.counselorStatus);
        LinearLayout tagsLayout = convertView.findViewById(R.id.specialtyTags);

        nameView.setText(counselor.getName());
        emailView.setText(counselor.getEmail());

        // Update status badge based on isActive field
        if (counselor.getIsActive()) {
            statusView.setText("Active");
            statusView.setTextColor(context.getResources().getColor(R.color.counselor_green, null));
            statusView.setBackgroundResource(R.drawable.icon_bg_green);
        } else {
            statusView.setText("Inactive");
            statusView.setTextColor(context.getResources().getColor(R.color.text_gray, null));
            statusView.setBackgroundResource(R.drawable.icon_bg_gray);
        }

        // clear old tags and rebuild for this counselor
        tagsLayout.removeAllViews();
        List<String> specialties = counselor.getSpecialties();
        if (specialties != null) {
            int limit = Math.min(specialties.size(), 2);
            for (int i = 0; i < limit; i++) {
                TextView tag = (TextView) LayoutInflater.from(context)
                        .inflate(R.layout.item_specialty_tag, tagsLayout, false);
                tag.setText(specialties.get(i));
                tagsLayout.addView(tag);
            }
        }

        return convertView;
    }
}