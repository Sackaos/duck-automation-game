package com.example.duck_automation_game.ui;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.duck_automation_game.R;
import com.example.duck_automation_game.engine.CustomMarker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String CUSTOM_MARKERLIST_PREFKEY = "markerlistKey";
    SharedPreferences sharedPref;
    FusedLocationProviderClient locationProvider;
    GoogleMap gMap;
    LatLng currentLatLng;
    MapView mvMap;
    ArrayList<Marker> markerList;
    Boolean canClick;
    LinearLayout lyLower;
    BitmapDescriptor bitmapDescriptor;
    String factoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE);

        mvMap = findViewById(R.id.mvMaps);
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        // init google map
        mvMap.onCreate(null);
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("factory_icon2", 200, 200));
        mvMap.getMapAsync(this);
        canClick = getIntent().getBooleanExtra("canClick", false);
        factoryName = getIntent().getStringExtra("factoryName");
        lyLower = findViewById(R.id.ly_mapsLower);
        lyGone();
        setOnClicks();
    }

    private void setOnClicks() {
        Button accept = findViewById(R.id.btn_factoryAccept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyGone();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("did_buy", true);
                LatLng factoryPosition = markerList.get(markerList.size() - 1).getPosition();
                returnIntent.putExtra("factory_position", factoryPosition);

                returnIntent.putExtra("factoryName", factoryName);

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Button deny = findViewById(R.id.btn_factoryDeny);
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canClick = true;
                lyGone();
                markerList.get(markerList.size() - 1).remove();
                markerList.remove(markerList.size() - 1);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("did_buy", false);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();


            }
        });
    }

    private void getLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationProvider.getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // do something with the location
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            float zoomLevel = 18f;
                            //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
                            zoomLevel = 16f;
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));

                            //gMap.addMarker(new MarkerOptions().position(currentLatLng).title("current location"));
                            setMapClick(gMap);
                        }
                    });

        } else {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("GAD", "onMapReady:IM IN !@!@! ");
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        getLocation();

        //also shows said markers on map
        markerList = getMarkerListFromPref();

    }

    public ArrayList<Marker> getMarkerListFromPref() {
        String customMarkerListString = getStringFromPref(CUSTOM_MARKERLIST_PREFKEY, "");
        if (!customMarkerListString.equals("")) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<CustomMarker>>() {
            }.getType();
            ArrayList<CustomMarker> customMarkerArrayList = gson.fromJson(customMarkerListString, type);
            return makeCustomMarkerArrayToGMarkerArray(customMarkerArrayList);

        } else return new ArrayList<Marker>();
    }

    private void saveMarkerListToSharedPref() {
        Gson gson = new Gson();
        ArrayList<CustomMarker> CustomMarkerList = makeGmarkerArrayToCustomMarkerArray(markerList);
        String markerListString = gson.toJson(CustomMarkerList);
        //save in shared prefs
        sharedPref.edit().putString(CUSTOM_MARKERLIST_PREFKEY, markerListString).apply();
    }

    private ArrayList<Marker> makeCustomMarkerArrayToGMarkerArray(ArrayList<CustomMarker> customMarkerArrayList) {
        customMarkerArrayList.add(new CustomMarker(new LatLng(0.0d, 0.0d), "DABMAN", "a"));
        ArrayList<Marker> MarkerList = new ArrayList<Marker>();

        for (CustomMarker cm : customMarkerArrayList) {
            MarkerOptions mo = makeMarkerOptionsFromCustomMarker(cm);
            mo.icon(bitmapDescriptor);
            MarkerList.add(gMap.addMarker(mo));
        }
        return MarkerList;
    }

    private ArrayList<CustomMarker> makeGmarkerArrayToCustomMarkerArray(ArrayList<Marker> MarkerArrayList) {
        ArrayList<CustomMarker> CustomMarkerList = new ArrayList<CustomMarker>();
        for (Marker m : MarkerArrayList) {
            CustomMarker cm = new CustomMarker(m.getPosition(), m.getTitle(), m.getSnippet());
            CustomMarkerList.add(cm);
        }
        return CustomMarkerList;
    }

    public String getStringFromPref(String prefKey, String defaultValue) {
        return sharedPref.getString(prefKey, defaultValue);
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void setMapClick(final GoogleMap map) {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (canClick) {
                    Boolean isOK = true;
                    for (Marker marker : markerList) {
                        if (distanceTo(marker.getPosition(), latLng) < 100) isOK = false;
                    }
                    if (isOK) {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title(factoryName)
                                .snippet(latLng + "")
                                .icon(bitmapDescriptor);
                        Marker a = map.addMarker(markerOptions);
                        a.setTag(bitmapDescriptor);
                        markerList.add(a);
                        lyShow();
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(a.getPosition(), 16f));

//                Animation tweenAnimation = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.tween);
//                lyLower.startAnimation(tweenAnimation);
//                lyLower.setVisibility(View.GONE);
                        canClick = false;
                    } else showToast("distance too smol");
                }
                else{
                    Marker closestMarker=markerList.get(0);
                    for (Marker marker:markerList) {
                    if (distanceTo(latLng,marker.getPosition())<distanceTo(latLng,closestMarker.getPosition())){}
                    closestMarker=marker;
                    }
                    float zoomLevel = 16f;
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(closestMarker.getPosition(), zoomLevel));
                }
            }
        });
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void lyShow() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.2f
        );
        lyLower.setLayoutParams(param);
    }

    private void lyGone() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0f
        );
        lyLower.setLayoutParams(param);
    }

    public Marker findMarkerByLatLng(LatLng latLng) {
        for (Marker marker : markerList) {
            if (marker.getPosition().equals(latLng)) {
                return marker;
            }
        }
        return null;
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private MarkerOptions makeMarkerOptionsFromCustomMarker(CustomMarker m) {
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("factory_icon2", 200, 200));

        return new MarkerOptions()
                .position(m.getPosition())
                .title(m.getTitle())
                .snippet(m.getSnippet())
                .icon(bitmapDescriptor);
    }

    private MarkerOptions makeMarkerOptionsFromMarker(Marker m) {
        return new MarkerOptions()
                .position(m.getPosition())
                .title(m.getTitle())
                .snippet(m.getSnippet())
                .icon(((BitmapDescriptor) m.getTag()));
    }


    // ------ life cycle methods (for the mapView) ------ //


    @Override
    protected void onStart() {
        super.onStart();
        mvMap.onStart();

    }

    @Override
    protected void onStop() {
        //saveMarkerListToSharedPref();
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
        saveMarkerListToSharedPref();
        mvMap.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            Boolean a = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
            Boolean b = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
            if (a && b) {
                getLocation();
            } else {
                showToast("I dont have enough permissions 😢");
            }
        }
    });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int distanceTo(LatLng start, LatLng end) {

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(start.latitude);
        startPoint.setLongitude(start.longitude);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(end.latitude);
        endPoint.setLongitude(end.longitude);

        int distance = (int) startPoint.distanceTo(endPoint);
        return distance;
    }
}
