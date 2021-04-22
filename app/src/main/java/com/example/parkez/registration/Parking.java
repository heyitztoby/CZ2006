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

public class Parking extends Fragment {

  private static final String TAG = "parking preference";

  private PageViewModel pageViewModel;
  private String userParking;

  public Parking() {
  }

  /**
   * @return A new instance of fragment Parking.
   */
  public static Parking newInstance() {
    return new Parking();
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
    View root = inflater.inflate(R.layout.activity_registration_parking, container, false);
    final TextView textView = root.findViewById(R.id.section_label);
    pageViewModel.getText().observe(this, new Observer<String>() {
      @Override
      public void onChanged(@Nullable String s) {
        textView.setText(s);
      }
    });

    ChipGroup choiceChipGroup = (ChipGroup) root.findViewById(R.id.parkingGroup);
    choiceChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(ChipGroup chipGroup, @IdRes int i) {
        Chip parking = chipGroup.findViewById(i);
        if(parking != null) {
          userParking = null;
          userParking = parking.getText().toString();
          SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
          SharedPreferences.Editor editor = sharedPref.edit();
          editor.putString("parkingPref",userParking);
          editor.commit();
          //Toast.makeText(getContext(), parking.getText().toString(),Toast.LENGTH_LONG).show(); //to test saved string
        }
      }
    });

    return root;
  }
}
