package com.firstapp.go2meet_map;
import android.content.res.Resources;

import androidx.annotation.RequiresApi;

import androidx.fragment.app.FragmentActivity;


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

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.LocationRequest;
import android.content.Context;

import java.util.List;

import android.content.Intent;
import android.view.View;




public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, SensorEventListener {
    /** DEFINING MAP **/
    Dataset dataset = new Dataset();
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
    private GoogleMap googleMap;

    DBHelper db = new DBHelper(this);

    /** DEFINING LIGHT SENSOR **/
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private List<Item> itemsList;
    Button listButton;

    Marker marker;
    boolean flag = false;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //searchText = (EditText) findViewById(R.id.inputSearch);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "binding done");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        listButton = findViewById(R.id.listBtn);

        radioGroup = findViewById(R.id.radioGroup);
        hybridMap = findViewById(R.id.hybrid_map);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group,int checkedId){

                if (checkedId == hybridMap.getId()) {
                    AccMap(mMap);
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
            LoadingThread t= new LoadingThread(dataset, db);
            t.start();
            /*while (dataset.size() < 50){//TODO substitute this for an observer of the size value of the dataset
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }*/
            Log.d("Maps activity", "Finished parsing the data");
            //dataset.fillDB(new DBHelper(this));
        }

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ListActivity.class);
                startActivity(intent);
            }

        });
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        uiSettings =mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(checkLocationPermission()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        //Adding pins here:
        for(Item i: itemsList ){
            LatLng location = new LatLng(i.getLatitude(), i.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(location));
            marker.setTag(i); // I identified here which marker was clicked
        }
        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                int position = (int) marker.getTag();
                Intent intent = new Intent(MapsActivity.this , DetailActivity.class);
                intent.putExtra("marker position" , position);
                startActivity(intent);
            }
        });*/



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
                            }
                        }
                    });

        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float lightLevel = event.values[0];
            Log.d("TAG", Integer.toString((int) lightLevel));
            if (lightLevel < 150) {
                hybridMap.setBackgroundResource(R.drawable.btn_dark_mode);
                listButton.setBackgroundResource(R.drawable.btn_dark_mode);
                changeToNightTheme(mMap);
            } else {
                hybridMap.setBackgroundResource(R.drawable.btn_round_corner);
                listButton.setBackgroundResource(R.drawable.btn_round_corner);
                if (mMap != null) {
                    applyDefaultMapStyle();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private void applyDefaultMapStyle() {
        boolean success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_normal));

        if (!success) {
            Log.e("TAG", "Default map style parsing failed.");
        }
    }
    private void showToast(String message) {Toast.makeText(this, message, Toast.LENGTH_SHORT).show();}
    @Override
    public void onSensorChanged(SensorEvent event) {}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void changeToNightTheme(GoogleMap googleMap){
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle_night));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }
    public void AccMap(GoogleMap googleMap){
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle_accessible));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

    }


}