package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class curious extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> tmv,Symbol,price,profit_loss,average_cost, Dividend_Yield,marketValue,frequency;
    TextView total_market_value;
    DBHelper DB;
    RecViewAdapter recViewAdapter;

    double totalMV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curious);
        DB = new DBHelper(this);
        Symbol = new ArrayList<>();
        price = new ArrayList<>();
        profit_loss = new ArrayList<>();
        average_cost = new ArrayList<>();
        Dividend_Yield = new ArrayList<>();
        marketValue = new ArrayList<>();
        frequency = new ArrayList<>();
        tmv = new ArrayList<>();

        total_market_value = findViewById(R.id.total_market_value);
        totalMarketValue();
        recyclerView = findViewById(R.id.portfolio);
        storeDataInArrays();
        recViewAdapter = new RecViewAdapter(this,Symbol,price,profit_loss,average_cost,
                                            Dividend_Yield,marketValue,frequency);
        recyclerView.setAdapter(recViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        total_market_value.setText("$"+String.valueOf(totalMV));

    }






    void storeDataInArrays() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(this,"No Stocks", Toast.LENGTH_SHORT).show();
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

    public void totalMarketValue (){
        Cursor cursor = DB.readAllData();
        if (cursor.getCount()==0) {
            Toast.makeText(this,"No stocks", Toast.LENGTH_SHORT).show();
        }
        else{
            while (cursor.moveToNext()) {
                tmv.add(cursor.getString(10));
            }
        }
        double total=0.0;
        for (int i = 0; i < tmv.size(); i++){
            total = total + Double.parseDouble(tmv.get(i));
            totalMV =  total;
        }


    }





}




