package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
/*
Created this class to text to see if the database class just has a problem
with calling methods and as I assumed that was the case

this class works with calling methods from another activity
now what next is to make an api class where all the api calls are gonna be made
in. Then Try to make the database class work or make a new database class
only of activity calls to run the methods in another class. Also use
multithreading so this application does not bog down the CPU and cause the application
to freeze. If SQLlite database does not work then look into firebase
*/
public class curious extends AppCompatActivity {
 Button button3,button5;
 TextView textview4,textView5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curious);
        MainActivity mn = new MainActivity();

        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mn.get_dividends("USOI");
               TextView v1 = findViewById(R.id.textView4);
               v1.setText(MainActivity.date_of_dividend);
               TextView v2 = findViewById(R.id.textView5);
               v2.setText(MainActivity.amount_of_dividend);

            }
        });
        button5 = findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mn.pr("aapl");
                TextView v2 = findViewById(R.id.textView5);
                v2.setText(MainActivity.stock_price);
            }
        });
    }
}