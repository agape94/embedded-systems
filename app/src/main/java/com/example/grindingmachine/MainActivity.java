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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NumberPicker mHundredsNumberPicker;
    private NumberPicker mTensNumberPicker;
    private NumberPicker mUnitsNumberPicker;
    private EditText mSetSpeedValue;
    private int currentRPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentRPM = 500;

        mSetSpeedValue = findViewById(R.id.rpm_edit_text);
        mSetSpeedValue.setText(String.valueOf(currentRPM));

        mHundredsNumberPicker = findViewById(R.id.hundreds_number_picker);
        mTensNumberPicker = findViewById(R.id.tens_number_picker);
        mUnitsNumberPicker = findViewById(R.id.units_number_picker);

        mHundredsNumberPicker.setMinValue(0);
        mHundredsNumberPicker.setMaxValue(9);

        mTensNumberPicker.setMinValue(0);
        mTensNumberPicker.setMaxValue(9);

        mUnitsNumberPicker.setMinValue(0);
        mUnitsNumberPicker.setMaxValue(9);

        mHundredsNumberPicker.setClickable(false);
        mTensNumberPicker.setClickable(false);
        mUnitsNumberPicker.setClickable(false);

        mHundredsNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String currentRpm = mSetSpeedValue.getText().toString();
                mSetSpeedValue.setText(replaceCharAtPos(currentRpm, 0, (char)(newVal + '0')));
            }
        });

        mTensNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String currentRpm = mSetSpeedValue.getText().toString();
                mSetSpeedValue.setText(replaceCharAtPos(currentRpm, 1, (char)(newVal + '0')));
            }
        });

        mUnitsNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String currentRpm = mSetSpeedValue.getText().toString();
                mSetSpeedValue.setText(replaceCharAtPos(currentRpm, 2, (char)(newVal + '0')));
            }
        });
        mSetSpeedValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                syncronizeRPMValue();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void syncronizeRPMValue(){
        char hundredsChar = '0';
        char tensChar = '0';
        char unitsChar = '0';

        String currentRpmValue = mSetSpeedValue.getText().toString();

        if(currentRpmValue.length() == 3) {
            hundredsChar = currentRpmValue.charAt(0);
            tensChar = currentRpmValue.charAt(1);
            unitsChar = currentRpmValue.charAt(2);
        }else if(currentRpmValue.length() == 2)
        {
            tensChar = currentRpmValue.charAt(0);
            unitsChar = currentRpmValue.charAt(1);
        }else if(currentRpmValue.length() == 1)
        {
            unitsChar = currentRpmValue.charAt(0);
        }else{
            Toast.makeText(this,"Could not parse the RPM!", Toast.LENGTH_SHORT);
            return;
        }

        int hundreds = (int) hundredsChar - '0';
        int tens = (int) tensChar - '0';
        int units = (int) unitsChar - '0';

        mHundredsNumberPicker.setValue((int) hundredsChar - '0');
        mTensNumberPicker.setValue((int) tensChar - '0');
        mUnitsNumberPicker.setValue((int) unitsChar - '0');
    }

    private String replaceCharAtPos(String oldString, int pos, char charToReplace){
//        char[] myNameChars = oldString.toCharArray();
//        myNameChars[pos] = charToReplace;
//        String newString = String.valueOf(myNameChars);
//        return newString;
        StringBuilder old = new StringBuilder(oldString);
        old.setCharAt(pos, charToReplace);
        return old.toString();
    }
}
