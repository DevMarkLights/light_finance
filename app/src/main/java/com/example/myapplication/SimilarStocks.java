package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimilarStocks extends AppCompatActivity implements RecylerViewInterface2 {

    RecyclerView similarStocksRecView;
    ArrayList<String> symbols = new ArrayList<String>();
    ArrayList<String> price = new ArrayList<String>();
    ArrayList<String> price_change_percent_ytd = new ArrayList<String>();
    ArrayList<String> dividend_yield_percent = new ArrayList<String>();
    ArrayList<String> dividend_rate_Annual = new ArrayList<String>();
    SimilarStocksRecViewAdpFromDivYield SimilarStocksRecViewAdpFromDivYield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_stocks);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setTitle("Similar Stocks");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int color = Color.parseColor("#808080");
        window.setStatusBarColor(color);
        ActionBar bar;
        bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#808080"));
        bar.setBackgroundDrawable(cd);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    similarStocksArrays();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });t1.start();

        symbols = new ArrayList<String>();
        price = new ArrayList<String>();
        price_change_percent_ytd = new ArrayList<String>();
        dividend_rate_Annual = new ArrayList<String>();
        dividend_yield_percent = new ArrayList<String>();



        similarStocksRecView = findViewById(R.id.similarStocksRecView);

    }



    public void similarStocksArrays() throws IOException, JSONException { // hotstocks sql

        float DY = (float) portfolio_V2.avgDY;
        float low = DY - 1;
        float high = DY + 2;
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "SELECT * FROM stocks WHERE NOT price_change_percent_5d IS NULL AND dividend_yield_percent >= "+low+" AND dividend_yield_percent <= "+high+" ORDER BY dividend_yield_percent  DESC LIMIT 10");
        Request request = new Request.Builder()
                .url("https://hotstoks-sql-finance.p.rapidapi.com/query")
                .post(body)
                .addHeader("content-type", "text/plain")
                .addHeader("x-rapidapi-host", "hotstoks-sql-finance.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = response.body().string();
        String allres = new JSONObject(all).getString("results");
        JSONArray t = new JSONArray(allres);
        for (int i = 0; i <t.length();i++){
            JSONObject l = new JSONObject(String.valueOf(t.get(i)));
            String symbol = new JSONObject(String.valueOf(l)).getString("symbol");
            String stockPrice = new JSONObject(String.valueOf(l)).getString("price");
            String PCPY = new JSONObject(String.valueOf(l)).getString("price_change_percent_5d");
            String DivYieldPercent = new JSONObject(String.valueOf(l)).getString("dividend_yield_percent");
            String DivRateAnnual = new JSONObject(String.valueOf(l)).getString("dividend_rate");

            symbols.add(symbol);
            price.add(stockPrice);
            price_change_percent_ytd.add(PCPY);
            dividend_yield_percent.add(DivYieldPercent);
            dividend_rate_Annual.add(DivRateAnnual);
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        // Send a task to the MessageQueue of the main thread
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                // Code will be executed on the main thread
                SimilarStocksRecViewAdpFromDivYield = new SimilarStocksRecViewAdpFromDivYield(SimilarStocks.this,symbols,
                        price,price_change_percent_ytd,dividend_yield_percent,dividend_rate_Annual, (RecylerViewInterface2) SimilarStocks.this);

                similarStocksRecView.setAdapter(SimilarStocksRecViewAdpFromDivYield);
                similarStocksRecView.setLayoutManager(new LinearLayoutManager(SimilarStocks.this));
            }
        });

    }
    @Override
    public void onItemClick2(int position) {
        Intent intent = new Intent(SimilarStocks.this,LineChart_V2.class);
        String s = symbols.get(position);
        intent.putExtra("Symbol",s);
        startActivity(intent);
    }
}