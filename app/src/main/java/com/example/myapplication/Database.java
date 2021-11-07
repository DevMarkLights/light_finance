package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Database extends AppCompatActivity {

    EditText symbol, shares, costBasis, frequency;
    Button addStock, deleteStock, updatestock, viewStocksList;
    DBHelper DB;
    String date_of_dividend;
    double price;
    double amount_of_dividend;
    double average_cost;
    double percentage;
    static String freq;

    ApiCalls api = new ApiCalls();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        symbol = findViewById(R.id.symbol);
        shares = findViewById(R.id.shares);
        costBasis = findViewById(R.id.costBasis);
        frequency = findViewById(R.id.frequency);
        addStock = findViewById(R.id.addStock);
        deleteStock = findViewById(R.id.deleteStock);
        updatestock = findViewById(R.id.updateStock);
        viewStocksList = findViewById(R.id.viewStockList);


        DB = new DBHelper(this);

        // need to create these methods use the link
        // https://www.youtube.com/watch?v=9t8VVWebRFM
        // timestamp -> 18:23

        addStock.setOnClickListener(view -> {
            addStock();
        });

        // to delete a stock out of the database
        deleteStock.setOnClickListener(view -> {
            String symbolTXT = symbol.getText().toString();
            boolean deleteStock = DB.deleteStock(symbolTXT);
            if (deleteStock) {
                Toast.makeText(Database.this, "Stock deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Database.this, "Stock not deleted", Toast.LENGTH_SHORT).show();
            }
        });

        // to update a stock in the database
        updatestock.setOnClickListener(view -> {
            String symbolTXT = symbol.getText().toString();
            String s = shares.getText().toString();
            double sharesTXT = Double.parseDouble(s);
            String c = costBasis.getText().toString();
            double costBasisTXT = Double.parseDouble(c);


            boolean updatestock = DB.updateStock(symbolTXT, sharesTXT, costBasisTXT);
            if (updatestock) {
                Toast.makeText(Database.this, "New stock updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Database.this, "New stock not added", Toast.LENGTH_SHORT).show();
            }
        });

        // shows the stocks in the portfolio in an alert message
        viewStocksList.setOnClickListener(view -> {
            Cursor res = DB.showStocks();
            if (res.getCount() == 0) {
                Toast.makeText(Database.this, "No stocks in portfolio", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuilder buffer = new StringBuilder();
            while (res.moveToNext()) {
                buffer.append("symbol: " + res.getString(0) + "\n");
                buffer.append("price: " + "$" + res.getString(1) + "\n");
                buffer.append("shares: " + res.getString(2) + "\n");
                buffer.append("average cost:" + res.getString(3) + "\n");
                buffer.append("Profit/Loss: " + res.getString(4) + "\n");
                buffer.append("dividend: " + res.getString(5) + "\n");
                buffer.append("Dividend yield: " + res.getString(6) + "%" + "\n");
                buffer.append("annual dividend: " + res.getString(7) + "\n");
                buffer.append("Frequency: " + res.getString(8) + "\n");
                buffer.append("date of dividend: " + res.getString(9) + "\n");
                buffer.append("market Value: " + res.getString(10) + "\n\n");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(Database.this);
            builder.setCancelable(true);
            builder.setTitle("Portfolio");
            builder.setMessage(buffer.toString());
            builder.show();
        });


    }


    public void addStock() {
        String symbolTXT = symbol.getText().toString();
        api.Dividend(symbolTXT);
        api.price(symbolTXT);
        amount_of_dividend = ApiCalls.amount_of_dividend;
        price = ApiCalls.stock_price;
        date_of_dividend = ApiCalls.date_of_dividend;
        String s = shares.getText().toString();
        double sharesTXT = Double.parseDouble(s);
        String fre = frequency.getText().toString();
        api.getAnnualDividend(fre);
        api.getDividendYield();
        // getting the frequency based off user input
        if (fre.startsWith("M")) {
            freq = "Monthly";
        } else if (fre.startsWith("Q")) {
            freq = "Quarterly";
        } else if (fre.startsWith("S")) {
            freq = "Semi-annually";
        } else {
            freq = "annually";
        }
        // converting the given string representation to double value
        String c = costBasis.getText().toString();
        double costBasisTXT = Double.parseDouble(c);
        average_cost = costBasisTXT;
        api.profit_loss(sharesTXT, costBasisTXT);
        percentage = ApiCalls.percentage_profit_loss;

        boolean checkaddstock = DB.addStock(symbolTXT, sharesTXT, average_cost);
        if (checkaddstock) {
            Toast.makeText(Database.this, "New stock added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Database.this, "Stock already in portfolio", Toast.LENGTH_SHORT).show();
        }

    }
}