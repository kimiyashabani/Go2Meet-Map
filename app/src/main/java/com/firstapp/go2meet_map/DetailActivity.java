package com.firstapp.go2meet_map;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import org.w3c.dom.Text;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    Button cheackMap;
    Button checkChat;
    String eventLink;
    Intent inputIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        inputIntent=getIntent();

        TextView visitWebsite = findViewById(R.id.linkTextView);
        TextView startTime = findViewById(R.id.startTimeTextView);
        TextView startDate = findViewById(R.id.startDateTextView);
        TextView locationDetails = findViewById(R.id.locationDetailsTextView);
        TextView locationName = findViewById(R.id.locationNameTextView);

        checkChat = findViewById(R.id.subscribeButton);
        cheackMap = findViewById(R.id.backToMapButton);

        eventLink = inputIntent.getStringExtra("link_key");
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

        visitWebsite.setMovementMethod(LinkMovementMethod.getInstance());
        visitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)eventLink));
                startActivity(intent);
            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.CANADA);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(DetailActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(DetailActivity.this, "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameToRead = locationName.getText().toString();
                textToSpeech.speak(nameToRead, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });


        cheackMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}