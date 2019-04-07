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
import java.util.List;

public class DigitAdapter extends ArrayAdapter {
    public DigitAdapter(@NonNull Context context, @NonNull ArrayList<String> digitsList) {
        super(context,0, digitsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        String digit = (String) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.digit_list_item, parent, false);
        }

        TextView digitTextView = convertView.findViewById(R.id.digit_text_view);
        digitTextView.setText(digit);

        return convertView;
    }
}
