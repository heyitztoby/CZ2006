package com.example.parkez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import com.example.parkez.Model.CarparkAvailability;
import com.example.parkez.SVY21.LatLonCoordinate;
import com.example.parkez.SVY21.SVY21Coordinate;
import com.example.parkez.registration.RegistrationActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, MyRecyclerViewAdapter.ItemClickListener,BlankFragment.OnFragmentInteractionListener{

    private Location location;
    private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private RecyclerView recyclerView;
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    DBHelper myDbHelper;
    Boolean isUserRegistered;
    SharedPreferences prefs;

    private FrameLayout fragmentContainer;
    private ImageButton button;


    //RecyclerView
    MyRecyclerViewAdapter adapter;
    private ArrayList<String> carparkNoList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("userPref", MODE_PRIVATE);
        isUserRegistered = prefs.getBoolean("userRegistered", false); //False is a default value

        //If user is not registered
        if (!isUserRegistered) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            startActivity(new Intent(this, RegistrationActivity.class));
        }

        //If user is registered
        else {
            setContentView(R.layout.activity_main);

            // Leroy Edit
            fragmentContainer =(FrameLayout)findViewById(R.id.fragment_container);
            button=(ImageButton)findViewById(R.id.helpBtn);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFragment();
                }
            });


            ImageView cat = findViewById(R.id.imageView2); //store images in res.drawable & retrieve element layout
            cat.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher)); // CTRL + Q to view documentation || CTRL + CLICK to enter function

            //View more car park button
            Button btnViewMoreCarpark = findViewById(R.id.viewMoreCarparkBtn);
            btnViewMoreCarpark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(v.getContext(), ViewMoreCarpark.class));
                }
            });

            locationTv = findViewById(R.id.lblDisplayCurrentLoc);
            // we add permissions we need to request location of the users
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            permissionsToRequest = permissionsToRequest(permissions);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.
                            toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                }
            }

            // we build google api client
            googleApiClient = new GoogleApiClient.Builder(this).
                    addApi(LocationServices.API).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).build();

            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //startActivity(new Intent(this, RegistrationActivity.class)); //to change activity

            //copying assets/db file to phone's data/data
            try {
                // CHECK IS EXISTS OR NOT
                File f = getDatabasePath("hdb-carpark-information.db");
                if (!f.exists()) {
                    // COPY IF NOT EXISTS
                    AssetManager am = getApplicationContext().getAssets();
                    OutputStream os = new FileOutputStream(getDatabasePath("hdb-carpark-information.db"));
                    byte[] b = new byte[100];
                    int r;
                    InputStream is = am.open("hdb-carpark-information.db");
                    while ((r = is.read(b)) != -1) {
                        os.write(b, 0, r);
                    }
                    is.close();
                    os.close();
                }
            } catch (Exception e) {

            }

            // set up the RecyclerView

            recyclerView = findViewById(R.id.rvCarparkView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "Hello Clarence", Toast.LENGTH_LONG).show();
            finish();
            return true;
        } else
        return super.onOptionsItemSelected(item);
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MainActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }

                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
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
            locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
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
                locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
                getNearbyCarpark(location);
            }
        });

        /*location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }*/

        startLocationUpdates();
    }

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

        if (distances.size() > 3) {
            List<Pair<Carpark, Float>> lim = distances.subList(0, 3);

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


        findViewById(R.id.sortBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.leToggle();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private SharedPreferences sp;

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

    //Recycler View
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, adapter.getItem(position).getAddress(), Toast.LENGTH_SHORT).show();
        String carparkID = adapter.getItem(position).getCarparkNo();
        Intent intent = new Intent(this, ViewCarparkInformation.class);
        intent.putExtra("carparkid", carparkID);
        startActivity(intent);
    }

    public void btnSettings_onClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        //intent.putExtra("carparkid", "carparkid here");
        startActivity(intent);


        // Other side
        //String carparkid = getIntent().getStringExtra("carparkid");
    }

    public void openFragment()
    {
        final BlankFragment fragment = new BlankFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,
                R.anim.enter_from_right,R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container,fragment,"BLANK_FRAGMENT").commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
    }


}
