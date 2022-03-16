package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphFloatingActionButton;
import soup.neumorphism.NeumorphTextView;

public class LineChart_V2 extends AppCompatActivity implements RecyclerViewInterface, PopupMenu.OnMenuItemClickListener  {
    RecyclerView recyclerView;
    RecViewAdpSimStocks RecViewAdpSimStocks;
    DBHelper DB;
    ApiCalls api = new ApiCalls();
    LineChart lineChart;
    // For card view
    TextView openValue,lowValue,highValue,closeValue,fifty_two_week_high_TV,fifty_two_week_low_TV,
            divAmountView,exDivDate,highestView,lowestView,oneDayPercentChange;
    double highVal;
    NeumorphTextView symbolView,priceView,highestvalue,lowestValue;
    NeumorphButton threeMoTV,sixMoTV,oneYear,fiveDay;
    NeumorphFloatingActionButton addStock;
    ImageView imp_View_in_Portfolio,percentDrawable;
    int days = 5;
    double lowest,highest;
    String fifty_two_week_loww,fifty_two_week_highh,high,low,open,close, exDate;
    double divAmount;
    ArrayList<String> historicalPrice5 = new ArrayList<>();

    List<Entry> lineEntries = new ArrayList<>();
    boolean stockInPortfolio = false;
    ArrayList<String> symbol, price, DivYeild, dividend_amount,percent_Change;
    String sym = "";
    int linechartcount = 0;
    DecimalFormat formatter = new DecimalFormat("#,###.00");

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart_v2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setTitle("stock price history");

        // make the action bar match with the status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int color = Color.parseColor("#808080");
        window.setStatusBarColor(color);

        ActionBar bar;
        bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#808080"));
        if (bar != null) {
            bar.setBackgroundDrawable(cd);
        }
        //----------------------------------


        DB = new DBHelper(this);
        sym = String.valueOf(getIntent().getSerializableExtra("Symbol"));

