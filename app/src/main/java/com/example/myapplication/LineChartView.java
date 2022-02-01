package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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

public class LineChartView extends AppCompatActivity implements RecyclerViewInterface {
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
    ArrayList<String> historicalPrice30 = new ArrayList<>();
    List<Entry> lineEntries = new ArrayList<Entry>();
    boolean stockInPortfolio = false;
    ArrayList<String> symbol, price, DivYeild, dividend_amount,percent_Change;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setTitle("stock price history");
        DB = new DBHelper(this);
        String sym = String.valueOf(getIntent().getSerializableExtra("Symbol"));
        try {
            api.similarStocks(sym);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        //------------------------
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                getArraysForRecView();
            }
        }); th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //getArraysForRecView();
        //-------------------------
        try {
            store_data_in_arrays();
        } catch (JSONException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

        //getUiElements();
        //-----------------------
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                api.price(sym);
            }
        }); thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //getUiElements();
        priceView.setText("$"+String.valueOf(ApiCalls.stock_price));
        symbolView.setText(sym);
        try {
            fiftyTwoWeek(sym);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getDivAmount_ExDiv(sym);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    historicalPrices(sym,days);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        highestValueForLineChart();
        lowestValueForLineChart();
        negOrPos();
        lineChart();

        threeMoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                days = 90;
                try {
                    historicalPrices(sym,days);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
                days = 180;
                try {
                    historicalPrices(sym,days);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
                days = 365;
                try {
                    historicalPrices(sym,days);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
                days = 5;
                try {
                    historicalPrices(sym,days);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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

        inPortfolio(sym);

        if(stockInPortfolio = false) {
            imp_View_in_Portfolio.setImageResource(R.drawable.red_x);
        }
        //getUiElements();
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
                    try {
                        api.regularMarketPrice(temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    price.add(String.valueOf(api.stock_price));
                    api.Dividend(temp);
                    dividend_amount.add(String.valueOf(api.amount_of_dividend));
                    try {
                        api.getDivYield(temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(api.Dividend_Yield == 0.0){
                        DivYeild.add("no dividend");
                    } else {
                        DivYeild.add(String.valueOf(api.Dividend_Yield));
                    }
                    try {
                        api.getPercentChange(temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    percent_Change.add(String.valueOf(api.percent_Change));
                }

                 }
                });
               t.start();
               t.join();
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
        RecViewAdpSimStocks = new RecViewAdpSimStocks(this, symbol, price,
                DivYeild, dividend_amount,percent_Change,this);
        recyclerView.setAdapter(RecViewAdpSimStocks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void historicalPrices(String s, int days) throws IOException, JSONException {
        String num_days = "";
        if (historicalPrice30 != null){
            historicalPrice30.clear();
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
            historicalPrice30.add(T);
        }
        getDataSet();
    }

    private List<Entry> getDataSet() {
        lineEntries.clear();
        for(int i = 0; i <historicalPrice30.size(); i++) {
            float data = Float.parseFloat(historicalPrice30.get(i));
            lineEntries.add(new Entry(i, data));

        }
        return lineEntries;
    }

    public void highestValueForLineChart() {
        highVal = Double.parseDouble(Collections.max(historicalPrice30));
        BigDecimal a = new BigDecimal(highVal);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        highestvalue.setText(String.format("$%s", String.valueOf(b)));

    }

    public void lowestValueForLineChart() {
        double min = 0.0;
        for (int i = 0; i < historicalPrice30.size(); i++) {
            if (i == 0){
                min = Double.parseDouble(historicalPrice30.get(i));
            }
            if (Double.parseDouble(historicalPrice30.get(i)) < min) {
                min = Double.parseDouble(historicalPrice30.get(i));
            }
        }
        BigDecimal a = new BigDecimal(min);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        lowestValue.setText(String.format("$%s", String.valueOf(b)));


    }

    public void negOrPos(){

        double l = Double.parseDouble(historicalPrice30.get(0));
        double h = Double.parseDouble(historicalPrice30.get(historicalPrice30.size()-1));
        lowest = l;
        highest = h;
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
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(historicalPrice30.size()));
        xAxis.setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.getDescription().setText("Past 5 day stock price");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.setData(lineData);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        if(lowest > highest){
            fiveDay.setBackgroundColor(Color.rgb(255, 0, 0));
        }else {
            fiveDay.setBackgroundColor(Color.rgb(0, 150, 255));
        }

    }

    public void fiftyTwoWeek(String symb) throws InterruptedException {
       Thread thread = new Thread(new Runnable() {
           @Override
           public void run() {
               api.stockQuote(symb);
           }
       }); thread.start(); //thread.join();

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
    }

    public void getDivAmount_ExDiv(String sym){
        api.Dividend(sym);
        divAmount = ApiCalls.amount_of_dividend;
        exDate = ApiCalls.date_of_dividend;
        String amount = String.valueOf(divAmount);
        divAmountView.setText("$"+amount);
        exDivDate.setText(exDate);
    }

    public void inPortfolio(String sym) {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor.moveToNext()) {
                if (sym == cursor.getString(0));
                stockInPortfolio = true;
            }
        }
    }

    @Override
    public void onItemClick(int position) {

    }
}
