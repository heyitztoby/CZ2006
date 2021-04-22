package com.example.parkez;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Resources res = getResources();
        TypedArray vehArray = res.obtainTypedArray(R.array.vehicleList);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }





    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            String vehPref, parkingPref, sortPref;
            //User userPref = new User();

            //userPref.setVehiclePref("Car");
            //userPref.setSortingPref("Distance");
            //userPref.setParkingPref("Electronic");

            SharedPreferences sharedPref = getContext().getSharedPreferences("parkez_preferences", Context.MODE_PRIVATE);

            //vehPref = sharedPref.getString("vehiclePref","");
            //parkingPref = sharedPref.getString("parkingPref","");
            //sortPref = sharedPref.getString("sortPref","");

            //ListPreference vehPreference = findPreference("vehiclePref");
            //ListPreference parkingPreference = findPreference("parkingPref");
            //ListPreference sortPreference = findPreference("sortPref");
            //vehPreference.setSummary(vehPreference.getValue());
            //parkingPreference.setSummary(parkingPreference.getValue());
            //sortPreference.setSummary(sortPreference.getValue());
            //vehPreference.setDefaultValue(vehPref);
            //parkingPreference.setDefaultValue(parkingPref);
            //sortPreference.setDefaultValue(sortPref);
            /*
            vehPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue != vehPref){
                        userPref.setVehiclePref(newValue.toString());
                    }
                    return true;
                }
            });
            */


        }
    }
}