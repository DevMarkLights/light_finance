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

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView textPrice,textView,textView2;
    Button price;
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

            /*
            String am = String.valueOf(amount_of_dividend);
            TextView textView = findViewById(R.id.textView);
            textView.setText(am);
            */
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

    public void get_dividends (String v) {
        try {
            HttpResponse<String> response = Unirest.get("https://yahoofinance-stocks1.p.rapidapi.com/dividends?Symbol="+v+"&OrderBy=Descending")
                    .header("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                    .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .asString();

            String all = response.getBody();
            String allres = new JSONObject(all).getString("results");
            date_of_dividend = allres.substring(10,20);
           // amount_of_dividend = allres.substring(31,35);


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