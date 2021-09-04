package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.os.StrictMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Stops the app from crashing on button click and takes the application out of safe mode
        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //initializing views in the create method stops app from crashing also
        Button b1 = (Button) findViewById(R.id.button);
        EditText res;


    }

    // app keeps crashing here and cannot execute the method
    public void get_Dividends (View view) {

        Button myButton;
        myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(v -> {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://yahoofinance-stocks1.p.rapidapi.com/dividends?Symbol=MSFT&OrderBy=Ascending")
                    .get()
                    .addHeader("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String res = response.toString();
            EditText viewById;
            viewById = findViewById(R.id.editTextTextMultiLine2);
            viewById.setText(res);
        });




    }





}