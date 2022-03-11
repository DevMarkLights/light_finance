package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;

public class portfolio_V2 extends AppCompatActivity implements RecyclerViewInterface, PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerView;
    ArrayList<String> tmv, Symbol, price, profit_loss, average_cost, Dividend_Yield, marketValue, frequency;
    TextView total_market_value, totalProfitLossView, avgDyview, totalAnnualDiv;
    EditText searchInput;
    DBHelper DB;

    ApiCalls api = new ApiCalls();
    NeumorphCardView annualDividendCardView,divYieldPerStock,marketValueCardView,ProfitValueCard;
    RecViewAdapter recViewAdapter;
    ArrayList<Double> total_Profit_Loss, average_Dividend_Yield;
    static double totalMV, totalProfitLoss, avgDY, annualDividend;
    NeumorphFloatingActionButton neumorphFloatingActionButton,search_stocks,futureValue_foreground,LinkToSimilarStocksActivity;

    // for similar stocks
    RecyclerView simStocksRecViewCardView;
    SimilarStocksRecViewAdpFromDivYield SimilarStocksRecViewAdpFromDivYield;
    ArrayList<String> symbols = new ArrayList<String>();
    ArrayList<String> price2 = new ArrayList<String>();
    ArrayList<String> price_change_percent_ytd = new ArrayList<String>();
    ArrayList<String> dividend_yield_percent = new ArrayList<String>();
    ArrayList<String> dividend_rate_Annual = new ArrayList<String>();

    //for portfolio update
    String symbolU;
    double sharesU;
    static double mktvalU;
    double costbasisU;
    static boolean dataUpdated = false;
    //--------------------
    DecimalFormat formatter = new DecimalFormat("#,###.00");


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_v2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setTitle("Portfolio");

        DB = new DBHelper(this);
        //-------------------------------
        getAllArrayListForUI();
        //--------------------------------------------
        getUiElements();
        //-----------------------------------------------
        recViewAdapter = new RecViewAdapter(this, Symbol, price, profit_loss, average_cost,
                Dividend_Yield, marketValue, frequency,this);
        recyclerView.setAdapter(recViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalMarketValue();
        totalProfitLoss();
        averageDividendYield();
        annualDividend();
        storeDataInArrays();
        //-------------------------
        getAllOnClickListners();
        // if stocks in database has been updated once then skip step
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(Calendar.getInstance().getTime());

        if(!dataUpdated && (time.startsWith("09") || time.startsWith("10") || time.startsWith("11") || time.startsWith("12") ||
                time.startsWith("13") || time.startsWith("14") || time.startsWith("15")|| time.startsWith("16"))) {
            Thread newCall = new Thread(() -> {
                try {
                    getDataForUpdate();
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    // Send a task to the MessageQueue of the main thread
                    mainHandler.post(() -> {
                        // Code will be executed on the main thread
                        totalMarketValue();
                        totalProfitLoss();
                        averageDividendYield();
                        annualDividend();
                    });
                } catch (InterruptedException | JSONException | IOException e) {
                    e.printStackTrace();
                }
            });
            newCall.start();
        }


    }

    public void getAllOnClickListners(){
        annualDividendCardView.setOnClickListener(view -> {
            Intent intent = new Intent(portfolio_V2.this, TotalDividends.class);
            startActivity(intent);
        });

        divYieldPerStock.setOnClickListener(view -> {
            Intent intent = new Intent(portfolio_V2.this,dividendYieldPerStock.class);
            startActivity(intent);
        });

        marketValueCardView.setOnClickListener(view -> {
            Intent intent = new Intent(portfolio_V2.this,TotalMarketValuePerStock.class);
            startActivity(intent);
        });

        ProfitValueCard.setOnClickListener(view -> {
            Intent intent = new Intent(portfolio_V2.this,TotalProfitValuePerStock.class);
            startActivity(intent);
        });

        neumorphFloatingActionButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(portfolio_V2.this, view);
            popup.setOnMenuItemClickListener(portfolio_V2.this);
            popup.inflate(R.menu.menu_drop_down);
            popup.show();
        });

        search_stocks.setOnClickListener(view -> {
            String s = searchInput.getText().toString();
            if (s.isEmpty()){
                Toast.makeText(portfolio_V2.this, "input is empty", Toast.LENGTH_LONG).show();
            }else{
                Thread thread = new Thread(() ->
                        api.checkSymbol(s)); thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //if stock doesn't exist then tells the user its doesn't exist and check input
                if(!api.stockExists){
                    Toast.makeText(portfolio_V2.this, "Stock doesnt exist. Check input", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(portfolio_V2.this,LineChart_V2.class);
                intent.putExtra("Symbol",s);
                startActivity(intent);
            }
        });

        futureValue_foreground.setOnClickListener(view -> {
            Intent intent = new Intent(portfolio_V2.this,FutureValue.class);
            startActivity(intent);
        });

        LinkToSimilarStocksActivity.setOnClickListener(view ->{
            Intent intent = new Intent(portfolio_V2.this,SimilarStocks.class);
            startActivity(intent);
        });
    }

    public void getUiElements() {
        recyclerView = findViewById(R.id.portfolio);
        totalProfitLossView = findViewById(R.id.TPV_View);
        avgDyview = findViewById(R.id.AvgDivYieldView);
        total_market_value = findViewById(R.id.TMVtextV);
        totalAnnualDiv = findViewById(R.id.AnnualDivView);
        annualDividendCardView = findViewById(R.id.AnnualDividendCardView);
        divYieldPerStock = findViewById(R.id.averageDividendYieldCard);
        marketValueCardView = findViewById(R.id.totalMarketValueCard);
        ProfitValueCard = findViewById(R.id.TPV_Card);
        neumorphFloatingActionButton = findViewById(R.id.neumorphFloatingActionButton);
        searchInput = findViewById(R.id.searchInput);
        search_stocks = findViewById(R.id.search_stocks);
        futureValue_foreground = findViewById(R.id.futureValue_foreground);

        // for similar stocks
        simStocksRecViewCardView = findViewById(R.id.simStocksRecViewCardView);
        symbols = new ArrayList<String>();
        price = new ArrayList<String>();
        price_change_percent_ytd = new ArrayList<String>();
        dividend_rate_Annual = new ArrayList<String>();
        dividend_yield_percent = new ArrayList<String>();
        LinkToSimilarStocksActivity = findViewById(R.id.LinkToSimilarStocksActivity);
    }

    public void getAllArrayListForUI() {
        Symbol = new ArrayList<>();
        price = new ArrayList<>();
        profit_loss = new ArrayList<>();
        average_cost = new ArrayList<>();
        Dividend_Yield = new ArrayList<>();
        marketValue = new ArrayList<>();
        frequency = new ArrayList<>();
        tmv = new ArrayList<>();
        total_Profit_Loss = new ArrayList<>();
        average_Dividend_Yield = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void storeDataInArrays() {
        Thread t = new Thread(() -> {
            Cursor cursor = DB.readAllData();
            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    Symbol.add(cursor.getString(0));
                    price.add(cursor.getString(1));
                    profit_loss.add(cursor.getString(4));
                    average_cost.add(cursor.getString(3));
                    Dividend_Yield.add(cursor.getString(6));
                    marketValue.add(cursor.getString(10));
                    frequency.add(cursor.getString(8));
                }
            }
            try {
                getDataForUpdate();
            } catch (InterruptedException | JSONException | IOException e) {
                e.printStackTrace();
            }
        }); t.start();

    }

    public void totalMarketValue() {
        Thread t = new Thread(() -> {
            tmv.clear();
            Cursor cursor = DB.readAllData();
            if (cursor.getCount() == 0) {
                Toast.makeText(portfolio_V2.this, "No stocks", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    tmv.add(cursor.getString(10));
                }
            }
            cursor.close();
            double total = 0.0;
            for (int i = 0; i < tmv.size(); i++) {
                total = total + Double.parseDouble(tmv.get(i));
                totalMV = total;
            }
            BigDecimal a = new BigDecimal(totalMV);
            BigDecimal b = a.setScale(3, RoundingMode.DOWN);
            totalMV = Double.parseDouble(String.valueOf(b));

            Handler mainHandler = new Handler(Looper.getMainLooper());
            // Send a task to the MessageQueue of the main thread
            mainHandler.post(() -> {
                // Code will be executed on the main thread
                total_market_value.setText("");
                total_market_value.setText(String.format("$%s", formatter.format(totalMV)));
            });

        }); t.start();

    }

    public void averageDividendYield() {
        Thread t = new Thread(() -> {
            average_Dividend_Yield.clear();
            Cursor cursor = DB.readAllData();
            if (cursor.getCount() == 0) {
                Toast.makeText(portfolio_V2.this, "No Stocks", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    average_Dividend_Yield.add(Double.valueOf(cursor.getString(6)));
                }
            }
            cursor.close();
            double size = average_Dividend_Yield.size();
            double total = 0.0;
            for (int i = 0; i < average_Dividend_Yield.size(); i++) {
                total = total + Double.parseDouble(String.valueOf(average_Dividend_Yield.get(i)));
                avgDY = total / size;
            }
            BigDecimal a = new BigDecimal(avgDY);
            BigDecimal b = a.setScale(3, RoundingMode.DOWN);
            avgDY = Double.parseDouble(String.valueOf(b));

            Handler mainHandler = new Handler(Looper.getMainLooper());
            // Send a task to the MessageQueue of the main thread
            mainHandler.post(() -> {
                // Code will be executed on the main thread
                avgDyview.setText("");
                avgDyview.setText(String.format("%s%%", avgDY));
                try {
                    similarStocksArrays();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            });

        }); t.start();

    }

    public void totalProfitLoss() {
            Thread t = new Thread(() -> {
                total_Profit_Loss.clear();
                Cursor cursor = DB.readAllData();
                if (cursor.getCount() == 0) {
                    Toast.makeText(portfolio_V2.this, "No stocks", Toast.LENGTH_SHORT).show();
                } else {
                    while (cursor.moveToNext()) {
                        total_Profit_Loss.add(Double.valueOf(cursor.getString(4).substring(0, 3)));
                    }
                }
                cursor.close();
                double sum = 0;
                for (int i = 0; i < total_Profit_Loss.size(); i++) {
                    sum = sum + Double.parseDouble(String.valueOf(total_Profit_Loss.get(i)));
                }
                totalProfitLoss = sum;
                BigDecimal a = new BigDecimal(totalProfitLoss);
                BigDecimal b = a.setScale(3, RoundingMode.DOWN);
                double tpl = Double.parseDouble(String.valueOf(b));

                Handler mainHandler = new Handler(Looper.getMainLooper());
                // Send a task to the MessageQueue of the main thread
                mainHandler.post(() -> {
                    // Code will be executed on the main thread
                    totalProfitLossView.setText(String.format("$%s", formatter.format(tpl)));
                    totalProfitLossView.setTextColor(Color.rgb(80, 200, 120));
                });
            });
            t.start();

    }

    public void annualDividend() {
        Thread t = new Thread(() -> {
            annualDividend=0;
            Cursor cursor = DB.readAllData();
            if (cursor.getCount() == 0) {
                Toast.makeText(portfolio_V2.this, "No Stocks", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    float div = Float.parseFloat(cursor.getString(5));
                    float shares = Float.parseFloat(cursor.getString(2));
                    String f = cursor.getString(8);
                    float totaldiv;
                    if (f.startsWith("M")) {
                        totaldiv = (div * shares) * 12;
                    } else if (f.startsWith("Q")) {
                        totaldiv = (div * shares) * 4;
                    } else if (f.startsWith("S")) {
                        totaldiv = (div * shares) * 2;
                    } else {
                        totaldiv = div * shares;
                    }
                    annualDividend = annualDividend + totaldiv;
                }
                cursor.close();

                Handler mainHandler = new Handler(Looper.getMainLooper());
                // Send a task to the MessageQueue of the main thread
                mainHandler.post(() -> {
                    // Code will be executed on the main thread
                    BigDecimal a = new BigDecimal(annualDividend);
                    BigDecimal b = a.setScale(2, RoundingMode.DOWN);
                    totalAnnualDiv.setText(String.format("$%s", formatter.format(b)));
                });
            }

        }); t.start();

    }

    public void reloadActivity () {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getDataForUpdate() throws InterruptedException, JSONException, IOException {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                symbolU = cursor.getString(0);
                sharesU = Double.parseDouble(cursor.getString(2));
                costbasisU = Double.parseDouble(cursor.getString(3));
                ApiCalls.getOnlyStockPriceUpdadte(symbolU);
                updatePortfolio();
            }
            cursor.close();
            dataUpdated = true;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updatePortfolio() throws JSONException, IOException {
        mktvalU = ApiCalls.stockPriceU * sharesU;
        BigDecimal a = new BigDecimal(mktvalU);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        mktvalU = Double.parseDouble(String.valueOf(b));
        ApiCalls.profitLossUpdate(sharesU, costbasisU);
        ApiCalls.dividendYieldUpdate(symbolU,sharesU);
        DB.updateDatabaseStocksRegularly(symbolU,sharesU);

    }

    // for recycler view
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(portfolio_V2.this,LineChart_V2.class);
        String s = Symbol.get(position);
        intent.putExtra("Symbol",s);
        startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.addStock2:
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.popup_input_dialog_addstock);
                dialog.show();
                Button add = (Button) dialog.findViewById(R.id.add_Stock_popup);
                Button cancel = (Button) dialog.findViewById(R.id.button_cancel_user_data);
                DBHelper DB = new DBHelper(this);
                add.setOnClickListener(view -> {
                    EditText sy = (EditText) dialog.findViewById(R.id.symbol);
                    EditText shar = (EditText) dialog.findViewById(R.id.Shares_add_stock_popup);
                    EditText cps = (EditText) dialog.findViewById(R.id.average_cost_popup);
                    String symbol = sy.getText().toString();
                    String sha = shar.getText().toString();
                    double shares = Double.parseDouble(sha);
                    String ag = cps.getText().toString();
                    double avgCost = Double.parseDouble(ag);
                    // check to see if symbol is valid
                    api.checkSymbol(symbol);
                    // if symbol is valid then add the stock
                    if (api.stockExists){
                        try {
                            DB.addStocksDialog(symbol, shares, avgCost);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        reloadActivity();
                    } else {
                        Toast.makeText(portfolio_V2.this, "Stock doesnt exist. Check input", Toast.LENGTH_LONG).show();
                    }

                });
                cancel.setOnClickListener(view -> dialog.dismiss());
                return true;
            default:
                return false;
        }
    }

    // for similar stocks recycler view
    public void similarStocksArrays() throws IOException, JSONException {
        float DY = (float) portfolio_V2.avgDY;
        float low = DY - 1;
        float high = DY + 2;
        Thread ha = new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "SELECT * FROM stocks WHERE NOT price_change_percent_ytd IS " +
                    "NULL AND dividend_yield_percent >= "+low+" AND dividend_yield_percent <= "+high+" ORDER BY dividend_yield_percent  DESC LIMIT 5");
            Request request = new Request.Builder()
                    .url("https://hotstoks-sql-finance.p.rapidapi.com/query")
                    .post(body)
                    .addHeader("content-type", "text/plain")
                    .addHeader("x-rapidapi-host", "hotstoks-sql-finance.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String all = null;
            try {
                all = Objects.requireNonNull(response.body()).string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String allres = null;
            try {
                allres = new JSONObject(all).getString("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray t = null;
            try {
                t = new JSONArray(allres);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i <t.length();i++){
                JSONObject l = null;
                try {
                    l = new JSONObject(String.valueOf(t.get(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String symbol = null;
                try {
                    symbol = new JSONObject(String.valueOf(l)).getString("symbol");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String stockPrice = null;
                try {
                    stockPrice = new JSONObject(String.valueOf(l)).getString("price");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String PCPY = null;
                try {
                    PCPY = new JSONObject(String.valueOf(l)).getString("price_change_percent_ytd");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String DivYieldPercent = null;
                try {
                    DivYieldPercent = new JSONObject(String.valueOf(l)).getString("dividend_yield_percent");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String DivRateAnnual = null;
                try {
                    DivRateAnnual = new JSONObject(String.valueOf(l)).getString("dividend_rate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                    SimilarStocksRecViewAdpFromDivYield = new SimilarStocksRecViewAdpFromDivYield(portfolio_V2.this, symbols, price, price_change_percent_ytd, dividend_yield_percent,
                            dividend_rate_Annual, portfolio_V2.this);
                    simStocksRecViewCardView.setAdapter(SimilarStocksRecViewAdpFromDivYield);
                    simStocksRecViewCardView.setLayoutManager(new LinearLayoutManager(portfolio_V2.this));
                }
            });
        }); ha.start();


    }

}