package com.firstapp.go2meet_map;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.firstapp.go2meet_map.databinding.ActivityMapsBinding;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.LocationRequest;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, SensorEventListener {
    /** DEFINING MAP **/
    private GoogleMap mMap;
    private UiSettings uiSettings;
    private ActivityMapsBinding binding;
    private RadioGroup radioGroup;
    private RadioButton normalMap;
    private RadioButton hybridMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient ;
    private LocationCallback locationCallback;
    private static final String TAG = MapsActivity.class.getSimpleName();
    double longitude;
    double latitude;

    /** DEFINING LIGHT SENSOR **/
    private SensorManager sensorManager;
    private Sensor lightSensor;
    boolean lightSensorActivated;

    //Button btnList;//= findViewById(R.id.homeBtn);
    //widgets
    private EditText searchText;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //searchText = (EditText) findViewById(R.id.inputSearch);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            Toast.makeText(this, "Light sensor not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        radioGroup = findViewById(R.id.radioGroup);
        normalMap = findViewById(R.id.normal_map);
        hybridMap = findViewById(R.id.hybrid_map);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group,int checkedId){
                if (checkedId == normalMap.getId()) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                } else if (checkedId == hybridMap.getId()) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(checkLocationPermission()){
            ImageButton currentLocationButton = findViewById(R.id.currentLocationBtn);
            currentLocationButton.setOnClickListener(view -> {
                requestCurrentLocation();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latitude, longitude), 15f)
                );
            });
        }


        //sensorManager.registerListener(MapsActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        /*btnList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });*/
    }

    private boolean checkLocationPermission(){
        //Here we check if we have the location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            //if else we should request for location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
    }
    //We did not have permission so we requested for it and here we should handle the result of permission REQUEST:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission was granted
                if (checkLocationPermission()){
                    mMap.setMyLocationEnabled(true);
                    requestCurrentLocation();
                }
            }else {
                //permission was denied
                showToast("The current location cannot be get because there is no permisson to do so");
            }
        }

    }

    /*private void init(){
        Log.d(TAG,"init: initializing");
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction() == event.ACTION_DOWN
                || event.getAction() == event.KEYCODE_ENTER){
                    //now we execute our method for searching that will find and show the places
                    //geoLocate();

                }
                return false;
            }
        });
    }*/
    /**   MAKING THE LIST OF SEARCH BAR **/
    /*private void geoLocate(){
        Log.d(TAG,"Geolocate : geolocating");
        String searchedString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchedString , 1);
        } catch (IOException e){
            Log.e(TAG, "geolocate : IOException" + e.getMessage());
        }
        if (list.size() > 0){
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);
        }
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        uiSettings =mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if(checkLocationPermission()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle_retro));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    public void requestCurrentLocation(){
        try{
            CurrentLocationRequest locationRequest = new CurrentLocationRequest.Builder()
                    .setPriority(LocationRequest.QUALITY_HIGH_ACCURACY)
                    .setDurationMillis(60000)
                    .build();
            //this request location updates
            fusedLocationProviderClient.getCurrentLocation(locationRequest, null)
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                            }else{

                            }
                        }
                    });

        } catch (SecurityException e){
            e.printStackTrace();
            }
    }
    private void setUpMap(GoogleMap googleMap){

        // parsed lat and lang ???

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        /*float lightLevel = event.values[0];

        // Adjust this threshold based on your application's requirements
        if (lightLevel < 1000) {
            try {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.mapstyle_night));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }

         */

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (accuracy < 100) {
            try {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.mapstyle_night));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}