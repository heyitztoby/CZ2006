package com.example.parkez.registration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkez.R;

import androidx.annotation.IdRes;
import androidx.preference.PreferenceManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class Vehicle extends Fragment {

    private static final String TAG = "vehicle preference";

    private PageViewModel pageViewModel;
    private String userVehicle;

    public Vehicle() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment Vehicle.
     */
    public static Vehicle newInstance() {
        return new Vehicle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        pageViewModel.setIndex(TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.activity_registration_vehicle, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        ChipGroup choiceChipGroup = (ChipGroup) root.findViewById(R.id.vehicleGroup);
        choiceChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, @IdRes int i) {
                Chip vehicle = chipGroup.findViewById(i);
                if(vehicle != null) {
                    userVehicle = null;
                    userVehicle = vehicle.getText().toString();
                    //userPref.setVehiclePref(userVehicle);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("vehiclePref",userVehicle);
                    editor.commit();
                    //Toast.makeText(getContext(), vehicle.getText().toString(),Toast.LENGTH_LONG).show(); //to test saved string
                }
            }
        });

        return root;
    }




}
