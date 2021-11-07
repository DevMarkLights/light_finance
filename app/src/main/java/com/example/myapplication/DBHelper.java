package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class DBHelper extends SQLiteOpenHelper {
    ApiCalls api = new ApiCalls();

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

    public boolean addStock(String symbol, double shares, double average_cost) {
        // calling the method from database class to bring the dividend value in
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
        double percentage = ApiCalls.percentage_profit_loss;
        String frequency = Database.freq;
        // getting the frequency based off user input

        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        // putting values
        contentValues.put("symbol", symbol); //0
        contentValues.put("Price", price);   // 1
        contentValues.put("shares", shares); // 2
        contentValues.put("averageCost", average_cost); // 3
        contentValues.put("Profit_or_Loss", ApiCalls.profit_loss + "(" + percentage + "%)"); // 4
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
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("shares", shares);
        contentValues.put("costBasis", costBasis);
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

    // displays the stock database
    public Cursor showStocks() {
        SQLiteDatabase DB = this.getWritableDatabase();

        return DB.rawQuery("select * from Stocks ", null);

    }

    Cursor readAllData() {
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = null;
        if (DB != null) {
            cursor = DB.rawQuery("Select * from Stocks", null);

        }
        return cursor;
    }

    public Cursor Pie() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery("Select symbol,marketValue from Stocks", null);
        }
        return cursor;
    }

    public boolean addStocksDialog(String symbol,double shares,double average_cost,String freqq){
        api.Dividend(symbol);
        api.price(symbol);
        api.getAnnualDividend(freqq);
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
        api.getDividendYield();
        api.profit_loss( shares, average_cost);
        double dy = ApiCalls.Dividend_Yield;
        double percentage = ApiCalls.percentage_profit_loss;
        String frequency = null;
        if (freqq.startsWith("M")) {
            frequency = "Monthly";
        } else if (freqq.startsWith("Q")) {
            frequency = "Quarterly";
        } else if (freqq.startsWith("S")) {
            frequency = "Semi-annually";
        } else {
            frequency = "annually";
        }


        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        // putting values
        contentValues.put("symbol", symbol); //0
        contentValues.put("Price", price);   // 1
        contentValues.put("shares", shares); // 2
        contentValues.put("averageCost", average_cost); // 3
        contentValues.put("Profit_or_Loss", ApiCalls.profit_loss + "(" + percentage + "%)"); // 4
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
}







