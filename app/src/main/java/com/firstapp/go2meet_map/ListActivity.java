package com.firstapp.go2meet_map;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class ListActivity extends AppCompatActivity implements SensorEventListener {
    private RecyclerView recyclerView;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    MyAdapter recyclerViewAdapter;
    Button mapButton;
    Dataset dataset=new Dataset();
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        setTitle("List view of the events");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new MyAdapter(dataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.CANADA);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(ListActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ListActivity.this, "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Item clickedItem=dataset.getItemAtPosition(position);
                        Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                        String eventName =  clickedItem.getEventName();
                        String eventLocation =  clickedItem.getPlace();
                        String startDate = reverseDate( clickedItem.getStartDate());
                        String endDate = reverseDate( clickedItem.getEndDate());
                        String eventTime =  clickedItem.getTime();
                        String url =  clickedItem.getUrl();
                        intent.putExtra("eventName", eventName);
                        intent.putExtra("startDate", startDate);
                        intent.putExtra("endDate", endDate);
                        intent.putExtra("eventLocation", eventLocation);
                        intent.putExtra("eventTime", eventTime);
                        intent.putExtra("link_key", url);
                        startActivity(intent);
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                        //Test to speech
                        Item clickedItem=dataset.getItemAtPosition(position);
                        speak(clickedItem.getEventName()+", in, "+clickedItem.getPlace());

                    }
                })
        );
        mapButton = findViewById(R.id.mapBtn);
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                super.handleMessage(msg);
                if((msg.getData().getBoolean("Full"))) {
                    Log.d("DATASET-LIST", "Dataset has "+dataset.size()+" elements");
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        };
        LoadingThread t= new LoadingThread(dataset, handler, new DBHelper(this));
        t.start();
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(ListActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    private String reverseDate(String input){
        String[] helper = input.split("-");
        StringBuilder sb = new StringBuilder();
        for (int i = helper.length - 1; i >= 0; i--) {
            sb.append(helper[i]);
            if (i != 0){
                sb.append("/");
            }
        }
        return sb.toString();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_LIGHT){
            float lightLevel = event.values[0];
            if (lightLevel < 150) {
                mapButton.setBackgroundResource(R.drawable.btn_dark_mode);
            } else if (lightLevel > 250) {
                mapButton.setBackgroundResource(R.drawable.btn_round_corner);
            }
        }
    }
    private void speak(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
