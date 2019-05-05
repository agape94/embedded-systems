package com.example.grindingmachine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpeedPofilesCustomAdapter extends RecyclerView.Adapter<SpeedPofilesCustomAdapter.ViewHolder> {
    @NonNull
    @Override
    public SpeedPofilesCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View speedProfileView = inflater.inflate(R.layout.speed_profile, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(speedProfileView);
        return viewHolder;
    }

    public void onBindViewHolder(SpeedPofilesCustomAdapter.ViewHolder viewHolder, int i) {
        SpeedProfile profile = mSpeedProfiles.get(i);
        TextView titleView = viewHolder.mTitleTextView;
        TextView speedView = viewHolder.mSpeedTextView;

        titleView.setText(profile.getTitle());
        speedView.setText(String.valueOf(profile.getSpeed()));

    }

    @Override
    public int getItemCount() {
        return mSpeedProfiles.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView mTitleTextView;
        public TextView mSpeedTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            mTitleTextView = (TextView) itemView.findViewById(R.id.speed_profile_title_text_view);
            mSpeedTextView = (TextView) itemView.findViewById(R.id.speed_profile_rpm_value_text_view);
        }
    }

    // Store a member variable for the contacts
    private List<SpeedProfile> mSpeedProfiles;

    // Pass in the contact array into the constructor
    public SpeedPofilesCustomAdapter(List<SpeedProfile> speedProfiles) {
        mSpeedProfiles = speedProfiles;
    }
}
