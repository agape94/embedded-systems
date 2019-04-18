package com.example.grindingmachine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SeekBar mMotorSpeedSeekbar;
    private EditText mSetSpeedValue;
    private int currentRPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentRPM = 500;

        mSetSpeedValue = findViewById(R.id.rpm_edit_text);
        mSetSpeedValue.setText(String.valueOf(currentRPM));

        mMotorSpeedSeekbar = findViewById(R.id.motorSpeedSeekBar);

        mMotorSpeedSeekbar.setMin(0);
        mMotorSpeedSeekbar.setMax(999); //TODO Set to configurable value. Motor specific value

        mSetSpeedValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeSeekBarValue();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mMotorSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int currentRpmValue = seekBar.getProgress();
                if(!mSetSpeedValue.getText().toString().isEmpty()) {
                    if (currentRpmValue != Integer.parseInt(mSetSpeedValue.getText().toString())) {
                        mSetSpeedValue.setText(String.valueOf(currentRpmValue));
                        mSetSpeedValue.setSelection(mSetSpeedValue.getText().toString().length());
                    }else {
                        return;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void changeSeekBarValue(){

        int currentRpmValue;
        if(!mSetSpeedValue.getText().toString().isEmpty()) {
            currentRpmValue = Integer.parseInt(mSetSpeedValue.getText().toString());
            mMotorSpeedSeekbar.setProgress(currentRpmValue, true);
        }else{
            mMotorSpeedSeekbar.setProgress(0, true);
        }
    }
}
