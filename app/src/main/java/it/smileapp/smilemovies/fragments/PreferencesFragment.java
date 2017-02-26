package it.smileapp.smilemovies.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import it.smileapp.smilemovies.R;

/**
 * Created by carde on 24/02/17.
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.prefs, rootKey);
        refreshPreferenceSummaries();

    }

    /**
     * Adds the summaries to all the ListPreferences
     */
    public void refreshPreferenceSummaries() {
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                if (listPreference.getEntry() != null) {
                    preference.setSummary(listPreference.getEntry());
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        refreshPreferenceSummaries();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
