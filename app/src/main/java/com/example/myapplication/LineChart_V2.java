package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphTextView;

public class LineChart_V2 extends AppCompatActivity implements RecyclerViewInterface  {
    RecyclerView recyclerView;
    RecViewAdpSimStocks RecViewAdpSimStocks;
    DBHelper DB;
    ApiCalls api = new ApiCalls();
    LineChart lineChart;
    // For card view
    TextView openValue,lowValue,highValue,closeValue,fifty_two_week_high_TV,fifty_two_week_low_TV,
            divAmountView,exDivDate,highestView,lowestView;
    double highVal;
    NeumorphTextView symbolView,priceView,highestvalue,lowestValue;
    NeumorphButton threeMoTV,sixMoTV,oneYear,fiveDay;
    ImageView imp_View_in_Portfolio;
    int days = 5;
    double lowest,highest;
    String fifty_two_week_loww,fifty_two_week_highh,high,low,open,close, exDate;
    double divAmount;
    ArrayList<String> historicalPrice5 = new ArrayList<>();

    List<Entry> lineEntries = new ArrayList<Entry>();
    boolean stockInPortfolio = false;
    ArrayList<String> symbol, price, DivYeild, dividend_amount,percent_Change;
    String sym = "";
    int linechartcount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart_v2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setTitle("stock price history");
        DB = new DBHelper(this);
        sym = String.valueOf(getIntent().getSerializableExtra("Symbol"));

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiCalls.getOnlyStockPrice(sym);

