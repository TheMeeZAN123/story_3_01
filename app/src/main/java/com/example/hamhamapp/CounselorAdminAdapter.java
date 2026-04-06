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
 * Specialty tags are added programmatically because their count varies
 * per counselor. Follows the View layer of the MVC pattern.
 *
 * Outstanding issues: None.
 */
public class CounselorAdminAdapter extends ArrayAdapter<Counselor> {

    private final Context        context;
    private final List<Counselor> counselors;

    /**
     * Constructs the adapter.
     *
     * @param context   Calling Activity context.
     * @param counselors List of counselors to display.
     */
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

        // status badge — placeholder; Firestore field "accountStatus" would drive this
        statusView.setText("Active");

        // clear old tags and rebuild for this counselor
        tagsLayout.removeAllViews();
        List<String> specialties = counselor.getSpecialties();
        if (specialties != null) {
            // show at most 2 specialty tags to keep rows compact
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