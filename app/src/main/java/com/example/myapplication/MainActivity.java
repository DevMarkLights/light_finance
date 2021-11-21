package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView textPrice,textView,textView2;
    Button price,upcdivv;
    static String date_of_dividend;
    static double amount_of_dividend;
    static String stock_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("LightFinance");
        //Stops the app from crashing on button click and takes the application out of safe mode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //initializing views in the create method stops app from crashing also
        Button b1 = (Button) findViewById(R.id.button);
        Button price = (Button) findViewById(R.id.price);
        TextView textview = findViewById(R.id.textView);
        TextView textview2 = findViewById(R.id.textView2);
        Button mydbpage = (Button) findViewById(R.id.dbpage);
        Button upcdivv = (Button) findViewById(R.id.UpcDiv);
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
                Intent intent = new Intent(MainActivity.this, Portfolio.class);
                startActivity(intent);
            }
        });

        upcdivv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TotalDividends.class);
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

        */
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test("SLVO");

            }
        });

    }

    public void test (String v) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://twelve-data1.p.rapidapi.com/price?symbol="+v+"&format=json&outputsize=30").get()
                .addHeader("x-rapidapi-host","twelve-data1.p.rapidapi.com")
                .addHeader("x-rapidapi-key","1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        try{
            Response response = client.newCall(request).execute();
            String allres = response.body().string();
            String ally = new JSONObject(allres).getString("price");


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void pr (String v) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://twelve-data1.p.rapidapi.com/price?symbol="+v+"&format=json&outputsize=30").get()
                .addHeader("x-rapidapi-host","twelve-data1.p.rapidapi.com")
                .addHeader("x-rapidapi-key","1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        try{
            Response response = client.newCall(request).execute();
            String allres = response.body().string();
            String ally = new JSONObject(allres).getString("Current Price");
            String ax = new JSONObject(ally).getString("Price");
            //stock_price = Double.parseDouble(ax);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }



}