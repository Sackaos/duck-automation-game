package com.example.duck_automation_game.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.duck_automation_game.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    MapView mvMap;
    GoogleMap gmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mvMap=findViewById(R.id.mvMaps);
        // init google map
        mvMap.onCreate(null);
        mvMap.getMapAsync(this);
    }
    // ------ life cycle methods (for the mapView) ------ //

    @Override
    protected void onStart() {
        super.onStart();
        mvMap.onStart();

    }

    @Override
    protected void onStop() {
        mvMap.onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mvMap.onLowMemory();
    }


    @Override
    protected void onPause() {
        mvMap.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setOnMapClickListener(latLng -> {//func func func
             });

    }
}