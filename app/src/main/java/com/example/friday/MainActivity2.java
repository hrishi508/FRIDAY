package com.example.friday;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {

    TextToSpeech textToSpeech;
    // creating a client
    OkHttpClient okHttpClient = new OkHttpClient();

    // building a request
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        String query;
        Handler handler = new Handler();

        // create an object textToSpeech and adding features into it
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        // create the get Intent object
        Intent intent = getIntent();
        // receive the value by getStringExtra() method and
        // key must be same which is send by first activity
        String key = intent.getStringExtra("message_key");
        request = new Request.Builder().url("http://192.168.0.212:5000/?user="+key).build();

//        GETRequest();

        // Define the code block to be executed
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GETRequest();
                handler.postDelayed(this, 1000);
            }
        };

        // Start the Runnable immediately
        handler.post(runnable);
    }

    protected void GETRequest() {

        // making call asynchronously
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            // called if server is unreachable
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity2.this, "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            // called if we get a
            // response from the server
            public void onResponse(
                    @NotNull Call call,
                    @NotNull Response response)
                    throws IOException {
                String res = response.body().string();
                if(!res.equals("0")){
                    textToSpeech.speak(res,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }
}