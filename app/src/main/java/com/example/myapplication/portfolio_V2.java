package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;

public class portfolio_V2 extends AppCompatActivity implements RecyclerViewInterface, PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerView;
    ArrayList<String> tmv, Symbol, price, profit_loss, average_cost, Dividend_Yield, marketValue, frequency;
    TextView total_market_value, totalProfitLossView, avgDyview, totalAnnualDiv;
    EditText searchInput;
    DBHelper DB;

    ApiCalls api = new ApiCalls();
    NeumorphCardView annualDividendCardView,divYieldPerStock,marketValueCardView,ProfitValueCard,similarStocksCardView;
    RecViewAdapter recViewAdapter;
    ArrayList<Double> total_Profit_Loss, average_Dividend_Yield;
    static double totalMV, totalProfitLoss, avgDY, annualDividend;
    NeumorphFloatingActionButton neumorphFloatingActionButton,search_stocks,futureValue_foreground;

    //for portfolio update
    String symbolU,freqU;
    double sharesU;
    static double mktvalU;
    double costbasisU;
    static boolean dataUpdated = false;
    //--------------------
    DecimalFormat formatter = new DecimalFormat("#,###.00");


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
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(Calendar.getInstance().getTime());

        if(!dataUpdated && time.startsWith("09") || time.startsWith("10") || time.startsWith("11") || time.startsWith("12") ||
                time.startsWith("13") || time.startsWith("14") || time.startsWith("15")|| time.startsWith("16")) {
            Thread newCall = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getDataForUpdate();
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        // Send a task to the MessageQueue of the main thread
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Code will be executed on the main thread
                                totalMarketValue();
                                totalProfitLoss();
                                averageDividendYield();
                                annualDividend();
                            }
                        });
                    } catch (InterruptedException | JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            newCall.start();
        }
    }

    public void getAllOnClickListners(){
        annualDividendCardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(portfolio_V2.this, TotalDividends.class);
                startActivity(intent);
            }
        });

        divYieldPerStock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(portfolio_V2.this,dividendYieldPerStock.class);
                startActivity(intent);
            }
        });

        marketValueCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(portfolio_V2.this,TotalMarketValuePerStock.class);
                startActivity(intent);
            }
        });

        ProfitValueCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(portfolio_V2.this,TotalProfitValuePerStock.class);
                startActivity(intent);
            }
        });

        neumorphFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(portfolio_V2.this, view);
                popup.setOnMenuItemClickListener(portfolio_V2.this);
                popup.inflate(R.menu.menu_drop_down);
                popup.show();
            }
        });

        search_stocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = searchInput.getText().toString();
                if (s.isEmpty()){
                    return;
                }else{
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            api.checkSymbol(s);
                        }
                    }); thread.start();
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
            }
        });

        futureValue_foreground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(portfolio_V2.this,FutureValue.class);
                startActivity(intent);
            }
        });

        similarStocksCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(portfolio_V2.this,SimilarStocks.class);
                startActivity(intent);
            }
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
        similarStocksCardView = findViewById(R.id.similarStocksCardView);
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

    void storeDataInArrays() {
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
    }

    public void totalMarketValue() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                tmv.clear();
                Cursor cursor = DB.readAllData();
                if (cursor.getCount() == 0) {
                    Toast.makeText(portfolio_V2.this, "No stocks", Toast.LENGTH_SHORT).show();
                } else {
                    while (cursor.moveToNext()) {
                        tmv.add(cursor.getString(10));
                    }
                }
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
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Code will be executed on the main thread
                        total_market_value.setText("");
                        total_market_value.setText("$"+formatter.format(totalMV));
                    }
                });

            }
        }); t.start();

    }

    public void averageDividendYield() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = DB.readAllData();
                if (cursor.getCount() == 0) {
                    Toast.makeText(portfolio_V2.this, "No Stocks", Toast.LENGTH_SHORT).show();
                } else {
                    while (cursor.moveToNext()) {
                        average_Dividend_Yield.add(Double.valueOf(cursor.getString(6)));
                    }
                }
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
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Code will be executed on the main thread
                        avgDyview.setText("");
                        avgDyview.setText(avgDY + "%");
                    }
                });

            }
        }); t.start();

    }

    public void totalProfitLoss() {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    total_Profit_Loss.clear();
                    Cursor cursor = DB.readAllData();
                    if (cursor.getCount() == 0) {
                        Toast.makeText(portfolio_V2.this, "No stocks", Toast.LENGTH_SHORT).show();
                    } else {
                        while (cursor.moveToNext()) {
                            total_Profit_Loss.add(Double.valueOf(cursor.getString(4).substring(0, 3)));
                        }
                    }
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
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Code will be executed on the main thread
                            totalProfitLossView.setText("$"+formatter.format(tpl));
                            totalProfitLossView.setTextColor(Color.rgb(80, 200, 120));
                        }
                    });
                }
            });
            t.start();

    }

    public void annualDividend() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                annualDividend=0;
                Cursor cursor = DB.readAllData();
                if (cursor.getCount() == 0) {
                    Toast.makeText(portfolio_V2.this, "No Stocks", Toast.LENGTH_SHORT).show();
                } else {
                    while (cursor.moveToNext()) {
                        float div = Float.parseFloat(cursor.getString(5));
                        float shares = Float.parseFloat(cursor.getString(2));
                        String f = cursor.getString(8);
                        float totaldiv = 0;
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

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    // Send a task to the MessageQueue of the main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Code will be executed on the main thread
                            BigDecimal a = new BigDecimal(annualDividend);
                            BigDecimal b = a.setScale(2, RoundingMode.DOWN);
                            totalAnnualDiv.setText("$"+formatter.format(b));
                        }
                    });
                }

            }
        }); t.start();

    }

    public void reloadActivity () {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void getDataForUpdate() throws InterruptedException, JSONException, IOException {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                symbolU = cursor.getString(0);
                sharesU = Double.parseDouble(cursor.getString(2));
                costbasisU = Double.parseDouble(cursor.getString(3));
                freqU = cursor.getString(8);
                ApiCalls.getOnlyStockPriceUpdadte(symbolU);
                ApiCalls.dividendUpdate(symbolU);
                updatePortfolio();
            }
            dataUpdated = true;

        }
    }

    public void updatePortfolio() throws JSONException, IOException {
        mktvalU = ApiCalls.stockPriceU * sharesU;
        BigDecimal a = new BigDecimal(mktvalU);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        mktvalU = Double.parseDouble(String.valueOf(b));
        ApiCalls.profitLossUpdate(sharesU, costbasisU);
        ApiCalls.getAnnualDividendUpdate(freqU);
        ApiCalls.dividendYieldUpdate(symbolU);
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
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText sy = (EditText) dialog.findViewById(R.id.symbol);
                        EditText shar = (EditText) dialog.findViewById(R.id.Shares_add_stock_popup);
                        EditText cps = (EditText) dialog.findViewById(R.id.average_cost_popup);
                        EditText fr = (EditText) dialog.findViewById(R.id.frequency);
                        String symbol = sy.getText().toString();
                        String sha = shar.getText().toString();
                        double shares = Double.parseDouble(String.valueOf(sha));
                        String ag = cps.getText().toString();
                        double avgCost = Double.parseDouble(String.valueOf(ag));
                        String freqq = fr.getText().toString();
                        DB.addStocksDialog(symbol, shares, avgCost, freqq);
                        dialog.dismiss();
                        reloadActivity();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                });
                return true;
            case R.id.delete2:
                Dialog delete = new Dialog(this);
                delete.setContentView(R.layout.popup_input_dialog_deletestock);
                delete.show();
                Button delete2 = (Button) delete.findViewById(R.id.delete_Stock_popup);
                Button cancel2 = (Button) delete.findViewById(R.id.button_cancel_user_data);
                DBHelper DB2 = new DBHelper(this);
                delete2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        EditText sy = (EditText) delete.findViewById(R.id.symbol);
                        String symbol = sy.getText().toString();
                        DB2.deleteStock(symbol);
                        delete.dismiss();
                        reloadActivity();
                    }
                });

                cancel2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        delete.dismiss();
                    }
                });
                return true;
            case R.id.updateStock2:
                Dialog update = new Dialog(this);
                update.setContentView(R.layout.popup_input_dialog_updatestock);
                update.show();
                Button update2 = (Button) update.findViewById(R.id.update_Stock_popup);
                Button cancel3 = (Button) update.findViewById(R.id.button_cancel_user_data);
                DBHelper DB3 = new DBHelper(this);
                update2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        EditText sym = (EditText) update.findViewById(R.id.symbol);
                        EditText share = (EditText) update.findViewById(R.id.Shares_add_stock_popup);
                        EditText Avg = (EditText) update.findViewById(R.id.average_cost_popup);
                        String symbol2 = sym.getText().toString();
                        String sha = share.getText().toString();
                        String avgg = Avg.getText().toString();
                        double shares = Double.parseDouble(String.valueOf(sha));
                        double aveg = Double.parseDouble(String.valueOf(avgg));
                        DB3.updateStock(symbol2, shares, aveg);
                        update.dismiss();
                        reloadActivity();
                    }
                });
                cancel3.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        update.dismiss();
                    }
                });

                return true;
            default:
                return false;
        }
    }

}