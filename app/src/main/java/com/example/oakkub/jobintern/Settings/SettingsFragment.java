package com.example.oakkub.jobintern.Settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oakkub.jobintern.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by OaKKuB on 8/3/2015.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Bind(R.id.settingsMainToolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.settings_main, container, false);

        ButterKnife.bind(this, rootView);
        setToolbar((AppCompatActivity) getActivity());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

        // set default value
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, true);

        // bindPreferenceSummaryToValue(findPreference(getString(R.string.key_job_type_setting)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notification_settings)), true);

        return rootView;
    }

    private void bindPreferenceSummaryToValue(Preference preference, boolean isBooleanValue) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                !isBooleanValue ?
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getString(preference.getKey(), "")
                        :
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getBoolean(preference.getKey(), true));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        String stringValue = String.valueOf(value);

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    private void setToolbar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
