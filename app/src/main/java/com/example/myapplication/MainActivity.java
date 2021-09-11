package com.example.myapplication;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Stops the app from crashing on button click and takes the application out of safe mode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //initializing views in the create method stops app from crashing also
        Button b1 = (Button) findViewById(R.id.button);
    }

    // app keeps crashing here and cannot execute the method
    public void get_Dividends (View view)  {
        Button myButton;
        myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(this::onClick);
    }

    public void onClick(View v) {
        try {
            HttpResponse<String> response = Unirest.get("https://yahoofinance-stocks1.p.rapidapi.com/dividends?Symbol=USOI&OrderBy=Descending")
                    .header("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                    .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .asString();

            String all = response.getBody();
            String allres = new JSONObject(all).getString("results");
            String date_of_dividend = allres.substring(10,20);
            String amount_of_dividend = allres.substring(31,35);
            // grab the textview to use it
            TextView v1 = findViewById(R.id.textView);
            TextView v2 = findViewById(R.id.textView2);
            // change textview text with api data
            v1.setText(date_of_dividend);
            v2.setText(amount_of_dividend);

        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }
    }



}