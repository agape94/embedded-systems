package com.example.grindingmachine;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SpeedProfilesActivity extends AppCompatActivity {

    private RecyclerView mReciclerView;
    private ArrayList<SpeedProfile> mSpeedProfiles;
    private SpeedPofilesCustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_profiles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadData();
        mReciclerView = (RecyclerView)findViewById(R.id.speed_profiles_recicler_view);
        mAdapter = new SpeedPofilesCustomAdapter(mSpeedProfiles);
        mReciclerView.setAdapter(mAdapter);
        mReciclerView.setLayoutManager(new LinearLayoutManager(this));


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SpeedProfilesActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.profile_popup_dialog, null);
                final EditText mTitleTV = (EditText) mView.findViewById(R.id.pop_up_title);
                final EditText mSpeedTV = (EditText) mView.findViewById(R.id.pop_up_speed);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO save to file
                        if(!mTitleTV.getText().toString().isEmpty() && !mSpeedTV.getText().toString().isEmpty()) {
                            mSpeedProfiles.add(new SpeedProfile(
                                    mTitleTV.getText().toString(),
                                    Integer.parseInt(mSpeedTV.getText().toString())
                            ));
                            mAdapter.notifyItemChanged(mSpeedProfiles.size()-1);
                            saveData();
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"One field is empty",Toast.LENGTH_SHORT);
                        }
                    }
                });

                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SPEED_PROFILES_SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mSpeedProfiles);
        editor.putString(Constants.SPEED_PROFILES_SHARED_PREF_KEY, json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SPEED_PROFILES_SHARED_PREF_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.SPEED_PROFILES_SHARED_PREF_KEY, null);
        Type type = new TypeToken<ArrayList<SpeedProfile>>() {}.getType();
        mSpeedProfiles = gson.fromJson(json, type);

        if (mSpeedProfiles == null) {
            mSpeedProfiles = new ArrayList<>();
        }
    }

}
