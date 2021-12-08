package com.example.opsc7312_poe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    TextView txtLat, txtLon, txtAcc, txtAlt, txtSpd, txtUp, txtAdd, txtSens, txtWaypointCounts;
    private Button logout, btnNewWaypoint, btnShowWaypoints, btnShowMap;

    Switch swtLocUp, swtGPS;

    boolean updateOn = false;

    Location currLocation;
    List<Location> savedLocations;

    LocationRequest locationRequest;
    LocationCallback locationCallback;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        txtLat = findViewById(R.id.tv_lat);
        txtLon = findViewById(R.id.tv_lon);
        txtAcc = findViewById(R.id.tv_accuracy);
        txtAlt = findViewById(R.id.tv_altitude);
        txtSpd = findViewById(R.id.tv_speed);
        txtUp = findViewById(R.id.tv_updates);
        txtAdd = findViewById(R.id.tv_address);
        txtSens = findViewById(R.id.tv_sensor);
        txtWaypointCounts = findViewById(R.id.txtCountOfCrumbs);
        swtGPS = findViewById(R.id.sw_gps);
        swtLocUp = findViewById(R.id.sw_locationsupdates);
        btnNewWaypoint = findViewById(R.id.btnNewWayPoint);
        btnShowWaypoints = findViewById(R.id.btnShowWaypoints);
        btnShowMap = findViewById(R.id.btnShowMap);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        updateUIValues(location);
                    }
                }
            }
        };

        logout = (Button) findViewById(R.id.logOut);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MapActivity.this, MainActivity.class));
            }
        });

        btnNewWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapInfo mapInfo = (MapInfo)getApplicationContext();
                savedLocations = mapInfo.getMyLocations();
                savedLocations.add(currLocation);
            }
        });

        btnShowWaypoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, LandmarkList.class);
                startActivity(intent);
            }
        });

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        swtGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swtGPS.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    txtSens.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    txtSens.setText("Using Towers + WiFi");
                }
            }
        });

        swtLocUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swtLocUp.isChecked()) {
                    doLocUpdates();
                } else {
                    endLocUpdates();
                }
            }
        });


        updateGPS();
    }

    private void doLocUpdates() {
        txtUp.setText("Location is updating");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        //LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }

    private void endLocUpdates() {
        txtUp.setText("Tracking ended");
        txtLat.setText("Not tracking location");
        txtSpd.setText("Not tracking location");
        txtAcc.setText("Not tracking location");
        txtAlt.setText("Not tracking location");
        txtLon.setText("Not tracking location");
        txtAdd.setText("Not tracking location");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 99:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else{
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void updateGPS(){
        //Toast.makeText(this, "Update GPS", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);


        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(this, "Update GPS", Toast.LENGTH_SHORT).show();
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, null);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                    currLocation = location;
                }
            });
            fusedLocationProviderClient.getLastLocation().addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    displayFail();
                }
            });
            fusedLocationProviderClient.getLastLocation().addOnCanceledListener(this, new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    displayCancel();
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }
    }

    private void displayCancel() {
        Toast.makeText(this, "CANCELLED", Toast.LENGTH_SHORT).show();
    }

    private void displayFail() {
        Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show();
    }

    private void updateUIValues(Location location) {
        txtLat.setText(String.valueOf(location.getLatitude()));
        txtLon.setText(String.valueOf(location.getLongitude()));
        txtAcc.setText(String.valueOf(location.getAccuracy()));
        //Toast.makeText(this, "Update GPS", Toast.LENGTH_SHORT).show();

        if (location.hasAltitude()) {
            txtAlt.setText(String.valueOf(location.getAltitude()));
        }
        else {
            txtAlt.setText("Alt not available");
        }
        if (location.hasSpeed()) {
            txtSpd.setText(String.valueOf(location.getSpeed()));
        }
        else {
            txtSpd.setText("Speed not available");
        }

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            txtAdd.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e){
            txtAdd.setText("Address not found");
        }

        MapInfo mapInfo = (MapInfo)getApplicationContext();
        savedLocations = mapInfo.getMyLocations();

        txtWaypointCounts.setText(Integer.toString(savedLocations.size()));
    }


}