package com.example.parkez;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkez.Model.CarparkAvailability;
import com.example.parkez.SVY21.LatLonCoordinate;
import com.example.parkez.SVY21.SVY21Coordinate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;
import java.util.Collections;
import java.util.Set;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Pair<Carpark, Float>> carparkInfoList;
    private List<Pair<Carpark, Float>> carparkInfoListTmp;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    @Nullable private HashMap<String, CarparkAvailability.CarparkData> mCarpark;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Pair<Carpark, Float>> carparkInfoList, @Nullable CarparkAvailability cp) {
        this.mInflater = LayoutInflater.from(context);
        this.carparkInfoList = carparkInfoList;
        if (cp == null) {
            this.mCarpark = null;
        } else {
            ArrayList<CarparkAvailability.CarparkData> cpData = cp.getItems().get(0).getCarpark_data();
            this.mCarpark = new HashMap<>();
            for (CarparkAvailability.CarparkData cpD : cpData) this.mCarpark.put(cpD.getCarpark_number(), cpD);
        }
    }

    public void setmCarpark(@Nullable CarparkAvailability cp) {
        if (cp == null) {
            this.mCarpark = null;
            return;
        }
        ArrayList<CarparkAvailability.CarparkData> cpData = cp.getItems().get(0).getCarpark_data();
        this.mCarpark = new HashMap<>();
        for (CarparkAvailability.CarparkData cpD : cpData) this.mCarpark.put(cpD.getCarpark_number(), cpD);
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    private boolean isLotMode = false;
    public void leToggle() {
        Log.d("Adap", "leToggle() = " + isLotMode);
        if (isLotMode) revert();
        else sortByLots();
        isLotMode = !isLotMode;
        notifyDataSetChanged();
    }

    private void sortByLots() {
        if (mCarpark == null) {
            Log.e("Bruh", "Bruh. no carpark lots la");
            return;
        }
        carparkInfoListTmp = new ArrayList<>(carparkInfoList);
        ArrayList<Pair<Carpark, Float>> lots = new ArrayList<>();
        HashMap<Carpark, Float> distData = new HashMap<>();
        for (Pair<Carpark, Float> c : carparkInfoList) {
            Carpark cp = c.first;
            distData.put(cp, c.second);
            if (mCarpark.containsKey(cp.getCarparkNo())) {
                lots.add(new Pair<Carpark, Float>(cp, Float.parseFloat(mCarpark.get(cp.getCarparkNo()).getCarpark_info().get(0).getLots_available() + "")));
            } else lots.add(new Pair<Carpark, Float>(cp, 0f));
        }

        lots.sort(new Comparator<Pair<Carpark, Float>>() {
            @Override
            public int compare(Pair<Carpark, Float> o1, Pair<Carpark, Float> o2) {
                return Float.compare(o2.second, o1.second);
            }
        });

        for (int i = 0; i < lots.size(); i++) {
            Pair<Carpark, Float> f = lots.get(i);
            lots.set(i, new Pair<>(f.first, distData.get(f.first)));
        }

        carparkInfoList = lots;
    }

    private void revert() {
        carparkInfoList = carparkInfoListTmp;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Carpark carpark = carparkInfoList.get(position).first;
        holder.carparkAddrTextView.setText(carpark.getAddress());
        holder.carparkParkingSystemTextView.setText(carpark.getParkingSystemType());
        if (mCarpark == null) {
            // TODO: Do error if not carpark availability data
        } else {
            if (this.mCarpark.containsKey(carpark.getCarparkNo())) {
                CarparkAvailability.CarparkData carparkData = this.mCarpark.get(carpark.getCarparkNo());
                // TODO: Do what you want with this data
                holder.carparkAvailTextView.setText(carparkData.getCarpark_info().get(0).getLots_available());
            } else {
                // TODO: Data not inside you handle yourself
                holder.carparkAvailTextView.setText("NA");
            }
        }

        SVY21Coordinate fuckSingapore = new SVY21Coordinate(carpark.getYCoord(), carpark.getXCoord());
        LatLonCoordinate yayGlobal = fuckSingapore.asLatLon();
        final String directionsUrl = "https://maps.google.com/maps?daddr=" + yayGlobal.getLatitude() + "," + yayGlobal.getLongitude();
        holder.btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(directionsUrl)));
            }
        });
        holder.carparkDistTextView.setText(String.format("%.2f",(carparkInfoList.get(position).second/1000)) + "km");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return carparkInfoList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView carparkAddrTextView;
        TextView carparkAvailTextView;
        TextView carparkDistTextView;
        TextView carparkParkingSystemTextView;
        Button btnDirections;

        ViewHolder(View itemView) {
            super(itemView);
            carparkAddrTextView = itemView.findViewById(R.id.txtCarparkAddress);
            carparkAvailTextView = itemView.findViewById(R.id.txtCarparkAvailability);
            carparkDistTextView = itemView.findViewById(R.id.txtDistance);
            carparkParkingSystemTextView = itemView.findViewById(R.id.txtCarparkType);
            btnDirections = itemView.findViewById(R.id.btnDirection);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Carpark getItem(int id) {
        return carparkInfoList.get(id).first;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
