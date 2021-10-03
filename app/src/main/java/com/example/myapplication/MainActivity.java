package com.example.myapplication;

import android.content.Intent;
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
    TextView textPrice;
    Button price;
    static String date_of_dividend, amount_of_dividend;
    static String stock_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Stops the app from crashing on button click and takes the application out of safe mode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //initializing views in the create method stops app from crashing also
        Button b1 = (Button) findViewById(R.id.button);
        Button price = (Button) findViewById(R.id.price);

        Button mydbpage = (Button) findViewById(R.id.dbpage);
        mydbpage.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               // go to the database class when the button is pressed
               Intent intent = new Intent(MainActivity.this,Database.class);
               startActivity(intent);
           }

        });

        Button curious = (Button) findViewById(R.id.button4);
        curious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, curious.class);
                startActivity(intent);
            }
        });
        /*
        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    pr("SLVO");

            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_dividends("SLVO");

            }
        });
        */
    }


    public void pr (String v) {

            try {
                HttpResponse<String> response = Unirest.get("https://twelve-data1.p.rapidapi.com/price?symbol=slvo&format=json&outputsize=30")
                        .header("x-rapidapi-host", "twelve-data1.p.rapidapi.com")
                        .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                        .asString();

                String all = response.getBody();
                String allres = new JSONObject(all).getString("price");
                stock_price = allres;
                //TextView v1 = findViewById(R.id.textPrice);
                //v1.setText(allres);

        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }

    }

    public void get_dividends (String v) {
        try {
            HttpResponse<String> response = Unirest.get("https://yahoofinance-stocks1.p.rapidapi.com/dividends?Symbol="+v+"&OrderBy=Descending")
                    .header("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                    .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .asString();

            String all = response.getBody();
            String allres = new JSONObject(all).getString("results");
            date_of_dividend = allres.substring(10,20);
            amount_of_dividend = allres.substring(31,35);
            // grab the textview to use it
           // TextView v1 = findViewById(R.id.textView);
            //TextView v2 = findViewById(R.id.textView2);
            // change textview text with api data
            //v1.setText(date_of_dividend);
            //v2.setText(amount_of_dividend);

        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }
        try {
            HttpResponse<String> response = Unirest.get("https://twelve-data1.p.rapidapi.com/price?symbol=+"+v+"&format=json&outputsize=30")
                    .header("x-rapidapi-host", "twelve-data1.p.rapidapi.com")
                    .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .asString();

            String all = response.getBody();
            String allres = new JSONObject(all).getString("price");
            stock_price = allres;
            TextView v1 = findViewById(R.id.textPrice);
            v1.setText(allres);

        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }
    }

}