package com.example.parkez;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkez.Model.CarparkAvailability;
import com.example.parkez.SVY21.LatLonCoordinate;
import com.example.parkez.SVY21.SVY21Coordinate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ViewCarparkInformation extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, MyRecyclerViewAdapter.ItemClickListener, BlankFragment2.OnFragmentInteractionListener{


    private FrameLayout fragmentContainer2;
    private ImageButton button2;

    private GoogleMap mMap;
    DBHelper myDbHelper;

    private Location location;
    private LatLng cpLocation;
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
    @Nullable private HashMap<String, CarparkAvailability.CarparkData> mCarpark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_carpark_information);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.mCarpark = new HashMap<>();



        fragmentContainer2 =(FrameLayout) findViewById(R.id.fragment_container1);
        button2 = (ImageButton) findViewById(R.id.helpButton);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment2();
            }
        });

        //locationTv = findViewById(R.id.lblDisplayCurrentLoc);
        // we add permissions we need to request location of the users
        //permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        //permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        //permissionsToRequest = permissionsToRequest(permissions);
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.
                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }


 */
        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //startActivity(new Intent(this, RegistrationActivity.class)); //to change activity

        //copying assets/db file to phone's data/data
        /*
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


         */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*if(cpLocation != null) {

            mMap = googleMap;

            // Add a marker in Sydney and move the camera
//            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            //LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(cpLocation).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cpLocation, 18f));



        }
        else{
            *//*
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            //LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng hall = new LatLng(1.354342, 103.6846703);
            mMap.addMarker(new MarkerOptions().position(hall).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(hall));

             *//*
        }*/
        mMap = googleMap;
        // Permissions ok, we get last location
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
                ViewCarparkInformation.this.location = location;
                getNearbyCarpark(location);
            }
        });

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
                            new AlertDialog.Builder(ViewCarparkInformation.this).
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }*/

        startLocationUpdates();
    }

    private void getSpecificCarpark(Location yourLocation){

        myDbHelper = new DBHelper(getApplicationContext());
        myDbHelper.openDatabase();

        String carparkid = getIntent().getStringExtra("carparkid");

    }

    private void getNearbyCarpark(Location yourLocation) {
        Carpark selectedCarpark = null;
        double distance = 0.0;

        myDbHelper = new DBHelper(getApplicationContext());
        myDbHelper.openDatabase();
        ArrayList<Carpark> carparkInfoList = myDbHelper.getAllCarparks();
        String carparkid = getIntent().getStringExtra("carparkid");
        for(Carpark c : carparkInfoList){
            if (carparkid.equals(c.getCarparkNo())){
                selectedCarpark = c;
            }
        }
        if(yourLocation != null && selectedCarpark != null) {
            SVY21Coordinate fuckSingapore = new SVY21Coordinate(selectedCarpark.getYCoord(), selectedCarpark.getXCoord());
            LatLonCoordinate hooray = fuckSingapore.asLatLon();
            this.cpLocation = new LatLng(hooray.getLatitude(), hooray.getLongitude());
            float[] res = new float[1];
            Location.distanceBetween(yourLocation.getLatitude(), yourLocation.getLongitude(), hooray.getLatitude(), hooray.getLongitude(), res);
            distance = res[0];
            final String directionsUrl = "https://maps.google.com/maps?daddr=" + hooray.getLatitude() + "," + hooray.getLongitude();
            findViewById(R.id.btnDirection).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(directionsUrl)));
                }
            });
        }

        TextView carparkAddrTextView;
        carparkAddrTextView = findViewById(R.id.txtCarparkAddress);
        carparkAddrTextView.setText(selectedCarpark.getAddress());

        TextView carparkTypeTextView;
        carparkTypeTextView = findViewById(R.id.lblTypeOfCarpark);
        carparkTypeTextView.setText(selectedCarpark.getParkingSystemType());

        TextView carparkFloorTextView;
        carparkFloorTextView = findViewById(R.id.lblFloor);
        carparkFloorTextView.setText(String.valueOf(selectedCarpark.getCarparkDecks())+" floor(s)");

        TextView carparkHeightTextView;
        carparkHeightTextView = findViewById(R.id.lblCarparkHeight);
        float gantryHeight = (float)selectedCarpark.getGantryHeight();
        carparkHeightTextView.setText(String.valueOf(gantryHeight) + "m");

       // TextView carparkOpeningHoursTextView;
        //carparkOpeningHoursTextView = findViewById(R.id.lblOpeningHours);
        //carparkOpeningHoursTextView.setText(selectedCarpark.getShortTermParking());

        Random carLot = new Random();
        Random bikeLot = new Random();
        Random lorryLot = new Random();

        int carLots = carLot.nextInt(50-0) + 0;
        int bikeLots = bikeLot.nextInt(30-0) + 0;
        int lorryLots = lorryLot.nextInt(15-0) + 0;

        TextView availCarLots;
        availCarLots = findViewById(R.id.lblCarLots);
        availCarLots.setText(String.valueOf(carLots));

        TextView availBikeLots;
        availBikeLots = findViewById(R.id.lblBikeLots);
        availBikeLots.setText(String.valueOf(bikeLots));

        TextView availLorryLots;
        availLorryLots = findViewById(R.id.lblLorryLots);
        availLorryLots.setText(String.valueOf(lorryLots));


        mMap.addMarker(new MarkerOptions().position(cpLocation).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cpLocation, 18f));


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

    private void runUIDisplay(ArrayList<Carpark> carparkInfoList){
/*
        Carpark carpark = carparkInfoList.get(position).first;
        holder.carparkAddrTextView.setText(carpark.getAddress());
        holder.carparkParkingSystemTextView.setText(carpark.getParkingSystemType());
        if (mCarpark == null) {
            // TODO: Do error if not carpark avaiability data
        } else {
            if (this.mCarpark.containsKey(carpark.getCarparkNo())) {
                CarparkAvailability.CarparkData carparkData = this.mCarpark.get(carpark.getCarparkNo());
                // TODO: Do what you want with this data
                holder.carparkAvailTextView.setText(carparkData.getCarpark_info().get(0).getLots_available());
            } else {
                // TODO: Data not inside you handle yourself
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
        holder.carparkDistTextView.setText(carparkInfoList.get(position).second + "m");
         */

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

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        }, null);
        //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            //LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            //LatLng sydney = new LatLng(-34, 151);
            //mMap.addMarker(new MarkerOptions().position(current).title("Marker"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        }
    }

    public void onItemClick(View view, int position) {
        /*
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        String carparkID = adapter.getItem(position).getCarparkNo();
        Intent intent = new Intent(this, ViewCarparkInformation.class);
        intent.putExtra("carparkid", carparkID);
        startActivity(intent);

         */
    }

    public void openFragment2()
    {
        BlankFragment2 fragment2=new BlankFragment2();
        FragmentManager fragmentManager2=getSupportFragmentManager();
        FragmentTransaction transaction2 = fragmentManager2.beginTransaction();
        transaction2.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,
                R.anim.enter_from_right,R.anim.exit_to_right);
        transaction2.addToBackStack(null);
        transaction2.add(R.id.fragment_container1,fragment2,"BLANK_FRAGMENT2").commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
    }
}
