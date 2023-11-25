package com.firstapp.go2meet_map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        ImageView eventImage= findViewById(R.id.madridImage);

        checkChat = findViewById(R.id.subscribeButton);
        cheackMap = findViewById(R.id.backToMapButton);

        eventLink = inputIntent.getStringExtra("link_key");
        PrivateThread t=new PrivateThread(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load image or failure
                Bitmap bitmap;
                super.handleMessage(msg);
                if((bitmap = msg.getData().getParcelable("image")) != null) {
                    eventImage.setImageBitmap(bitmap);
                }else Toast.makeText(DetailActivity.this, "Event image not found", Toast.LENGTH_SHORT).show();
            }
        });
        t.start();
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

        checkChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(DetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("topic", intent.getStringExtra("eventName"));
                startActivity(chatIntent);
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
    private class PrivateThread extends Thread{
        Handler handler;

        PrivateThread(Handler handler){
            this.handler=handler;
        }
        @Override
        public void run(){
            URL url;
            Message msg = handler.obtainMessage();
            Bundle msg_data = msg.getData();
            try {
                eventLink=eventLink.substring(0,4)+"s"+eventLink.substring(4,eventLink.length());
                url = new URL(eventLink);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is=urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(reader);
                // We read the text contents line by line and add them to the response:
                String line = in.readLine();
                String text="";
                while (line != null) {
                    text += line + "\n";
                    line = in.readLine();
                }
                String capture="";
                Pattern pattern = Pattern.compile("<img alt=\"(.*)\" src=\"(.*)/>");
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    // do something with matcher.group(1));
                    if(matcher.group(1).equals("section-image"))continue;
                    capture=matcher.group(2);
                    //Make it so the string ends with the extension of the file
                    while(capture.charAt(capture.length()-1)!='g')
                        capture=capture.substring(0,capture.length()-1);
                    if(!capture.equals("/assets/images/logo-madrid.png"))break;
                }
                if(capture.equals("/assets/images/logo-madrid.png")){
                    msg_data.putParcelable("image",null);
                    msg.sendToTarget();
                    return;
                }
                capture="https://www.madrid.es"+capture.substring(0,capture.length());
                if(!capture.endsWith(".jpg") && !capture.endsWith(".png") && !capture.endsWith(".jpeg")){
                    msg_data.putParcelable("image",null);
                    msg.sendToTarget();
                    return;
                }
                url = new URL(capture);
                urlConnection = (HttpURLConnection) url.openConnection();
                is = urlConnection.getInputStream();
                msg_data.putParcelable("image",BitmapFactory.decodeStream(is));
                msg.sendToTarget();
                urlConnection.disconnect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}