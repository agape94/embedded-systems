package com.example.grindingmachine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SpeedProfilesActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<SpeedProfile> mSpeedProfiles;
    private SpeedPofilesCustomAdapter mAdapter;
    private int mRequestCode = -1;

    //======================================================================================

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_profiles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadData();
        mListView = (ListView)findViewById(R.id.speed_profiles_recicler_view);
        mAdapter = new SpeedPofilesCustomAdapter(this,mSpeedProfiles);
        mListView.setAdapter(mAdapter);

        Intent intent = getIntent();
        mRequestCode = intent.getIntExtra(Constants.SPEED_PROFILES_ACTIVITY_FOR, -1);
        FloatingActionButton fab = findViewById(R.id.fab);

        if(mRequestCode == Constants.SELECT_SPEED_PROFILE_REQUEST_CODE){
            fab.setVisibility(View.INVISIBLE);

        }else if(mRequestCode == Constants.SAVE_SPEED_PROFILE_REQUEST_CODE){
            SpeedProfile speedProfileToSave = (SpeedProfile) intent.getSerializableExtra(Constants.SPEED_PROFILE_SAVE_KEY);
            if(speedProfileToSave != null){
                mSpeedProfiles.add(speedProfileToSave);
                mAdapter.notifyDataSetChanged();
                saveData();
                setResult(Activity.RESULT_OK);
                finish();
            }
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpeedProfile profileToEdit = mSpeedProfiles.get(position);
                if(mRequestCode == Constants.SELECT_SPEED_PROFILE_REQUEST_CODE) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.SPEED_PROFILE_SELECT_KEY, profileToEdit);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }else if (mRequestCode == Constants.EDIT_VIEW_SPEED_PROFILES_REQUEST_CODE) {
                    showEditSpeedProfileDialog(profileToEdit, Constants.EditOptions.EDIT_SPEED_PROFILE, position);
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SpeedProfile profileToRemove = mSpeedProfiles.get(position);
                if(mRequestCode == Constants.EDIT_VIEW_SPEED_PROFILES_REQUEST_CODE) {
                    showEditSpeedProfileDialog(profileToRemove, Constants.EditOptions.DELETE_SPEED_PROFILE, position);
                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditSpeedProfileDialog(new SpeedProfile(), Constants.EditOptions.NEW_SPEED_PROFILE, 0);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //======================================================================================

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SPEED_PROFILES_SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mSpeedProfiles);
        editor.putString(Constants.SPEED_PROFILES_SHARED_PREF_KEY, json);
        editor.apply();
    }

    //======================================================================================

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

    //======================================================================================

    private void showEditSpeedProfileDialog(final SpeedProfile sp, final Constants.EditOptions option, final int idx){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SpeedProfilesActivity.this);

        if(option != Constants.EditOptions.DELETE_SPEED_PROFILE) {
            View mView = getLayoutInflater().inflate(R.layout.profile_popup_dialog, null);
            final EditText mTitleEditText = (EditText) mView.findViewById(R.id.pop_up_title);
            final EditText mSpeedEditText = (EditText) mView.findViewById(R.id.pop_up_speed);
            TextView mDialogTitle = (TextView) mView.findViewById(R.id.popup_dialog_title);
            switch (option) {
                case NEW_SPEED_PROFILE:
                    mDialogTitle.setText(R.string.new_speed_profile);
                    break;
                case EDIT_SPEED_PROFILE:
                    mDialogTitle.setText(R.string.edit_speed_profile);
                    break;
                default:
                    break;
            }
            mTitleEditText.setText(sp.getTitle());
            mSpeedEditText.setText(String.valueOf(sp.getSpeed()));
            mBuilder.setView(mView);
            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!mTitleEditText.getText().toString().isEmpty() && !mSpeedEditText.getText().toString().isEmpty()) {
                        SpeedProfile profile = new SpeedProfile();
                        profile.setSpeed(Integer.parseInt(mSpeedEditText.getText().toString()));
                        profile.setTitle(mTitleEditText.getText().toString());
                        switch (option) {
                            case NEW_SPEED_PROFILE:
                                mSpeedProfiles.add(profile);
                                break;
                            case EDIT_SPEED_PROFILE:
                                mSpeedProfiles.set(idx, profile);
                                break;
                            default:
                                break;
                        }
                        mAdapter.notifyDataSetChanged();
                        saveData();
                    }

                }
            });

            mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if (option == Constants.EditOptions.DELETE_SPEED_PROFILE)
        {
            mBuilder.setTitle("Confirm");
            mBuilder.setMessage("Are you sure you want to delete this profile?" + "\n" + sp.getTitle());

            mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    mSpeedProfiles.remove(idx);
                    mAdapter.notifyDataSetChanged();
                    saveData();
                }
            });

            mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

        }
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    //======================================================================================

    public void addNewProfile(SpeedProfile sp){
        if(sp != null){
            mSpeedProfiles.add(sp);
            mAdapter.notifyDataSetChanged();
            saveData();
        }
    }

    //======================================================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            setResult(Activity.RESULT_CANCELED);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
