package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Database extends AppCompatActivity {
    TextView price;
    Button button2;
    EditText symbol, shares, costBasis, frequency;
    Button addStock, deleteStock, updatestock, viewStocksList;
    DBHelper DB;
    String date_of_dividend,test;
    String amount_of_dividend;
    double annualDividend,Dividend_Yield;
    String price1;

    MainActivity mn = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        symbol = findViewById(R.id.symbol);
        shares = findViewById(R.id.shares);
        costBasis = findViewById(R.id.costBasis);
        price = findViewById(R.id.textView3);

        addStock = findViewById(R.id.addStock);
        deleteStock = findViewById(R.id.deleteStock);
        updatestock = findViewById(R.id.updateStock);
        viewStocksList = findViewById(R.id.viewStockList);
        price = findViewById(R.id.button2);

        DB = new DBHelper(this);

        // need to create these methods use the link
        // https://www.youtube.com/watch?v=9t8VVWebRFM
        // timestamp -> 18:23
        String symbolTXT = symbol.getText().toString();
        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mn.pr(symbolTXT);
                TextView v3 = findViewById(R.id.textView3);
                v3.setText(MainActivity.stock_price);
            }
        });


        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mn.get_dividends(symbolTXT);
                mn.pr(symbolTXT);

                double dividend = Double.parseDouble(mn.amount_of_dividend);
                double price = Double.parseDouble(mn.stock_price);

                String s = shares.getText().toString();
                double sharesTXT = Double.parseDouble(s);

                String fre = frequency.getText().toString();
                fre.toUpperCase(Locale.ROOT);
                     if(fre.startsWith("M")) {
                    annualDividend = dividend * 12;
                    }
                    else if (fre.startsWith("Q")) {
                    annualDividend = dividend * 4;
                     }
                     else if (fre.startsWith("S")) {
                    annualDividend = dividend * 2;
                    }
                     else {
                    annualDividend = dividend;
                    }

                double Dividend_Yield = annualDividend / price;


                // converting the given string representation to double value
                String c = costBasis.getText().toString();
                double costBasisTXT = Double.parseDouble(c);

                // api call for the price
                getPrice(symbolTXT);

                boolean checkaddstock = DB.addStock(symbolTXT, sharesTXT, costBasisTXT, fre);
                if (checkaddstock == true) {
                    Toast.makeText(Database.this, "New stock added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Database.this, "New stock not added", Toast.LENGTH_SHORT).show();
                }

            }

        });

        // to delete a stock out of the database
        deleteStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String symbolTXT = symbol.getText().toString();
                boolean deleteStock = DB.deleteStock(symbolTXT);
                if (deleteStock == true) {
                    Toast.makeText(Database.this, "New stock deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Database.this, "New stock not deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // to update a stock in the database
        updatestock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String symbolTXT = symbol.getText().toString();
                String s = shares.getText().toString();
                double sharesTXT = Double.parseDouble(s);
                String c = costBasis.getText().toString();
                double costBasisTXT = Double.parseDouble(c);

                boolean updatestock = DB.updateStock(symbolTXT, sharesTXT, costBasisTXT);
                if (updatestock == true) {
                    Toast.makeText(Database.this, "New stock updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Database.this, "New stock not added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // shows the stocks in the portfolio in an alert message
        viewStocksList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = DB.showStocks();
                if (res.getCount() == 0) {
                    Toast.makeText(Database.this, "No stocks in portfolio", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("name : " + res.getString(0) + "\n");
                    buffer.append("shares :" + res.getString(1) + "\n");
                    buffer.append("shares :" + res.getString(2) + "\n\n");
                    buffer.append("average cost :" + res.getString(3) + "\n\n\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Database.this);
                builder.setCancelable(true);
                builder.setTitle("Portfolio");
                builder.setMessage(buffer.toString());
                builder.show();
            }
        });
    }


    public void get_Dividends(String view) {
        // api call for the dividend
        try {
            HttpResponse<String> response = Unirest.get("https://yahoofinance-stocks1.p.rapidapi.com/dividends?Symbol=" + view + "&OrderBy=Descending")
                    .header("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                    .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .asString();


            String all = response.getBody();

            String allres = new JSONObject(all).getString("results");

            date_of_dividend = all.substring(43, 53);
            // this line is throwing the index out of bounds error
            amount_of_dividend = all.substring(64, 69);
            TextView v3 = findViewById(R.id.textView3);
            v3.setText(all);

        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }

    }

    public String getPrice(String v) {
        try {
            HttpResponse<String> response = Unirest.get("https://twelve-data1.p.rapidapi.com/price?symbol="+v+"&format=json&outputsize=30")
                    .header("x-rapidapi-host", "twelve-data1.p.rapidapi.com")
                    .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .asString();

            String all = response.getBody();
            String allres = new JSONObject(all).getString("price");
             price1 = allres;
            TextView v3 = findViewById(R.id.textView3);
            v3.setText(price1);
        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }

        return price1 ;
    }
    public String testmethod(String v) {
        try {
            HttpResponse<String> response = Unirest.get("https://yahoofinance-stocks1.p.rapidapi.com/dividends?Symbol=" + v + "&OrderBy=Descending")
                    .header("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                    .header("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                    .asString();


            String all = response.getBody();

           int allres = new JSONObject(all).length();
           // String date_of_dividend = all.substring(10, 20);
            //date_of_dividend = all.substring(43, 53);
            //amount_of_dividend = all.substring(64, 69);


           TextView v3 = findViewById(R.id.textView3);
           v3.setText(allres);
        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }
        return test;
    }
}