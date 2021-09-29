package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    Database dbj = new Database();
    ApiCalls api = new ApiCalls();

    public DBHelper(Context context) {
        super(context, "Stocks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("Create Table Stocks(symbol Text primary key,price Text, shares Text, averageCost Text, " +
                "Profit_or_Loss TEXT,dividend Text, Dividend_Yield Text,annualDividend Text, frequency Text, dateOfDividend Text, marketValue Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Stocks");
    }

    // inserts a new holding into the database
    public boolean addStock(String symbol,double shares, double average_cost, String frequency) {

        // calling the method from database class to bring the dividend value in

        Double annualDividend = dbj.annualDividend;
        // getting the dividend amount
        double dividend = api.amount_of_dividend;
        String dateOfDividend = api.date_of_dividend;
        //Getting the price of the stock
        String pric = String.valueOf(api.stock_price);
        double price = Double.parseDouble(pric);
        // getting the market value by multiplying the price by shares
        double marketValue = price * shares;

        // put values in database
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("symbol",symbol);
        contentValues.put("Price", price);
        contentValues.put("shares",shares);
        contentValues.put("averageCost",average_cost);


       // String Dividend_Yield = String.valueOf(dbj.Dividend_Yield);
        contentValues.put("dividend", dividend);
        contentValues.put("annualDividend",annualDividend);
        contentValues.put("dateOfDividend", dateOfDividend);
        contentValues.put("Dividend_Yield", dbj.Dividend_Yield); // this value is still 0.0
        contentValues.put("marketValue", marketValue);
        long results = DB.insert("Stocks",null,contentValues);
        // if the insert method did not work return false
        if (results == -1) {
            return false;
        }
        else{
            return true;
        }
    }

    // updates the shares and costbasis
    public boolean updateStock(String symbol, double shares, double costBasis) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("shares",shares);
        contentValues.put("costBasis",costBasis);
        Cursor cursor = DB.rawQuery("select * from Stocks where name=?", new String[] {symbol});
        if (cursor.getCount()>0){
            long results = DB.update("Stocks",contentValues, "symbol =?", new String[] {symbol});
            // if the update method did not work return false
            if (results == -1) {
                return false;
            }
            else{
                return true;
            }
        }
        else {
            return false;
        }
    }

    // delete the stock out of the database
    public boolean deleteStock(String symbol) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from Stocks where name=?", new String[] {symbol});
        if (cursor.getCount()>0){
            long results = DB.delete("Stocks", "symbol =?", new String[] {symbol});
            // if the delete method did not work return false
            if (results == -1) {
                return false;
            }
            else{
                return true;
            }
        }
        else {
            return false;
        }
    }

    // displays the stock database
    public Cursor showStocks() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from Stocks ", null);
        return cursor;
    }




}


