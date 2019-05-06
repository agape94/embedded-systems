package com.example.grindingmachine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    //======================================================================================

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        final EditTextPreference minimumRpmEditText = (EditTextPreference)
                getPreferenceScreen().findPreference(Constants.MINIMUM_RPM_KEY);
        minimumRpmEditText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int value = 0;
                if(!minimumRpmEditText.getEditText().getText().toString().isEmpty()) {
                    value = Integer.parseInt(minimumRpmEditText.getEditText().getText().toString());
                    if (value < Constants.MAXIMUM_RPM_VALUE) {
                        updateEditPreference(Constants.MINIMUM_RPM_KEY, value);
                        return true;
                    } else {
                        Toast.makeText(getContext(), "The minimum RPM value should be less than " + Constants.MAXIMUM_RPM_VALUE, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return false;
            }
        });

        final EditTextPreference maximumRpmEditText = (EditTextPreference)
                getPreferenceScreen().findPreference(Constants.MAXIMUM_RPM_KEY);
        maximumRpmEditText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int value = 0;
                if(!maximumRpmEditText.getEditText().getText().toString().isEmpty()) {
                    value = Integer.parseInt(maximumRpmEditText.getEditText().getText().toString());
                    if (value > Constants.MINIMUM_RPM_VALUE) {
                        updateEditPreference(Constants.MAXIMUM_RPM_KEY, value);
                        return true;
                    } else {
                        Toast.makeText(getContext(), "The maximum RPM value should be more than " + Constants.MINIMUM_RPM_VALUE, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return false;
            }
        });
    }

    //======================================================================================

    public SettingsFragment() {
        // Required empty public constructor
    }

    //======================================================================================

    void updateEditPreference(String key, int value){
        SharedPreferences settingsSharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settingsSharedPref.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }
}
