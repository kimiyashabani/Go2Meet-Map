package com.firstapp.go2meet_map;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    Button checkMap;
    Button checkChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView visitWebsite = findViewById(R.id.linkTextView);
        TextView startTime = findViewById(R.id.startTimeTextView);
        TextView startDate = findViewById(R.id.startDateTextView);
        TextView locationDetails = findViewById(R.id.locationDetailsTextView);
        TextView locationName = findViewById(R.id.locationNameTextView);
        checkChat = findViewById(R.id.subscribeButton);
        checkMap = findViewById(R.id.backToMapButton);


        //Intents to populate fields
        Intent intent = getIntent();
        if (intent == null) {
            //TODO Edge case handling -> discuss with TEAM
        }
        assert intent != null;
        StringBuilder sb = new StringBuilder();
        if (intent.getStringExtra("startDate").equals(intent.getStringExtra("endDate"))) {
            sb.append(intent.getStringExtra("startDate"));
        } else {
            sb.append("from: ").append(intent.getStringExtra("startDate")).append("\nto: ").append(intent.getStringExtra("endDate"));
        }
        startDate.setText(sb.toString());
        //TODO missing case: when event time is not specified
        startTime.setText(intent.getStringExtra("eventTime"));
        locationDetails.setText(intent.getStringExtra("eventLocation"));
        locationName.setText(intent.getStringExtra("eventName"));


        visitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = intent.getStringExtra("link_key");
                if (url != null) {
                    Uri webpage = Uri.parse(url);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(webIntent);
                } else {
                    Log.e("URL", "URL is null");
                }
            }
        });

        checkMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(DetailActivity.this, MapsActivity.class);
                startActivity(mapIntent);
            }
        });

        checkChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(DetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("topic", intent.getStringExtra("eventName"));
                startActivity(chatIntent);
            }
        });
    }
}