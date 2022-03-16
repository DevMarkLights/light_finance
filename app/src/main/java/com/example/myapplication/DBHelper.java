package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Stocks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("Create Table Stocks(symbol Text primary key, price Text, shares Text, averageCost Text, " +
                "Profit_or_Loss Text,dividend Text, Dividend_Yield Text,annualDividend Text, frequency Text, " +
                "dateOfDividend Text, marketValue Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Stocks");
    }

    // inserts a new holding into the database
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean addStock(String symbol, double shares, double average_cost) throws JSONException, IOException {
        // calling the method from database class to bring the dividend value in
        String sym = symbol.toUpperCase();
        ApiCalls.simplifiedDividendForAddingStocks(symbol);
        ApiCalls.profit_loss(shares, average_cost);
        double annualDividend = ApiCalls.annualDividend;
        // getting the dividend amount
        double dividend = ApiCalls.amount_of_dividend;
        String dateOfDividend = ApiCalls.date_of_dividend;
        //Getting the price of the stock
        String pric = String.valueOf(ApiCalls.stock_price);
        double price = Double.parseDouble(pric);
        BigDecimal a = new BigDecimal(price);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        price = Double.parseDouble(String.valueOf(b));
        // getting the market value by multiplying the price by shares
        double mValue = price * shares;
        BigDecimal mva = new BigDecimal(mValue);
        BigDecimal bva = mva.setScale(2,RoundingMode.DOWN);
        double marketValue = Double.parseDouble(String.valueOf(bva));
        // put values in database
        double dy = ApiCalls.Dividend_Yield;
        String frequency = ApiCalls.frequency;
        // getting the frequency based off user input

        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        // putting values
        contentValues.put("symbol", sym); // 0
        contentValues.put("Price", price);   // 1
        contentValues.put("shares", shares); // 2
        contentValues.put("averageCost", average_cost); // 3
        contentValues.put("Profit_or_Loss", ApiCalls.profit_loss); // 4
        contentValues.put("dividend", dividend); // 5
        contentValues.put("Dividend_Yield", dy); // 6
        contentValues.put("annualDividend", annualDividend); // 7
        contentValues.put("frequency", frequency);// 8
        contentValues.put("dateOfDividend", dateOfDividend);// 9
        contentValues.put("marketValue", marketValue); // 10
        //insert values into the database
        long results = DB.insert("Stocks", null, contentValues);
        // if the insert method did not work return false

        DB.close();
        return results != -1;

    }

    // updates the shares and costbasis
    public boolean updateStock(String symbol, double shares, double costBasis) {
        ApiCalls.getOnlyStockPriceForSingleStockUpdating(symbol);
        double mva = shares * ApiCalls.stock_price_for_updating_single_stock;
        ApiCalls.profit_loss(shares,costBasis);
        double pf = ApiCalls.profit_loss;
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("shares", shares);
        contentValues.put("averageCost", costBasis);
        contentValues.put("marketValue",mva);
        contentValues.put("Profit_or_Loss",pf);
        Cursor cursor = DB.rawQuery("select * from Stocks where symbol=?", new String[]{symbol});
        if (cursor.getCount() > 0) {
            long results = DB.update("Stocks", contentValues, "symbol =?", new String[]{symbol});
            // if the update method did not work return false
            if (results == -1) {
                cursor.close();
                return false;

            } else {
                return true;
            }
        } else {

            return false;
        }

    }

    // delete the stock out of the database
    public boolean deleteStock(String symbol) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from Stocks where symbol=?", new String[]{symbol});
        if (cursor.getCount() > 0) {
            long results = DB.delete("Stocks", "symbol =?", new String[]{symbol});
            // if the delete method did not work return false
            if (results == -1) {
                cursor.close();
                return false;
            } else {

                return true;
            }
        } else {

            return false;
        }
    }


    Cursor readAllData() {
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = null;
        if (DB != null) {
            cursor = DB.rawQuery("Select * from Stocks", null);

        }

        return cursor;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean addStocksDialog(String symbol, double shares, double average_cost) throws JSONException, IOException {
        ApiCalls.simplifiedDividendForAddingStocks(symbol);
        String sym = symbol.toUpperCase();
        double annualDividend = ApiCalls.annualDividend;
        double dividend = ApiCalls.amount_of_dividend;
        String dateOfDividend = ApiCalls.date_of_dividend;
        String pric = String.valueOf(ApiCalls.stock_price);
        double price = Double.parseDouble(pric);
        BigDecimal a = new BigDecimal(price);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        price = Double.parseDouble(String.valueOf(b));
        double mValue = price * shares;
        BigDecimal mva = new BigDecimal(mValue);
        BigDecimal bva = mva.setScale(2,RoundingMode.DOWN);
        double marketValue = Double.parseDouble(String.valueOf(bva));
        ApiCalls.profit_loss(shares, average_cost);
        double dy = ApiCalls.Dividend_Yield;

        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        // putting values
        contentValues.put("symbol", sym); //0
        contentValues.put("Price", price);   // 1
        contentValues.put("shares", shares); // 2
        contentValues.put("averageCost", average_cost); // 3
        contentValues.put("Profit_or_Loss", ApiCalls.profit_loss); // 4
        contentValues.put("dividend", dividend); // 5
        contentValues.put("Dividend_Yield", dy); // 6
        contentValues.put("annualDividend", annualDividend); // 7
        contentValues.put("frequency", ApiCalls.frequency);// 8
        contentValues.put("dateOfDividend", dateOfDividend);// 9
        contentValues.put("marketValue", marketValue); // 10
        //insert values into the database
        long results = DB.insert("Stocks", null, contentValues);
        // if the insert method did not work return false

        //DB.close();
        return results != -1;
    }


    public boolean updateDatabaseStocksRegularly(String symbol, double shares) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("shares", shares);
        contentValues.put("price", ApiCalls.stockPriceU);
        contentValues.put("annualDividend", ApiCalls.annualDividendUpdate);
        contentValues.put("Profit_or_Loss",ApiCalls.profitLossU);
        contentValues.put("dividend", ApiCalls.amountOfDividendU);
        contentValues.put("Dividend_Yield", ApiCalls.dividendYieldU);
        contentValues.put("marketValue",portfolio_V2.mktvalU);
        contentValues.put("dateOfDividend", ApiCalls.dateOfDividendU);

        Cursor cursor = DB.rawQuery("select * from Stocks where symbol=?", new String[]{symbol});
        if (cursor.getCount() > 0) {
            long results = DB.update("Stocks", contentValues, "symbol =?", new String[]{symbol});
            // if the update method did not work return false
            if (results == -1) {
                cursor.close();
                return false;

            } else {
                return true;
            }
        } else {

            return false;
        }

    }
}







