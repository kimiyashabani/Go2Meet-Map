package com.firstapp.go2meet_map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListActivity extends AppCompatActivity {
    private boolean datasetReady=false;
    private RecyclerView recyclerView;
    MyAdapter recyclerViewAdapter;
    Button mapButton;
    Dataset dataset=new Dataset();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new MyAdapter(dataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mapButton = findViewById(R.id.mapBtn);
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                super.handleMessage(msg);
                if((msg.getData().getBoolean("full"))) {
                    datasetReady=true;
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
        /*while (!datasetReady){}
        recyclerViewAdapter.notifyDataSetChanged();*/
    }
}
