package com.example.grindingmachine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class SpeedPofilesCustomAdapter extends ArrayAdapter<SpeedProfile> {


    public SpeedPofilesCustomAdapter(@NonNull Context context, @NonNull ArrayList<SpeedProfile> speedProfilesList) {
        super(context,0, speedProfilesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.speed_profile, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.speed_profile_title_text_view);
        TextView speedTextView = convertView.findViewById(R.id.speed_profile_rpm_value_text_view);

        SpeedProfile sp = getItem(position);
        if(sp == null) {
            return  convertView;
        }
        titleTextView.setText(sp.getTitle());
        speedTextView.setText(String.valueOf(sp.getSpeed()));

        return convertView;
    }
}