                // Create a handler that associated with Looper of the main thread
                Handler mainHandler = new Handler(Looper.getMainLooper());

// Send a task to the MessageQueue of the main thread
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Code will be executed on the main thread
                        priceView.setText("$"+String.valueOf(ApiCalls.stock_price));
                    }
                });
            }
        }); thread1.start();

        getUiElements();
        // similar stocks thread
        Thread simStocksThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    api.similarStocks(sym);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }); simStocksThread.start();

        // historical prices
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    historicalPrices(sym,days);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread newCall = new Thread(new Runnable() {
            @Override
            public void run() {
                inPortfolio(sym);
            }
        }); newCall.start();
        //52 week thread
        try {
            fiftyTwoWeek(sym);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // store data in arrays
        try {
            store_data_in_arrays();
        } catch (JSONException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setOnClickListeners() {
        threeMoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linechartcount++;
                days = 90;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            historicalPrices(sym,days);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                negOrPos();
                lineChart();
                lineChart.getDescription().setText("Past 3 months day stock price");
                highestValueForLineChart();
                lowestValueForLineChart();
                highestView.setText("3 month highest");
                lowestView.setText("3 month lowest");
                if(lowest > highest){
                    threeMoTV.setBackgroundColor(Color.rgb(255, 0, 0));
                }else {
                    threeMoTV.setBackgroundColor(Color.rgb(0, 150, 255));
                }
                sixMoTV.setBackgroundColor(Color.rgb(255,255,255));
                oneYear.setBackgroundColor(Color.rgb(255,255,255));
                fiveDay.setBackgroundColor(Color.rgb(255,255,255));

            }
        });
        sixMoTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                linechartcount++;
                days = 180;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            historicalPrices(sym,days);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lineChart.getDescription().setText("Past 6 months day stock price");
                negOrPos();
                lineChart();
                highestValueForLineChart();
                lowestValueForLineChart();
                highestView.setText("6 month highest");
                lowestView.setText("6 month lowest");
                threeMoTV.setBackgroundColor(Color.rgb(255, 255, 255));
                if(lowest > highest){
                    sixMoTV.setBackgroundColor(Color.rgb(255, 0, 0));
                }else {
                    sixMoTV.setBackgroundColor(Color.rgb(0, 150, 255));
                }
                oneYear.setBackgroundColor(Color.rgb(255,255,255));
                fiveDay.setBackgroundColor(Color.rgb(255,255,255));
            }
        });
        oneYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linechartcount++;
                days = 365;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            historicalPrices(sym,days);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lineChart.getDescription().setText("Past year day stock price");
                negOrPos();
                lineChart();
                highestValueForLineChart();
                lowestValueForLineChart();
                negOrPos();
                highestView.setText("Year highest");
                lowestView.setText("Year lowest");
                threeMoTV.setBackgroundColor(Color.rgb(255, 255, 255));
                sixMoTV.setBackgroundColor(Color.rgb(255,255,255));
                if(lowest > highest){
                    oneYear.setBackgroundColor(Color.rgb(255, 0, 0));
                }else {
                    oneYear.setBackgroundColor(Color.rgb(0, 150, 255));
                }
                fiveDay.setBackgroundColor(Color.rgb(255,255,255));
            }
        });
        fiveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linechartcount++;
                days = 5;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            historicalPrices(sym,days);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lineChart.getDescription().setText("Past 5 day stock price");
                negOrPos();
                lineChart();
                highestValueForLineChart();
                lowestValueForLineChart();
                negOrPos();
                highestView.setText("7 day highest");
                lowestView.setText("7 day lowest");
                threeMoTV.setBackgroundColor(Color.rgb(255, 255, 255));
                sixMoTV.setBackgroundColor(Color.rgb(255,255,255));
                oneYear.setBackgroundColor(Color.rgb(255,255,255));
                if(lowest > highest){
                    fiveDay.setBackgroundColor(Color.rgb(255, 0, 0));
                }else {
                    fiveDay.setBackgroundColor(Color.rgb(0, 150, 255));
                }
            }
        });
    }

    private void getArraysForRecView() {
        symbol = new ArrayList<>();
        price = new ArrayList<>();
        DivYeild = new ArrayList<>();
        dividend_amount = new ArrayList<>();
        percent_Change = new ArrayList<>();
    }

    public void store_data_in_arrays() throws JSONException, IOException, InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <api.recommendedSymbols.size();i++){
                    String temp = api.recommendedSymbols.get(i);
                    symbol.add(temp);
                   // api.stockQuote(temp);
                    try {
                        api.regularMarketPrice(temp);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    price.add(String.valueOf(ApiCalls.stock_price));
                    try {
                        api.getDivYield(temp);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    if(ApiCalls.Dividend_Yield == (0.0)) {
                        DivYeild.add("no dividend");
                        dividend_amount.add("0.0");
                    } else {
                        dividend_amount.add(String.valueOf(ApiCalls.annualDividendSimStockStocks));
                        DivYeild.add(String.valueOf(ApiCalls.Dividend_Yield));
                    }

                    try {
                        api.getPercentChange(temp);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    percent_Change.add(String.valueOf(ApiCalls.percent_Change));

                }

               setRecyclerView();

            }
        });
        t.start();
        getUiElements();

    }

    public void getUiElements() {
        highestvalue = findViewById(R.id.highestValue);
        lowestValue = findViewById(R.id.lowestValue);
        threeMoTV = findViewById(R.id.threeMoTV);
        sixMoTV = findViewById(R.id.sixMoTV);
        oneYear = findViewById(R.id.oneYear);
        fiveDay = findViewById(R.id.fiveDay);
        openValue = findViewById(R.id.openValue);
        lowValue = findViewById(R.id.lowValue);
        highValue = findViewById(R.id.highValue);
        closeValue = findViewById(R.id.closeValue);
        fifty_two_week_high_TV = findViewById(R.id.fifty_two_week_high_TV);
        fifty_two_week_low_TV = findViewById(R.id.fifty_two_week_low_TV);
        divAmountView = findViewById(R.id.divAmountView);
        exDivDate = findViewById(R.id.exDivDate);
        symbolView = findViewById(R.id.symbolView);
        priceView = findViewById(R.id.priceView);
        highestView = findViewById(R.id.highest);
        lowestView = findViewById(R.id.lowest);

        imp_View_in_Portfolio = findViewById(R.id.imp_View_in_Portfolio);

        recyclerView = findViewById(R.id.similarStocksRecView);

        setOnClickListeners();
        getArraysForRecView();
        symbolView.setText(sym.toUpperCase());
        imp_View_in_Portfolio.setImageDrawable(getResources().getDrawable(R.drawable.blank_square));


    }

    public void setRecyclerView() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        // Send a task to the MessageQueue of the main thread
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                // Code will be executed on the main thread
                recyclerView = findViewById(R.id.similarStocksRecView);
                RecViewAdpSimStocks = new RecViewAdpSimStocks(LineChart_V2.this, symbol, price,
                        DivYeild, dividend_amount,percent_Change,LineChart_V2.this);
                recyclerView.setAdapter(RecViewAdpSimStocks);
                recyclerView.setLayoutManager(new LinearLayoutManager(LineChart_V2.this));
            }
        });


    }

    public void historicalPrices(String s, int days) throws IOException, JSONException {
        String num_days = "";
        if (historicalPrice5 != null){
            historicalPrice5.clear();
        }
        //api presets for price data
        //1d 5d 3mo 6mo 1y 5y max
        if(days == 5) {
            num_days = "5d";
        }else if(days == 90) {
            num_days = "3mo";
        } else if (days == 180) {
            num_days = "6mo";
        }else if (days == 365) {
            num_days = "1y";
        }
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v8/finance/spark?symbols="+s+"&range="+num_days+"&interval=1d")
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = response.body().string();
        String allres = new JSONObject(all).getString(s);
        String allre = new JSONObject(allres).getString("close");
        JSONArray t = new JSONArray(allre);
        for(int i = 0; i < t.length();i++){
            String T = String.valueOf(t.get(i));
            historicalPrice5.add(T);
        }
        getDataSet();
    }

    private List<Entry> getDataSet() {
        lineEntries.clear();
        for(int i = 0; i < historicalPrice5.size(); i++) {
            float data = Float.parseFloat(historicalPrice5.get(i));
            lineEntries.add(new Entry(i, data));

        }
        // Create a handler that associated with Looper of the main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());

        // Send a task to the MessageQueue of the main thread
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                // Code will be executed on the main thread
                lineChart();
                highestValueForLineChart();
                lowestValueForLineChart();
                negOrPos();
            }
        });
        return lineEntries;

    }

    public void highestValueForLineChart() {
        highVal = Double.parseDouble(Collections.max(historicalPrice5));
        BigDecimal a = new BigDecimal(highVal);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        highVal = Double.parseDouble(String.valueOf(b));
        highestvalue.setText(String.format("$%s", String.valueOf(highVal)));

    }

    public void lowestValueForLineChart() {
        double min = 0.0;
        for (int i = 0; i < historicalPrice5.size(); i++) {
            if (i == 0){
                min = Double.parseDouble(historicalPrice5.get(i));
            }
            if (Double.parseDouble(historicalPrice5.get(i)) < min) {
                min = Double.parseDouble(historicalPrice5.get(i));
            }
        }
        BigDecimal a = new BigDecimal(min);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        lowestValue.setText(String.format("$%s", String.valueOf(b)));


    }

    public void negOrPos(){
        if(historicalPrice5.isEmpty()){
            lowest = 0.0;
            highest = 0.0;
        } else {
            double l = Double.parseDouble(historicalPrice5.get(0));
            double h = Double.parseDouble(historicalPrice5.get(historicalPrice5.size() - 1));
            lowest = l;
            highest = h;
        }
    }

    public void reloadActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void lineChart() {
        lineChart = findViewById(R.id.lineChart);
        if(lineEntries.isEmpty()){
            getDataSet();
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(2);
        if(lowest > highest){
            lineDataSet.setColors(Color.rgb(255, 0, 0));
            lineDataSet.setCircleColors(255, 0, 0);
        }else{
            lineDataSet.setColors(Color.rgb(0, 150, 255));
            lineDataSet.setCircleColors(0,150,255);
        }
        lineDataSet.setCircleRadius(1f);
        lineDataSet.setCircleHoleRadius(.5f);
        lineDataSet.setDrawValues(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(historicalPrice5.size()));
        xAxis.setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.getDescription().setText("Past 5 day stock price");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.setData(lineData);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        if (linechartcount == 0){
            if (lowest > highest) {
                fiveDay.setBackgroundColor(Color.rgb(255, 0, 0));
            } else {
                fiveDay.setBackgroundColor(Color.rgb(0, 150, 255));
            }
        }
    }

    public void fiftyTwoWeek(String symb) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                api.stockQuote(symb);
                api.Dividend(sym);
                // Create a handler that associated with Looper of the main thread
                Handler mainHandler = new Handler(Looper.getMainLooper());

                // Send a task to the MessageQueue of the main thread
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Code will be executed on the main thread
                        high = api.high;
                        low = api.low;
                        open = api.open;
                        close = api.close;

                        openValue.setText("$"+open);
                        lowValue.setText("$"+low);
                        highValue.setText("$"+high);
                        closeValue.setText("$"+close);

                        fifty_two_week_loww = api.fifty_two_week_low;
                        fifty_two_week_highh = api.fifty_two_week_high;

                        fifty_two_week_low_TV.setText("$"+fifty_two_week_loww);
                        fifty_two_week_high_TV.setText("$"+fifty_two_week_highh);

                        divAmount = ApiCalls.amount_of_dividend;
                        exDate = ApiCalls.date_of_dividend;
                        String amount = String.valueOf(divAmount);
                        divAmountView.setText("$"+amount);
                        exDivDate.setText(exDate);
                    }
                });
            }
        }); thread.start();

    }


    public void inPortfolio(String sym) {
        stockInPortfolio = false;
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            // Create a handler that associated with Looper of the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());

            // Send a task to the MessageQueue of the main thread
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Code will be executed on the main thread
                    Toast.makeText(LineChart_V2.this, "No Stocks", Toast.LENGTH_SHORT).show();
                }
            });
            //Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor.moveToNext()) {
                String t = cursor.getString(0);
                String symU = sym.toUpperCase();
                String tU = t.toUpperCase();
                if (symU.equals(tU)) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());

                    // Send a task to the MessageQueue of the main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Code will be executed on the main thread
                            // if stock in portfolio set drawable to green
                            imp_View_in_Portfolio.setImageDrawable(getResources().getDrawable(R.drawable.green_check_mark));
                        }
                    });
                    return;
                }
            }
            // set the image to red x
            imp_View_in_Portfolio.setImageDrawable(getResources().getDrawable(R.drawable.red_x));

        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(LineChart_V2.this,LineChart_V2.class);
        String s = symbol.get(position);
        intent.putExtra("Symbol",s);
        startActivity(intent);
        linechartcount++;
    }
}