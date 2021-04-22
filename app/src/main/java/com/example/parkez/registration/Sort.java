package com.example.parkez.registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkez.R;

import androidx.annotation.IdRes;
import androidx.preference.PreferenceManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class Sort extends Fragment {

  private static final String TAG = "sort preference";

  private PageViewModel pageViewModel;
  private String userSort;

  public Sort() {
    // Required empty public constructor
  }

  /**
   * @return A new instance of fragment Sort.
   */
  public static Sort newInstance() {
    return new Sort();
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
    View root = inflater.inflate(R.layout.activity_registration_sort, container, false);
    final TextView textView = root.findViewById(R.id.section_label);
    pageViewModel.getText().observe(this, new Observer<String>() {
      @Override
      public void onChanged(@Nullable String s) {
        textView.setText(s);
      }
    });

    ChipGroup choiceChipGroup = (ChipGroup) root.findViewById(R.id.sortGroup);
    choiceChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(ChipGroup chipGroup, @IdRes int i) {
        Chip sort = chipGroup.findViewById(i);
        if(sort != null) {
          userSort = null;
          userSort = sort.getText().toString();
          //userPref.setSortingPref(userSort);
          SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
          SharedPreferences.Editor editor = sharedPref.edit();
          editor.putString("sortPref",userSort);
          editor.commit();

          //Toast.makeText(getContext(), sort.getText().toString(),Toast.LENGTH_LONG).show(); //to test saved string
        }
      }
    });

    Button btn = (Button) root.findViewById(R.id.done);

    btn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(Sort.this.getActivity(), Complete.class));
      }
    });


    return root;
  }
}
