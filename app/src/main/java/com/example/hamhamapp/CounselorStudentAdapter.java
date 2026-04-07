package com.example.hamhamapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * CounselorStudentAdapter.java
 *
 * Purpose: ArrayAdapter for the student's "Find Counselor" ListView.
 * Binds a list of Counselor objects to the item_counselor layout,
 * populating name, rating, description, and specialty tags.
 */
public class CounselorStudentAdapter extends ArrayAdapter<Counselor> {

    private final Context context;
    private final List<Counselor> counselors;
    private final OnViewTimesClickListener listener;

    public interface OnViewTimesClickListener {
        void onViewTimesClick(Counselor counselor);
    }

    public CounselorStudentAdapter(Context context, List<Counselor> counselors, OnViewTimesClickListener listener) {
        super(context, R.layout.item_counselor, counselors);
        this.context = context;
        this.counselors = counselors;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_counselor, parent, false);
        }

        Counselor counselor = counselors.get(position);

        TextView nameView = convertView.findViewById(R.id.counselorName);
        TextView ratingView = convertView.findViewById(R.id.counselorRating);
        TextView descriptionView = convertView.findViewById(R.id.counselorDescription);
        LinearLayout tagsLayout = convertView.findViewById(R.id.specialtyTags);
        Button viewTimesBtn = convertView.findViewById(R.id.viewAvailableTimesBtn);

        nameView.setText(counselor.getName());
        ratingView.setText(String.format("%.1f/5.0", counselor.getRating()));
        descriptionView.setText(counselor.getDescription());

        // clear old tags and rebuild for this counselor
        tagsLayout.removeAllViews();
        List<String> specialties = counselor.getSpecialties();
        if (specialties != null) {
            int limit = Math.min(specialties.size(), 3);
            for (int i = 0; i < limit; i++) {
                TextView tag = (TextView) LayoutInflater.from(context)
                        .inflate(R.layout.item_specialty_tag, tagsLayout, false);
                tag.setText(specialties.get(i));
                tagsLayout.addView(tag);
            }
        }

        viewTimesBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewTimesClick(counselor);
            }
        });

        return convertView;
    }
}