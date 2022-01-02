package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import soup.neumorphism.NeumorphCardView;

public class portfolio_V2 extends AppCompatActivity implements RecyclerViewInterface {
    RecyclerView recyclerView;
    ArrayList<String> tmv, Symbol, price, profit_loss, average_cost, Dividend_Yield, marketValue, frequency;
    TextView total_market_value, totalProfitLossView, avgDyview, totalAnnualDiv;
    DBHelper DB;
    Button test;
    ApiCalls api = new ApiCalls();
    NeumorphCardView annualDividendCardView,divYieldPerStock,marketValueCardView,ProfitValueCard;
    RecViewAdapter recViewAdapter;
    ArrayList<Double> total_Profit_Loss, average_Dividend_Yield;
    double totalMV, totalProfitLoss, avgDY, annualDividend;

    //for portfolio update
    String symbolU,freqU;
    double sharesU;
    static double mktvalU;
    double costbasisU;
    //--------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_v2);
        setTitle("Portfolio");
        DB = new DBHelper(this);
        //-------------------------------
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
        //--------------------------------------------
        recyclerView = findViewById(R.id.portfolio);
        totalProfitLossView = findViewById(R.id.TPV_View);
        avgDyview = findViewById(R.id.AvgDivYieldView);
        total_market_value = findViewById(R.id.TMVtextV);
        totalAnnualDiv = findViewById(R.id.AnnualDivView);
        annualDividendCardView = findViewById(R.id.AnnualDividendCardView);
        divYieldPerStock = findViewById(R.id.averageDividendYieldCard);
        marketValueCardView = findViewById(R.id.totalMarketValueCard);
        ProfitValueCard = findViewById(R.id.TPV_Card);
        test = findViewById(R.id.test);
        //-----------------------------------------------
        recViewAdapter = new RecViewAdapter(this, Symbol, price, profit_loss, average_cost,
                Dividend_Yield, marketValue, frequency,this);
        recyclerView.setAdapter(recViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalMarketValue();
        totalProfitLoss();
        averageDividendYield();
        storeDataInArrays();
        annualDividend();

        // click listener for annual dividend cardview
        annualDividendCardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(portfolio_V2.this, TotalDividends.class);
                startActivity(intent);
            }
        });
        // click listener for dividend per stock card view
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

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataForUpdate();
                reloadActivity();
            }
        });

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
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No stocks", Toast.LENGTH_SHORT).show();
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
        total_market_value.setText("");
        total_market_value.setText(String.format("$%s", totalMV));
    }

    public void averageDividendYield() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
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
        avgDyview.setText("");
        avgDyview.setText(avgDY + "%");

    }

    public void totalProfitLoss() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No stocks", Toast.LENGTH_SHORT).show();
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
        totalProfitLossView.setText(String.format("$%s", tpl));
    }

    public void annualDividend() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
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
            BigDecimal a = new BigDecimal(annualDividend);
            BigDecimal b = a.setScale(2, RoundingMode.DOWN);
            totalAnnualDiv.setText(String.format("$%s", String.valueOf(b)));
        }
    }

    public void reloadActivity () {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void getDataForUpdate() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                symbolU = cursor.getString(0);
                sharesU = Double.parseDouble(cursor.getString(2));
                costbasisU = Double.parseDouble(cursor.getString(3));
                freqU = cursor.getString(8);
                // i need marketvalue, frequency
                api.price(symbolU);
                api.Dividend(symbolU);


                updatePortfolio();

            }
        }
    }

    public void updatePortfolio() {
        mktvalU = ApiCalls.stock_price * sharesU;
        BigDecimal a = new BigDecimal(mktvalU);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        mktvalU = Double.parseDouble(String.valueOf(b));
        ApiCalls.profit_loss(sharesU, costbasisU);
        ApiCalls.getAnnualDividend(freqU);
        ApiCalls.getDividendYield();


        DB.updateDatabaseStocks(symbolU,sharesU);


    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(portfolio_V2.this,LineChartView.class);
        String s = Symbol.get(position);
        intent.putExtra("Symbol",s);
        startActivity(intent);
    }
}