package com.example.parkez;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkez.Model.CarparkAvailability;
import com.example.parkez.SVY21.LatLonCoordinate;
import com.example.parkez.SVY21.SVY21Coordinate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewMoreCarpark extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, MyRecyclerViewAdapter.ItemClickListener{

    DBHelper myDbHelper;
    //RecyclerView
    MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<String> carparkNoList = new ArrayList<String>();

    private Location location;
    //private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more_capark);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set up the RecyclerView

        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

        recyclerView = findViewById(R.id.rvMoreCarparkView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Toast.makeText(this, "Hello Clarence", Toast.LENGTH_LONG).show();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private SharedPreferences sp;

    private void getNearbyCarpark(Location yourLocation) {
        myDbHelper = new DBHelper(getApplicationContext());
        myDbHelper.openDatabase();
        ArrayList<Carpark> carparkInfoList = myDbHelper.getAllCarparks();
        ArrayList<Pair<Carpark, Float>> distances = new ArrayList<>();
        for (Carpark c : carparkInfoList) {
            if (yourLocation == null) continue;
            SVY21Coordinate fuckSingapore = new SVY21Coordinate(c.getYCoord(), c.getXCoord());
            LatLonCoordinate hooray = fuckSingapore.asLatLon();
            float[] res = new float[1];
            Location.distanceBetween(yourLocation.getLatitude(), yourLocation.getLongitude(), hooray.getLatitude(), hooray.getLongitude(), res);
            distances.add(new Pair<>(c, res[0]));
        }
        distances.sort(new Comparator<Pair<Carpark, Float>>() {
            @Override
            public int compare(Pair<Carpark, Float> o1, Pair<Carpark, Float> o2) {
                return Float.compare(o1.second, o2.second);
            }
        });

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String filterBy = sp.getString("parkingPref", null);
        if (filterBy != null) {
            ArrayList<Pair<Carpark, Float>> toFilter = new ArrayList<>();
            for (Pair<Carpark, Float> f : distances) {
                switch (filterBy) {
                    case "Coupon":
                        if (f.first.getParkingSystemType().equalsIgnoreCase("COUPON PARKING")) toFilter.add(f);
                        break;
                    case "Electronic":
                        if (f.first.getParkingSystemType().equalsIgnoreCase("ELECTRONIC PARKING")) toFilter.add(f);
                        break;
                }
            }
            distances = toFilter;
        }

        if (distances.size() > 9) {
            List<Pair<Carpark, Float>> lim = distances.subList(0, 10);

            new GetCarparkData(new GetCarparkData.Callback() {
                @Override
                public void onCallback(@Nullable CarparkAvailability availability) {
                    adapter.setmCarpark(availability);
                    String toggleBy = sp.getString("sortPref", null);
                    if(toggleBy.equalsIgnoreCase("Available lots")){
                        adapter.leToggle();
                        adapter.notifyDataSetChanged();
                    }
                    adapter.notifyDataSetChanged();
                }
            }).execute();
            adapter = new MyRecyclerViewAdapter(this, lim, null);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);


        } else {
            Log.wtf("WTF", "NANITF WHY NO CARPARK");
        }


        findViewById(R.id.btnToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.leToggle();
                adapter.notifyDataSetChanged();
            }
        });

    }




    public void onItemClick(View view, int position) {
        Toast.makeText(this, adapter.getItem(position).getAddress(), Toast.LENGTH_SHORT).show();
        String carparkID = adapter.getItem(position).getCarparkNo();
        Intent intent = new Intent(this, ViewCarparkInformation.class);
        intent.putExtra("carparkid", carparkID);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            //locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
                getNearbyCarpark(location);
            }
        });

        /*location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }*/

        startLocationUpdates();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

}
