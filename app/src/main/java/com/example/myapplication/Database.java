package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

public class Database extends AppCompatActivity {
    EditText symbol, shares, costBasis;
    Button addStock, deleteStock, updatestock, viewStocksList;
    DBHelper DB;
    String date_of_dividend;
    String amount_of_dividend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        symbol = findViewById(R.id.symbol);
        shares = findViewById(R.id.shares);
        costBasis = findViewById(R.id.costBasis);

        addStock = findViewById(R.id.addStock);
        deleteStock = findViewById(R.id.deleteStock);
        updatestock = findViewById(R.id.updateStock);
        viewStocksList = findViewById(R.id.viewStockList);

        String date_of_dividend;
        String amount_of_dividend;
        DB = new DBHelper(this);


        // need to create these methods use the link
        // https://www.youtube.com/watch?v=9t8VVWebRFM
        // timestamp -> 18:23

        deleteStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        // need to create these methods
        updatestock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //need to create these methods
        viewStocksList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void get_Dividends (String view)  {
        Button myButton;
        myButton = addStock;
        myButton.setOnClickListener(this::onClick);
    }

        public void onClick(View view) {
            String symbolTXT = symbol.getText().toString();
            String s = shares.getText().toString();
            // converting the given string representation to double value
            double sharesTXT = Double.parseDouble(s);
            String c = costBasis.getText().toString();
            double costBasisTXT = Double.parseDouble(c);
            try {
                HttpResponse<String> response = Unirest.get("https://yahoofinance-stocks1.p.rapidapi.com/dividends?Symbol=USOI&OrderBy=Descending")
                        .header("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                        .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                        .asString();

                String all = response.getBody();
                String allres = new JSONObject(all).getString("results");
                String date_of_dividend = allres.substring(10,20);
                String am = allres.substring(31,35);
                double amount_of_dividend = Double.parseDouble(am);
            } catch (UnirestException | JSONException e) {
                e.printStackTrace();
            }
        }

}