package com.example.opsc7312_poe;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class LandmarkList extends AppCompatActivity {

    ListView lstSavedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_list);

        lstSavedLocations = findViewById(R.id.lstLAndmarks);

        MapInfo mapInfo = (MapInfo)getApplicationContext();
        List<Location> savedLocations = mapInfo.getMyLocations();

        lstSavedLocations.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, savedLocations));
    }
}