        // gets the regular stock price for the given stock
        Thread thread1 = new Thread(() -> {
            try {
                ApiCalls.regularMarketPrice(sym);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            // Create a handler that associated with Looper of the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());
            // Send a task to the MessageQueue of the main thread
            mainHandler.post(() -> {
                // Code will be executed on the main thread
                priceView.setText(String.format("$%s", ApiCalls.stock_price));
                oneDayPercentChange.setText(String.format("%s%%", String.valueOf(ApiCalls.oneDayPercentChange)));
                if(ApiCalls.oneDayPercentChange > 0) {
                    percentDrawable.setImageDrawable(getResources().getDrawable(R.drawable.green_up_arrow_png));
                } else{
                    percentDrawable.setImageDrawable(getResources().getDrawable(R.drawable.red_down_arrow_png));
                }
            });
        }); thread1.start();

        // gets all the views, buttons and graphs
        getUiElements();

        // similar stocks thread && store data in arrays
        Thread simStocksThread = new Thread(() -> {
            try {
                api.similarStocks(sym);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            Handler mainHandler = new Handler(Looper.getMainLooper());
            // Send a task to the MessageQueue of the main thread
            mainHandler.post(() -> {
                try {
                    store_data_in_arrays();
                } catch (JSONException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }); simStocksThread.start();

        // historical prices
        Thread thread = new Thread(() -> {
            try {
                historicalPrices(sym,days);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // if the stock is in the portfolio or not
        Thread newCall = new Thread(() ->
                inPortfolio(sym));
        newCall.start();

        //52 week thread
        try {
            fiftyTwoWeek(sym);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    // gets the onclicklisteners for all the buttons
    private void setOnClickListeners() {
        threeMoTV.setOnClickListener(view -> {
            linechartcount++;
            days = 90;
            Thread thread = new Thread(() -> {
                try {
                    historicalPrices(sym,days);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
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

        });
        sixMoTV.setOnClickListener(view -> {
            linechartcount++;
            days = 180;
            Thread t = new Thread(() -> {
                try {
                    historicalPrices(sym,days);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
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
        });
        oneYear.setOnClickListener(view -> {
            linechartcount++;
            days = 365;
            Thread t = new Thread(() -> {
                try {
                    historicalPrices(sym,days);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
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
        });
        fiveDay.setOnClickListener(view -> {
            linechartcount++;
            days = 5;
            Thread t = new Thread(() -> {
                try {
                    historicalPrices(sym,days);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
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
        });

    }

    // arrays for the similar stocks recycler view
    private void getArraysForRecView() {
        symbol = new ArrayList<>();
        price = new ArrayList<>();
        DivYeild = new ArrayList<>();
        dividend_amount = new ArrayList<>();
        percent_Change = new ArrayList<>();
    }

    // store the data initialized arrays so that they can be used for the rec view
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void store_data_in_arrays() throws JSONException, IOException, InterruptedException {
        Thread t = new Thread(() -> {
            try {
                ApiCalls.getOnlyStockPriceSim();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < ApiCalls.recommendedSymbols.size(); i++){
                    String temp = ApiCalls.recommendedSymbols.get(i);
                    symbol.add(temp);
                    price.add(String.valueOf(ApiCalls.priceSim.get(i)));
                    try {
                        ApiCalls.getDivYieldSimStocks(temp);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    dividend_amount.add(String.valueOf(ApiCalls.annualDividendSimStockStocks));
                    DivYeild.add(formatter.format(ApiCalls.DivYieldSim));
                    percent_Change.add(String.valueOf(ApiCalls.percentChange.get(i)));
                }


           setRecyclerView();

        });
        t.start();


    }

    // gets all the views, buttons and graphs
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
        addStock = findViewById(R.id.addStock);
        percentDrawable = findViewById(R.id.percentDrawable);
        oneDayPercentChange = findViewById(R.id.oneDayPercentChange);

        imp_View_in_Portfolio = findViewById(R.id.imp_View_in_Portfolio);

        recyclerView = findViewById(R.id.similarStocksRecView);

        setOnClickListeners();
        getArraysForRecView();
        symbolView.setText(sym.toUpperCase());
        imp_View_in_Portfolio.setImageDrawable(getResources().getDrawable(R.drawable.blank_square));


    }

    // method to set the recycler view on the activity when the data is loaded
    public void setRecyclerView() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        // Send a task to the MessageQueue of the main thread
        mainHandler.post(() -> {
            // Code will be executed on the main thread
            recyclerView = findViewById(R.id.similarStocksRecView);
            RecViewAdpSimStocks = new RecViewAdpSimStocks(LineChart_V2.this, symbol, price,
                    DivYeild, dividend_amount,percent_Change,LineChart_V2.this);
            recyclerView.setAdapter(RecViewAdpSimStocks);
            recyclerView.setLayoutManager(new LinearLayoutManager(LineChart_V2.this));
        });


    }

    // gets the old stock prices for the line chart
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
        String all = Objects.requireNonNull(response.body()).string();
        String allres = new JSONObject(all).getString(s);
        String allre = new JSONObject(allres).getString("close");
        JSONArray t = new JSONArray(allre);
        for(int i = 0; i < t.length();i++){
            String T = String.valueOf(t.get(i));
            historicalPrice5.add(T);
        }
        negOrPos();
        getDataSet();
    }

    // take the historical prices arraylist and store it in the lineEntries arraylist
    private void getDataSet() {
        lineEntries.clear();
        for(int i = 0; i < historicalPrice5.size(); i++) {
            float data = Float.parseFloat(historicalPrice5.get(i));
            lineEntries.add(new Entry(i, data));

        }
        // Create a handler that associated with Looper of the main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());

        // Send a task to the MessageQueue of the main thread
        mainHandler.post(() -> {
            // Code will be executed on the main thread
            lineChart();
            highestValueForLineChart();
            lowestValueForLineChart();
        });

    }

    // get the highest value of the historical prices array list
    public void highestValueForLineChart() {
        highVal = Double.parseDouble(Collections.max(historicalPrice5));
        BigDecimal a = new BigDecimal(highVal);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        highVal = Double.parseDouble(String.valueOf(b));
        highestvalue.setText(String.format("$%s",(highVal)));

    }

    // gets the lowest value of the historical prices array list
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
        lowestValue.setText(String.format("$%s",(b)));


    }

    // check if the last value of the historical prices array list is lower than the first value in the arraylist
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

    // method just to reload the activity
    public void reloadActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    // method to initialize all the values in the line chart
    public void lineChart() {
        lineChart = findViewById(R.id.lineChart);
        if(lineEntries.isEmpty()){
            getDataSet();
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(2);
        // if the lowest is greater than the highest turn the line graph red or else blue
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

    // sets all the fifty two week views with values after the data is loaded
    public void fiftyTwoWeek(String symb) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            api.stockQuote(symb);
            api.Dividend(symb);
            // Create a handler that associated with Looper of the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());

            // Send a task to the MessageQueue of the main thread

            mainHandler.post(() -> {
                // Code will be executed on the main thread
                high = api.high;
                low = api.low;
                open = api.open;
                close = api.close;

                openValue.setText(String.format("$%s", formatter.format(Double.parseDouble(String.valueOf(open)))));
                lowValue.setText(String.format("$%s", formatter.format(Double.parseDouble(String.valueOf(low)))));
                highValue.setText(String.format("$%s", formatter.format(Double.parseDouble(String.valueOf(high)))));
                closeValue.setText(String.format("$%s", formatter.format(Double.parseDouble(String.valueOf(close)))));

                fifty_two_week_loww = api.fifty_two_week_low;
                fifty_two_week_highh = api.fifty_two_week_high;

                fifty_two_week_low_TV.setText(String.format("$%s", formatter.format(Double.parseDouble(String.valueOf(fifty_two_week_loww)))));
                fifty_two_week_high_TV.setText(String.format("$%s", formatter.format(Double.parseDouble(String.valueOf(fifty_two_week_highh)))));

                divAmount = ApiCalls.amount_of_dividend;
                exDate = ApiCalls.date_of_dividend;
                divAmountView.setText(String.format("$%s", divAmount));
                exDivDate.setText(exDate);
            });
        }); thread.start();

    }

    // read the data to see if the stock is in the portfolio
    public void inPortfolio(String sym) {
        stockInPortfolio = false;
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            // Create a handler that associated with Looper of the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());

            // Send a task to the MessageQueue of the main thread
            mainHandler.post(() -> {
                // Code will be executed on the main thread
                Toast.makeText(LineChart_V2.this, "No Stocks", Toast.LENGTH_SHORT).show();
            });
            //Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            //if stock is in the portfolio
            while(cursor.moveToNext()) {
                String t = cursor.getString(0);
                String symU = sym.toUpperCase();
                String tU = t.toUpperCase();
                if (symU.equals(tU)) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    // Send a task to the MessageQueue of the main thread
                    mainHandler.post(() -> {
                        // Code will be executed on the main thread
                        // if stock in portfolio set drawable to green
                        imp_View_in_Portfolio.setImageDrawable(getResources().getDrawable(R.drawable.green_check_mark));
                        addStock.setOnClickListener(view -> {
                            PopupMenu popup = new PopupMenu(LineChart_V2.this, view);
                            popup.setOnMenuItemClickListener(LineChart_V2.this);
                            popup.inflate(R.menu.line_chart_menu_stock_in_portfolio);
                            popup.show();
                        });
                    });
                    return;
                }
            }
            // set the image to red x
            Handler mainHandler = new Handler(Looper.getMainLooper());

            // Send a task to the MessageQueue of the main thread
            // if stock not in portfolio
            mainHandler.post(() -> {
                // Code will be executed on the main thread
                // if stock in portfolio set drawable to green
                imp_View_in_Portfolio.setImageDrawable(getResources().getDrawable(R.drawable.red_x));
                addStock.setOnClickListener(view -> {
                    PopupMenu popup = new PopupMenu(LineChart_V2.this, view);
                    popup.setOnMenuItemClickListener(LineChart_V2.this);
                    popup.inflate(R.menu.line_chart_menu_stock_not_in_portfolio);
                    popup.show();
                });
            });

        }
        cursor.close();
    }

    // if a row is clicked on the similar stocks rec view it will go the line chart for that stock
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(LineChart_V2.this,LineChart_V2.class);
        String s = symbol.get(position);
        intent.putExtra("Symbol",s);
        startActivity(intent);
        linechartcount++;
    }

    // for if you want to add the stock to the portfolio, update or delete the stock
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.addStockLineChart:
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.line_chart_popup_add_stock);
                dialog.show();
                Button add = (Button) dialog.findViewById(R.id.add_Stock_popup);
                Button cancel = (Button) dialog.findViewById(R.id.button_cancel_user_data);
                DBHelper DB = new DBHelper(this);
                add.setOnClickListener(view -> {
                    EditText shar = (EditText) dialog.findViewById(R.id.Shares_add_stock_popup);
                    EditText cps = (EditText) dialog.findViewById(R.id.average_cost_popup);
                    String sha = shar.getText().toString();
                    double shares = Double.parseDouble(sha);
                    String ag = cps.getText().toString();
                    double avgCost = Double.parseDouble(ag);
                    try {
                        DB.addStocksDialog(sym, shares, avgCost);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                });
                cancel.setOnClickListener(view -> dialog.dismiss());
                return true;
            case R.id.updateStockLineChart:
                Dialog update = new Dialog(this);
                update.setContentView(R.layout.line_chart_popup_update_stock);
                update.show();
                Button update2 = (Button) update.findViewById(R.id.update_Stock_popup);
                Button cancel3 = (Button) update.findViewById(R.id.button_cancel_user_data);
                DBHelper DB3 = new DBHelper(this);
                update2.setOnClickListener(view -> {
                    EditText share = (EditText) update.findViewById(R.id.Shares_add_stock_popup);
                    EditText Avg = (EditText) update.findViewById(R.id.average_cost_popup);
                    String sha = share.getText().toString();
                    String avgg = Avg.getText().toString();
                    double shares = Double.parseDouble(sha);
                    double aveg = Double.parseDouble(avgg);
                    DB3.updateStock(sym, shares, aveg);
                    update.dismiss();
                });
                cancel3.setOnClickListener(view ->
                        update.dismiss());
                return true;
            case R.id.delete2:
                Dialog delete = new Dialog(this);
                delete.setContentView(R.layout.line_chart_popup_delete_stock);
                delete.show();
                Button delete2 = (Button) delete.findViewById(R.id.delete_Stock_popup);
                Button cancel2 = (Button) delete.findViewById(R.id.button_cancel_user_data);
                DBHelper DB2 = new DBHelper(this);
                delete2.setOnClickListener(view -> {
                    DB2.deleteStock(sym);
                    delete.dismiss();
                });
                cancel2.setOnClickListener(view ->
                        delete.dismiss());
                return true;
            default:
                return false;
        }

    }
}