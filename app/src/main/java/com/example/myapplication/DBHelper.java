package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    Database dbj = new Database();
    private Object date_of_dividend;
    private Object amount_of_dividend;
    public DBHelper(Context context) {
        super(context, "Stocks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("Create Table Stocks(symbol Text primary key, shares Text, costBasis Text, " +
                "dividend Text, annualDividend Text, frequency Text, dateOfDividend Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Stocks");
    }

    // inserts a new holding into the database
    public boolean addStock(String symbol, double shares, double costBasis, String frequency) {
        Double annualDividend = 0.0;
        dbj.get_Dividends(symbol);
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("symbol",symbol);
        contentValues.put("shares",shares);
        contentValues.put("costBasis",costBasis);

        //
        double dividend = (double) amount_of_dividend;
        if(frequency == "Monthly") {
            annualDividend = dividend * 12;
        }
        else if (frequency == "Quartely") {
            annualDividend = dividend * 4;
        }
        else if (frequency == "Semi-annually") {
            annualDividend = dividend * 2;
        }
        else {
            annualDividend = dividend;
        }

        String dateOfDividend = (String) date_of_dividend;
        contentValues.put("dividend", dividend);
        contentValues.put("annualDividend",annualDividend);
        contentValues.put("dateOfDividend", dateOfDividend);
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
    public Cursor deleteStock() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from Stocks ", null);
        return cursor;
    }




}


