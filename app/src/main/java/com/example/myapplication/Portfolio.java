package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;


public class Portfolio extends AppCompatActivity implements OnMenuItemClickListener{
    private PieChart pieChart;
    RecyclerView recyclerView;
    ArrayList<String> tmv,Symbol,price,profit_loss,average_cost, Dividend_Yield,marketValue,frequency;
    TextView total_market_value,totalProfitLossView;
    DBHelper DB;
    RecViewAdapter recViewAdapter;
    ArrayList<String> pieChartMV,pieChartSym;
    ArrayList<PieEntry> products;
    ArrayList<PieModel> mylist = new ArrayList<>();
    ArrayList<Double> total_Profit_Loss;
    double totalMV,totalProfitLoss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curious);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Portfolio");


       //Initializing the arraylist
        DB = new DBHelper(this);
        Symbol = new ArrayList<>();
        price = new ArrayList<>();
        profit_loss = new ArrayList<>();
        average_cost = new ArrayList<>();
        Dividend_Yield = new ArrayList<>();
        marketValue = new ArrayList<>();
        frequency = new ArrayList<>();
        tmv = new ArrayList<>();
        pieChartMV = new ArrayList<>();
        pieChartSym = new ArrayList<>();
        products = new ArrayList<>();
        total_Profit_Loss = new ArrayList<>();

        //Getting the views to use them in the methods
        totalProfitLossView = findViewById(R.id.totalProfitLossView);
        pieChart = findViewById(R.id.PieChart);
        total_market_value = findViewById(R.id.total_market_value);
        recyclerView = findViewById(R.id.portfolio);

        recViewAdapter = new RecViewAdapter(this,Symbol,price,profit_loss,average_cost,
                                            Dividend_Yield,marketValue,frequency);
        recyclerView.setAdapter(recViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        totalMarketValue();
        // method throws and error
       totalProfitLoss();

        storeDataInArrays();
        loadDataForPie();
        for(int i = 0; i < mylist.size(); i++){
            String product = mylist.get(i).getProduct();
            float quantity = Float.parseFloat(mylist.get(i).getQuantity());
            products.add(new PieEntry(quantity,""+product));
        }

        PieDataSet pieDataSet = new PieDataSet(products, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setSelectionShift(1f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("stocks");
        pieChart.animate();

        Button editPortfolio = (Button) findViewById(R.id.editPortfolio);
        editPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(Portfolio.this,view);
                    popup.setOnMenuItemClickListener(Portfolio.this);
                    popup.inflate(R.menu.menu_drop_down);
                    popup.show();
            }
        });


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
        BigDecimal a = new BigDecimal(totalMV);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        totalMV = Double.parseDouble(String.valueOf(b));
        total_market_value.setText(String.format("$%s", totalMV));


    }

    public void loadDataForPie() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                mylist.add(new PieModel(""+cursor.getString(0),""+cursor.getString(10)));
            }
        }

    }

    public void totalProfitLoss () {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                total_Profit_Loss.add(Double.valueOf(cursor.getString(4).substring(0,3)));
            }
        }
        double sum = 0;
        for (int i = 0; i < total_Profit_Loss.size(); i++){
            sum =sum + Double.parseDouble(String.valueOf(total_Profit_Loss.get(i)));
        }
        totalProfitLoss = sum;
        BigDecimal a = new BigDecimal(totalProfitLoss);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        double tpl = Double.parseDouble(String.valueOf(b));
        totalProfitLossView.setText(String.format("$%s", tpl));
    }

    // implement methods to create an overlay so that the user can enter new data
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStock2:
                return true;
            case R.id.delete2:
                return true;
            case R.id.updateStock2:
                return true;
            default:
                return false;
        }
    }
}




