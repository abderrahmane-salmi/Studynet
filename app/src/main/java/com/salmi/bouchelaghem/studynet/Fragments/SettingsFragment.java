package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.salmi.bouchelaghem.studynet.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefrences, rootKey);
    }
}
