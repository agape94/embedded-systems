package com.example.grindingmachine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DigitListView mHundredsListView;
    DigitListView mTensListView;
    DigitListView mUnitsListView;

    ArrayList<String> mDigits;
    ArrayList<String> mDigits0;

    DigitAdapter mHundredsAdapter;
    DigitAdapter mTensAdapter;
    DigitAdapter mUnitsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDigits0 = new ArrayList<String>();
        mDigits = new ArrayList<String>();

        mDigits.add("1");
        mDigits.add("2");
        mDigits.add("3");
        mDigits.add("4");
        mDigits.add("5");
        mDigits.add("6");
        mDigits.add("7");
        mDigits.add("8");
        mDigits.add("9");

        mDigits0.add("0");
        mDigits0.add("1");
        mDigits0.add("2");
        mDigits0.add("3");
        mDigits0.add("4");
        mDigits0.add("5");
        mDigits0.add("6");
        mDigits0.add("7");
        mDigits0.add("8");
        mDigits0.add("9");


        mHundredsListView = findViewById(R.id.hundreds_list_view);
        mTensListView = findViewById(R.id.tens_list_view);
        mUnitsListView = findViewById(R.id.units_list_view);

        mHundredsAdapter = new DigitAdapter(this, mDigits);
        mTensAdapter = new DigitAdapter(this, mDigits0);
        mUnitsAdapter = new DigitAdapter(this, mDigits0);

//        mHundredsAdapter.setmCount(3);
//        mTensAdapter.setmCount(3);
//        mUnitsAdapter.setmCount(3);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).

        mHundredsListView.setAdapter(mHundredsAdapter);
        mTensListView.setAdapter(mTensAdapter);
        mUnitsListView.setAdapter(mUnitsAdapter);
    }
}
