package de.reikodd.meinwidget;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;


public class SettingActivity extends Activity {


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.drawable.ic_action_settings);
        setContentView(R.layout.settinglayout);
    }

    public static class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle saveInstanceState) {

            super.onCreate(saveInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference p2 = findPreference("version");
            p2.setSummary(BuildConfig.VERSION_NAME);
            Preference p3 = findPreference("birthday");
            p3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent().setAction("de.reikodd.meinwidget.SyncBirthday");
                    getActivity().sendBroadcast(i);
                    return false;
                }
            });

            onSharedPreferenceChanged(null, "TimeDiffStart");
            onSharedPreferenceChanged(null, "WeatherActString");
            onSharedPreferenceChanged(null, "WeatherPlaceStart");
            onSharedPreferenceChanged(null, "WeatherCountryStart");
            onSharedPreferenceChanged(null, "SunUpdateStart");
            onSharedPreferenceChanged(null, "patternBirthday");
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            ListPreference lp1 = (ListPreference) findPreference("TimeOffset");
            ListPreference lp2 = (ListPreference) findPreference("WeatherTimeAct");
            EditTextPreference etp1 = (EditTextPreference) findPreference("WeatherPlace");
            EditTextPreference etp2 = (EditTextPreference) findPreference("WeatherCountry");
            Preference p1 = findPreference("sun");
            Preference p3 = findPreference("birthday");
            etp2.setEnabled(false);


            if (key.equals("TimeOffset")) {
                Intent i = new Intent().setAction("de.reikodd.meinwidget.TimeDiffAct");
                getActivity().sendBroadcast(i);
                lp1.setSummary(lp1.getEntry());
            }

            if (key.equals("WeatherTimeAct")) {
                Intent i = new Intent().setAction("de.reikodd.meinwidget.WeatherAct");
                getActivity().sendBroadcast(i);
            }

            if (key.equals("WeatherActString")) {
                lp2.setSummary(settings.getString(key, ""));
            }

            if (key.equals("WeatherPlace")) {
                Intent i = new Intent().setAction("de.reikodd.meinwidget.WeatherAct");
                getActivity().sendBroadcast(i);
                etp1.setSummary(etp1.getText());
            }

            if (key.equals("WeatherCountry")) {
                Intent i = new Intent().setAction("de.reikodd.meinwidget.WeatherAct");
                getActivity().sendBroadcast(i);
                etp2.setSummary(etp2.getText());
            }

            //bei Start der SettingActivity nur Summary aktualisieren
            if (key.equals("TimeDiffStart")) {
                lp1.setSummary(lp1.getEntry());
            }

            if (key.equals("WeatherPlaceStart")) {
                etp1.setSummary(etp1.getText());
            }

            if (key.equals("WeatherCountryStart")) {
                etp2.setSummary(etp2.getText());
            }

            if (key.equals("SunUpdateStart")) {
                p1.setSummary(settings.getString(key, ""));
            }

            if (key.equals("patternBirthday")) {
                p3.setSummary(settings.getString(key, ""));
            }
        }
    }
}